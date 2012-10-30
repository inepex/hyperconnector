package com.inepex.hyperconnector.dao.bottom;

import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.dao.HyperOperationException;

public interface BottomLevelDao {
	
	List<Cell> select(ScanSpec ss) throws HyperOperationException;
	void insert(List<Cell> cells) throws HyperOperationException;
	String getNamespace();
	String getTableName();
}
