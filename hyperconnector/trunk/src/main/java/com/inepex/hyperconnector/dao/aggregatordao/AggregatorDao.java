package com.inepex.hyperconnector.dao.aggregatordao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ScanSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inepex.hyperconnector.ApplicationDelegate;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;

public class AggregatorDao implements BottomLevelDao{
	
	private static final long defaultPeriodMS = 500;	
	private static final Logger _logger = LoggerFactory
			.getLogger(AggregatorDao.class);

	
	private final BottomLevelDao bottomLevelDao;
	private final ApplicationDelegate applicationDelegate;
	private final ConcurrentLinkedQueue<ThriftCellInsertRequest> inserts = new ConcurrentLinkedQueue<>();
	
	public AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate) {
		this(bottomLevelDao, applicationDelegate, false, defaultPeriodMS);
	}
	
	AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate, boolean isTestDao, long periodMS) {
		this.bottomLevelDao=bottomLevelDao;
		this.applicationDelegate=applicationDelegate;
		
		if(!isTestDao) {
			AggregatorDaoThreadPool.getExecutorService().scheduleWithFixedDelay(new Runnable() {
				
				@Override
				public void run() {
					flushInserts();
				}
			}, periodMS, periodMS, TimeUnit.MILLISECONDS);
			
			applicationDelegate.addShutDownTask(new Runnable() {
				
				@Override
				public void run() {
					flushInserts();
				}
			});
		}
	}
	
	void flushInserts() {
		try {
			Long startMs = System.currentTimeMillis();
			
			List<Cell> realInserts = new ArrayList<>();
			final List<ThriftCellInsertRequest> requests = new ArrayList<>();
			while(true) {
				ThriftCellInsertRequest next = inserts.poll();
				if(next==null)
					break;
				
				realInserts.addAll(next.getCells());
				requests.add(next);
			}
			
			if(!realInserts.isEmpty()) {
				try {
					bottomLevelDao.insert(realInserts, new Runnable() {
						
						@Override
						public void run() {
							submitCallbacks(requests);
						}
					});
					
				} catch (HyperOperationException e) {
					inserts.addAll(requests);
					throw new RuntimeException(e);
				}
				
				if(applicationDelegate!=null)
					applicationDelegate.logInserting(realInserts.size(), System.currentTimeMillis()-startMs);
			}
			
		} catch (Exception e) {
			_logger.error("", e);
		}
	}	

	private void submitCallbacks(List<ThriftCellInsertRequest> requests) {
		for(ThriftCellInsertRequest request : requests){
			if(request.getCallback() != null){
				applicationDelegate.submit(request.getCallback());
			}
		}
	}

	@Override
	public void insert(List<Cell> cells) throws HyperOperationException {
		if(cells!=null && !cells.isEmpty())
			inserts.add(new ThriftCellInsertRequest(cells));
	}
	
	@Override
	public void insert(List<Cell> cells, Runnable callback) throws HyperOperationException {
		if(cells!=null && !cells.isEmpty())
			inserts.add(new ThriftCellInsertRequest(cells, callback));
	}
	
	@Override
	public List<Cell> select(ScanSpec ss) throws HyperOperationException {
		return bottomLevelDao.select(ss);
	}

	@Override
	public String getNamespace() {
		return bottomLevelDao.getNamespace();
	}

	@Override
	public String getTableName() {
		return bottomLevelDao.getTableName();
	}
}
