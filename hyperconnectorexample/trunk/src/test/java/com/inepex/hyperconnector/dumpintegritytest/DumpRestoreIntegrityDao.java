package com.inepex.hyperconnector.dumpintegritytest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.CellInterval;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.example.entity.Ticket;
import com.inepex.example.entity.TicketMapper;
import com.inepex.hyperconnector.dao.HyperDao;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDaoImpl;
import com.inepex.hyperconnector.dao.dumpeddao.DumpedDao;
import com.inepex.hyperconnector.dump.HyperDumpFileProvider;
import com.inepex.hyperconnector.dump.HyperDumper;
import com.inepex.hyperconnector.dump.HyperDumperTestShared;
import com.inepex.hyperconnector.dump.TestHyperDumperDelegate;
import com.inepex.hyperconnector.dump.TestNowProvider;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter.Content;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class DumpRestoreIntegrityDao {
	
	private final String firstDay="2010 9 12 13 42";
	private final String secondDay="2010 9 13 20 31";
	
	private final DumpRestoreIntegrityIO io;
	private final HyperPoolArgs hyperPoolArgs;
	private final DumpRestoreSnapshots snaps;
	private final String testNameSpace;
	private final String testTable;
	private final TestNowProvider nowProvider;
	private final TestHyperDumperDelegate hyperDumperDelegate;
	private final HyperDumper hyperDumper;
	private final InsertDao insertDao;
	private final RestoreDao restoreDao;
	
	public DumpRestoreIntegrityDao(DumpRestoreIntegrityIO io, HyperPoolArgs hyperPoolArgs, DumpRestoreSnapshots snaps, String testNameSpace, String testTable) {
		this.io = io;
		this.snaps=snaps;
		this.hyperPoolArgs = hyperPoolArgs;
		this.testTable=testTable;
		this.testNameSpace=testNameSpace;
		
		nowProvider = new TestNowProvider();
		hyperDumperDelegate=new TestHyperDumperDelegate();
		hyperDumper = new HyperDumper(nowProvider, 
				hyperDumperDelegate,
				DumpRestoreIntegrityIO.dumpFolderName);
		insertDao = new InsertDao();
		restoreDao = new RestoreDao();
	}

	public void makeFirstDayInserts() throws Exception {
		System.out.println("Making first day inserts...");
		nowProvider.setNow(timeOfDay(firstDay));
		
		for(String server : new String[] {"s1.com", "s2.com"}) {
			List<Ticket> reports = new ArrayList<Ticket>(100);
			for(int i=0; i<100; i++)
				reports.add(createTestTicket(server, nowProvider.nowPlusPlus()));
			
			insertDao.insertList(reports);
		}
		
		//~flush
		hyperDumperDelegate.doShutDown();
	}

	public void makeSecondDayInserts() throws Exception {
		System.out.println("Making second day inserts...");
		nowProvider.setNow(timeOfDay(secondDay));
		
		List<Ticket> reports = new ArrayList<Ticket>(34);
		for(int i=0; i<34; i++)
			reports.add(createTestTicket("s3.com", nowProvider.nowPlusPlus()));
		
		insertDao.insertList(reports);
		
		insertDao.deleteByServerTimestampRange("s2.com", Long.MIN_VALUE, Long.MAX_VALUE);
		
		List<Ticket> reports2 = new ArrayList<Ticket>(12);
		for(int i=0; i<12; i++)
			reports2.add(createTestTicket("s2.com", nowProvider.nowPlusPlus()));
		
		insertDao.insertList(reports2);
		
		//~flush
		hyperDumperDelegate.doShutDown();
	}
	
	private Ticket createTestTicket(String server, long timeStamp) {
		Ticket t = HyperDumperTestShared.getTestTicket();
		t.setTimestamp(timeStamp);
		t.setServer(server);
		return t;
	}
	
	private long timeOfDay(String day) {
		String[] parts = day.split(" ");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	public void restoreFirstDay() throws Exception {
		System.out.println("Restoring first day...");
		restoreDao.insertCellList(io.readCells(filterOfDay(firstDay)));
	}
	
	public void restoreSecondDay() throws Exception {
		System.out.println("Restoring second day...");
		restoreDao.insertCellList(io.readCells(filterOfDay(secondDay)));
	}
	
	public void restoreRemovesOfSecondDay() throws Exception {
		System.out.println("Restoring removes of secondDay...");
		restoreDao.insertCellList(io.readCells(
			filterOfDay(secondDay).content(Content.DELETE_ONLY)));
	}
	
	private HyperDumpReaderFilter filterOfDay(String day) {
		String[] parts = day.split(" ");
		return new HyperDumpReaderFilter()
			.yearAndMonth(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))
			.day(Integer.parseInt(parts[2]));
	}

	public void makeSnapshot(String name) throws Exception {
		snaps.storeSnapshot(name, restoreDao.selectAllAsCells());
	}
	
	public void makeSecondDaySnapshot(String name) throws Exception {
		long secDayCreationTimeStamp = timeOfDay(secondDay);
		List<Cell> secondDayCells = restoreDao.selectAsCell(getScanSpec_ByServerTimestampRange("s1.com", "s9.com", secDayCreationTimeStamp, secDayCreationTimeStamp+1000));
		snaps.storeSnapshot(name, secondDayCells);
	}
	
	private class InsertDao extends HyperDao<Ticket> {

		public InsertDao() {
			super(new DumpedDao(hyperDumper,new BottomLevelDaoImpl(hyperPoolArgs, testNameSpace, testTable))
					, new TicketMapper());
		}

		public void deleteByServerTimestampRange(String server, long minValue, long maxValue) throws HyperOperationException {
			delete(getScanSpec_ByServerTimestampRange(server, server, minValue, maxValue));
		}
	}
	
	private class RestoreDao extends HyperDao<Ticket> {

		public RestoreDao() {
			super(new BottomLevelDaoImpl(hyperPoolArgs, testNameSpace, testTable), new TicketMapper());
		}
		
		public List<Cell> selectAsCell(ScanSpec ss) throws HyperOperationException {
			return selectAsCells(ss);
		}

		public void insertCellList(List<Cell> cells) throws HyperOperationException {
			super.insert(cells);
		}
		
		public List<Cell> selectAllAsCells() throws HyperOperationException {
			return selectAsCells(getScanSpec_All());
		}
	}
	
	public static ScanSpec getScanSpec_ByServerTimestampRange(String server_start, String server_end, long timestamp_start, long timestamp_end) {
		ScanSpec ss = new ScanSpec();
		
		List<CellInterval> cis = new ArrayList<CellInterval>();
		CellInterval ci = new CellInterval();

		ci.setStart_row(server_start);
		ci.setEnd_row(server_end);
		ci.setStart_inclusive(true);
		ci.setEnd_inclusive(true);
		ci.setStart_column("description");
		ci.setEnd_column("title");
		cis.add(ci);
		
		ss.cell_intervals = cis;
		
		//add the necessary columns
		ss.columns = new ArrayList<String>();
		ss.columns.add("description");
		ss.columns.add("isFixed");
		ss.columns.add("priority");
		ss.columns.add("problemtype");
		ss.columns.add("title");
		
		//set start and end timestamp
		ss.setStart_time(timestamp_start);
		ss.setEnd_time(timestamp_end);
		
		return ss;
	}
}
