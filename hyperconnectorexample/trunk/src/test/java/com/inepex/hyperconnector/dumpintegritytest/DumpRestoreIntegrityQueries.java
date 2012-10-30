package com.inepex.hyperconnector.dumpintegritytest;

import java.util.List;

import org.hypertable.thriftgen.HqlService;
import org.hypertable.thriftgen.NamespaceListing;

import com.inepex.hyperconnector.thrift.HyperHqlServiceConnection;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class DumpRestoreIntegrityQueries {
	
	private final String testNameSpace; 
	private final String testTable;
	private final HyperPoolArgs hyperPoolArgs;
	
	public DumpRestoreIntegrityQueries(HyperPoolArgs hyperPoolArgs, String testNameSpace, String testTable) {
		this.hyperPoolArgs=hyperPoolArgs;
		this.testNameSpace=testNameSpace;
		this.testTable=testTable;
	}
	
	public void failOnNonBetaAppHyper() throws Exception {
		System.out.println("Check hypertable if is it a test database...");
		HyperHqlServiceConnection conn = getAHqlConnection();
		
		HqlService.Client client = conn.getClient();
		long nsid = client.open_namespace("/");
		List<NamespaceListing> nameSpaces = client.get_listing(nsid);
		
		StringBuffer getListingResult = new StringBuffer();
		getListingResult.append("Get liting of \"/\":\n");
		for(NamespaceListing ns : nameSpaces) {
			getListingResult.append("\t");
			getListingResult.append(ns.name);
			if(ns.is_namespace) {
				getListingResult.append(" (namespace)");
			}
			getListingResult.append("\n");
		}
		System.out.println(getListingResult.toString());
		
		
		boolean found = false;
		for(NamespaceListing ns : nameSpaces) {
			if(ns.is_namespace && testNameSpace.equals(ns.name)) {
				found=true;
				break;
			}
		}
		
		returnResource(conn);
		
		if(!found) {
			throw new Error("There is no namespace called "+testNameSpace+"! You must use the hypertable of beta-app!!!");
		}
	}

	public void dropTestTableIfExists() throws Exception {
		System.out.println("Droping test table if exists...");
		HyperHqlServiceConnection conn = getAHqlConnection();
		
		HqlService.Client client = conn.getClient();
		long nsid = client.open_namespace(testNameSpace);
		client.drop_table(nsid, testTable, true);
		returnResource(conn);
	}

	public void createTestTable() throws Exception {
		System.out.println("Creating test table...");
		HyperHqlServiceConnection conn = getAHqlConnection();
		
		HqlService.Client client = conn.getClient();
		long nsid = client.open_namespace(testNameSpace);
		client.hql_query(nsid, "create table "+testTable+" ( description, isFixed, priority, problemtype, title ) ");
		returnResource(conn);
	}
	
	private HyperHqlServiceConnection getAHqlConnection() throws Exception {
		 return hyperPoolArgs.getHyperHqlServicePool().getResource(hyperPoolArgs.getMaxWaitMillis(), 
				hyperPoolArgs.getTryGetCount(), 
				hyperPoolArgs.getTryCreateCount());	
	}
	
	private void returnResource(HyperHqlServiceConnection conn) {
		hyperPoolArgs.getHyperHqlServicePool().returnResource(conn);
	}
}
