package com.inepex.hyperconnector.dao.dumpeddao;

import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;
import com.inepex.hyperconnector.dump.HyperDumper;

public class DumpedDao implements BottomLevelDao{

	private final HyperDumper hyperDumper;
	private final BottomLevelDao bottomLevelDao;
	
	public DumpedDao(HyperDumper hyperDumper, BottomLevelDao bottomLevelDao) {
		this.hyperDumper=hyperDumper;
		this.bottomLevelDao=bottomLevelDao;
	}
	
	@Override
	public void insert(List<Cell> cells) throws HyperOperationException {
		hyperDumper.dumpCells(cells, bottomLevelDao.getNamespace(), bottomLevelDao.getTableName());
		bottomLevelDao.insert(cells);
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

	@Override
	public void insert(List<Cell> cells, Runnable cbk) throws HyperOperationException {
		hyperDumper.dumpCells(cells, bottomLevelDao.getNamespace(), bottomLevelDao.getTableName());
		bottomLevelDao.insert(cells, cbk);
	}

}
