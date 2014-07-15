package com.inepex.hyperconnector.dumpintegritytest;

import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

/**
 * By running this test use your own hypertable or hypertable of a test server.
 * 
 * Before you run, make a namespace called "DumpRestoreIntegrityTest".
 * 
 * It takes SEVERAL MINUTES depending on the speed of the network connection between your PC and hypertable.
 * 
 * @author sebi
 */
public class DumpRestoreIntegrityTest {
	
	public static void main(String[] args) throws Exception {
		new DumpRestoreIntegrityTest().doTest();
	}
	
	private final String testNameSpace = "DumpRestoreIntegrityTest";
	private final String testTable = "TicketTestTable";
	
	private final String origFirstDay="origFirstDay";
	private final String origSecondDay="origSecondDay";
	private final String origFull="origFull";
	
	private final String dbdFirstDay="dbdFirstDay";
	private final String dbdFull="dbdFull";
	
	private final String backwardsSecondDay="backSecondDay";
	private final String backwardsFull="backFull";
	
	private final HyperPoolArgs hyperPoolArgs;
	private final DumpRestoreIntegrityIO io;
	private final DumpRestoreIntegrityDao dao;
	private final DumpRestoreIntegrityQueries queries;
	private final DumpRestoreSnapshots snapshots;
	
	public DumpRestoreIntegrityTest() {
		HyperClientPool hyperClientPool = new HyperClientPool("localhost", 38080);
		HyperHqlServicePool hyperHqlServicePool = new HyperHqlServicePool("localhost", 38080);
		
		hyperPoolArgs = new HyperPoolArgs(hyperClientPool, 
				hyperHqlServicePool, 
				10, 
				3, 
				3, 
				0, 
				0);
		
		io=new DumpRestoreIntegrityIO();
		snapshots=new DumpRestoreSnapshots();
		dao=new DumpRestoreIntegrityDao(io, hyperPoolArgs, snapshots, testNameSpace, testTable);
		queries=new DumpRestoreIntegrityQueries(hyperPoolArgs, testNameSpace, testTable);
	}
	
	public void doTest() throws Exception {
		try {
			init();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//simulating usage
		dao.makeFirstDayInserts();
		dao.makeSnapshot(origFirstDay);
		dao.makeSecondDayInserts();
		dao.makeSecondDaySnapshot(origSecondDay);
		dao.makeSnapshot(origFull);
		
		//day by day restore
		queries.dropTestTableIfExists();
		queries.createTestTable();
		dao.restoreFirstDay();
		dao.makeSnapshot(dbdFirstDay);
		snapshots.difference(origFirstDay, dbdFirstDay);
		dao.restoreSecondDay();
		dao.makeSnapshot(dbdFull);
		snapshots.difference(origFull, dbdFull);
		
		//backward order restore
		queries.dropTestTableIfExists();
		queries.createTestTable();
		dao.restoreSecondDay();
		dao.makeSnapshot(backwardsSecondDay);
		snapshots.difference(origSecondDay, backwardsSecondDay);
		dao.restoreFirstDay();
		dao.restoreRemovesOfSecondDay();
		dao.makeSnapshot(backwardsFull);
		snapshots.difference(origFull, backwardsFull);
		
		cleanupAfterTest();
		System.out.println("---------------SUCCESSFUL TEST!---------------");
	}	

	private void init() throws Exception {
		queries.failOnNonBetaAppHyper();
		queries.dropTestTableIfExists();
		queries.createTestTable();
		io.deleteDumpFolderIfExists();
	}
	
	private void cleanupAfterTest() throws Exception {
		queries.dropTestTableIfExists();
		io.deleteDumpFolderIfExists();
		hyperPoolArgs.getHyperClientPool().destroyAllResource();
		hyperPoolArgs.getHyperHqlServicePool().destroyAllResource();
	}
}
