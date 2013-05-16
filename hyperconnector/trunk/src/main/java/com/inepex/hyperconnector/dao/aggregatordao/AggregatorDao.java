package com.inepex.hyperconnector.dao.aggregatordao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.ApplicationDelegate;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;

public class AggregatorDao implements BottomLevelDao{

	private static final long periodMS = 500;
	
	private final BottomLevelDao bottomLevelDao;
	private final ConcurrentLinkedQueue<List<Cell>> inserts = new ConcurrentLinkedQueue<>();
	
	static AggregatorDao createTestDao(BottomLevelDao bottomLevelDao) {
		return new AggregatorDao(bottomLevelDao, null, true);
	}
	
	public AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate) {
		this(bottomLevelDao, applicationDelegate, false);
	}
	
	private AggregatorDao(BottomLevelDao bottomLevelDao, ApplicationDelegate applicationDelegate, boolean isTestDao) {
		this.bottomLevelDao=bottomLevelDao;
		
		if(!isTestDao) {
			AggregatorDaoPool.getService().scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					flushInserts();
				}
			}, new Random().nextInt((int)periodMS) + periodMS, periodMS, TimeUnit.MILLISECONDS);
			
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
