package com.inepex.hyperconnector.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.CellInterval;
import org.hypertable.thriftgen.KeyFlag;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.dao.bottom.BottomLevelDao;
import com.inepex.hyperconnector.mapper.HyperMapperBase;
import com.inepex.hyperconnector.mapper.HyperMappingException;

public class HyperDao<T> {

	private static final byte[] deletedCellValue = new byte[0];
	
	private final BottomLevelDao bottomDao;
	private final HyperMapperBase<T> mapper;
	
	public HyperDao(BottomLevelDao bottomDao, HyperMapperBase<T> mapper) {
		this.bottomDao=bottomDao;
		this.mapper=mapper;
	}

	protected final void insert(Cell cell) throws HyperOperationException {
		if (cell != null)
			bottomDao.insert(Arrays.asList(cell));
	}
	
	protected final void insert(List<Cell> cells) throws HyperOperationException {
		if (cells != null)
			bottomDao.insert(cells);
	}

	protected final void delete(ScanSpec ss) throws HyperOperationException {
		List<Cell> cells = bottomDao.select(ss);
		if (cells != null) {
			for (Cell cell : cells)
				if (cell != null && cell.getKey() != null) {
					cell.getKey().setFlag(KeyFlag.DELETE_CELL_VERSION);
					cell.setValue(deletedCellValue);
				}
			bottomDao.insert(cells);
		}
	}
	
	public final void deleteAll() throws HyperOperationException {
		delete(getScanSpec_All());
	}
	
	protected final ScanSpec getScanSpec_All() {
		ScanSpec ss = new ScanSpec();
		List<CellInterval> cis = new ArrayList<CellInterval>();
		ss.cell_intervals = cis;
		return ss;
	}
	
	public final void insert(T hyperEntity) throws HyperOperationException, HyperMappingException {
		if(mapper.getIsSingleCell())
			insert(mapper.hyperEntityToCell(hyperEntity));
		else
			bottomDao.insert(mapper.hyperEntityToCellList(hyperEntity));
	}

	public final void insertList(List<T> hyperEntityList) throws HyperOperationException {
		bottomDao.insert(mapper.hyperEntityListToCellList(hyperEntityList));
	}
	
	public final List<T> select(ScanSpec ss) throws HyperOperationException {
		return mapper.cellListToHyperEntityList(
				bottomDao.select(ss));
	}
	
	protected final List<Cell> selectAsCells(ScanSpec ss) throws HyperOperationException {
		return bottomDao.select(ss);
	}

	public final List<T> selectAll() throws HyperOperationException {
		return select(getScanSpec_All());
	}
}
