package com.inepex.example.entity;

import java.util.ArrayList;
import java.util.List;

import org.hypertable.thriftgen.CellInterval;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.dao.HyperDao;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDaoImpl;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class TicketDao extends HyperDao<Ticket> {
	
	public static final String namespace = "TestUserSupportNameSpace";
	public static final String table = "Ticket";

	protected static final String column_description = "description";
	protected static final String column_isFixed = "isFixed";
	protected static final String column_priority = "priority";
	protected static final String column_problemtype = "problemtype";
	protected static final String column_title = "title";

	protected static final String firstColumn = "description";
	protected static final String lastColumn = "title";

	public TicketDao(HyperPoolArgs hyperClientPoolArgs) {
		super(new BottomLevelDaoImpl(hyperClientPoolArgs, namespace, table),
				new TicketMapper());
	}
	
	public static ScanSpec getScanSpec_ByServerTimestampRange(String server, long timestamp_start, long timestamp_end) {
		ScanSpec ss = new ScanSpec();
		
		List<CellInterval> cis = new ArrayList<CellInterval>();
		CellInterval ci = new CellInterval();

		//select all of the user's ticket
		String rk_start = server;
		String rk_end = server;
		ci.setStart_row(rk_start);
		ci.setEnd_row(rk_end);
		ci.setStart_inclusive(true);
		ci.setEnd_inclusive(true);
		ci.setStart_column(firstColumn);
		ci.setEnd_column(lastColumn);
		
		cis.add(ci);
		
		ss.cell_intervals = cis;
		
		//add the necessary columns
		ss.columns = new ArrayList<String>();
		ss.columns.add(column_description);
		ss.columns.add(column_isFixed);
		ss.columns.add(column_priority);
		ss.columns.add(column_problemtype);
		ss.columns.add(column_title);
		
		//set start and end timestamp
		ss.setStart_time(timestamp_start);
		ss.setEnd_time(timestamp_end);
		
		return ss;
	}
	
	public List<Ticket> selectByServerTimestampRange(String server, long timestamp_start, long timestamp_end) throws HyperOperationException {
		return select(getScanSpec_ByServerTimestampRange(server, timestamp_start, timestamp_end));
	}
	
	public void deleteByServerTimestampRange(String server, long timestamp_start, long timestamp_end) throws HyperOperationException {
		delete(getScanSpec_ByServerTimestampRange(server, timestamp_start, timestamp_end), null);
	}

}