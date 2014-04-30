package com.inepex.hyperconnector.dao.aggregatordao;

import java.util.List;

import org.hypertable.thriftgen.Cell;

public class ThriftCellInsertRequest {
	
	private final List<Cell> cells;
	private final Runnable callback;
	
	public ThriftCellInsertRequest(List<Cell> cells,
								   Runnable callback) {
		this.cells = cells;
		this.callback = callback;
	}
	
	public ThriftCellInsertRequest(List<Cell> cells) {
		this.cells = cells;
		this.callback = null;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public Runnable getCallback() {
		return callback;
	}
}
