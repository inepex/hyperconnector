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
	private final ConcurrentLinkedQueue<List<Cell>> inserts = new ConcurrentLinkedQueue<>();
	
	public AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate) {
		this(bottomLevelDao, applicationDelegate, false, defaultPeriodMS);
	}
	
	AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate, boolean isTestDao, long periodMS) {
		this.bottomLevelDao=bottomLevelDao;
		
		if(!isTestDao) {
			AggregatorDaoPool.getService().scheduleWithFixedDelay(new Runnable() {
				
				@Override
				public void run() {
					try {
					flushInserts();
					} catch (Exception e) {
						_logger.error("", e);
					}
					
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
		List<Cell> realInserts = new ArrayList<>();
		while(true) {
			List<Cell> next = inserts.poll();
			if(next==null)
				break;
			
			realInserts.addAll(next);
		}
		
		if(!realInserts.isEmpty()) {
			try {
				bottomLevelDao.insert(realInserts);
			} catch (HyperOperationException e) {
				inserts.add(realInserts);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void insert(List<Cell> cells) throws HyperOperationException {
		if(cells!=null && !cells.isEmpty())
			inserts.add(cells);
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
