package com.inepex.example.dao;

import java.util.ArrayList;
import java.util.List;

import org.hypertable.thriftgen.CellInterval;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.example.Ticket;
import com.inepex.example.mapper.TicketMapper;
import com.inepex.hyperconnector.dao.HyperDaoBase;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.mapper.HyperMappingException;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class TicketDao extends HyperDaoBase {
	public static final String namespace = "UserSupport";
	public static final String table = "Ticket";

	protected static final String column_description = "description";
	protected static final String column_isFixed = "isFixed";
	protected static final String column_priority = "priority";
	protected static final String column_problemtype = "problemtype";
	protected static final String column_title = "title";

	protected static final String firstColumn = "description";
	protected static final String lastColumn = "title";

	protected TicketMapper mapper = new TicketMapper();

	public TicketDao(HyperPoolArgs hyperClientPoolArgs) {
		super(hyperClientPoolArgs);
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getTable() {
		return table;
	}

	public void insert(Ticket hyperEntity) throws HyperOperationException,
			HyperMappingException {
		insert(mapper.hyperEntityToCellList(hyperEntity));
	}

	public void insertList(List<Ticket> hyperEntityList)
			throws HyperOperationException {
		insert(mapper.hyperEntityListToCellList(hyperEntityList));
	}

	private ScanSpec getScanSpec_All() {
		ScanSpec ss = new ScanSpec();
		List<CellInterval> cis = new ArrayList<CellInterval>();
		ss.cell_intervals = cis;
		return ss;
	}

	public List<Ticket> selectAll() throws HyperOperationException {
		return mapper.cellListToHyperEntityList(select(getScanSpec_All()));
	}

	public void deleteAll() throws HyperOperationException {
		delete(getScanSpec_All());
	}

}