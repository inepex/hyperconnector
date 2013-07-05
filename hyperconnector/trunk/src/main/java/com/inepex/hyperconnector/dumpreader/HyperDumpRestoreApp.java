package com.inepex.hyperconnector.dumpreader;

import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDaoImpl;
import com.inepex.hyperconnector.dumpreader.HyperDumpReader.FileContent;
import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;


public class HyperDumpRestoreApp {

	//temporaly stored
	private static String year=null;
	private static String month=null;
	
	//mandatory params
	private static String hyperAddress=null;
	private static Integer hyperPort=null;
	private static String baseFolder=null;
	private static String table=null;
	private static String nameSpace=null;
	
	//optional params
	private static HyperDumpReaderFilter filter = new HyperDumpReaderFilter();
	
	//hyper resources
	private static HyperPoolArgs hyperPoolArgs;
	private static BottomLevelDaoImpl daoImpl;
	
	public static void main(String[] args) {
		if(args==null || args.length==0) {
			printHelp();
			System.exit(-1);
		}
		
		if("help".equals(args[0])) {
			printHelp();
			System.exit(0);
		}
		
		System.out.println("Parsing params....");
		parseParams(args);
		checkMandatories();
		
		System.out.println("Connection to hypertable...");
		createHyperResources();
		
		System.out.println("Inserting cells...");
		long cellInsertedSum=0;
		try {
			for(FileContent fc : new HyperDumpReader(baseFolder, filter)) {
				System.out.print(fc.getPath()+": "+fc.getContent().size()+" cell in it.... ");
				cellInsertedSum+=fc.getContent().size();
				daoImpl.insert(fc.getContent());
				if(!fc.hasException())
					System.out.println("  Ok!");
				else {
					System.out.println(" Corrupt file:");
					fc.getReadException().printStackTrace(System.out);
				}
			}
			
			System.out.println("\nFinished ("+cellInsertedSum+" cells)------------------------------------");
		} catch (HyperOperationException ex) {
			ex.printStackTrace();
		} finally {
			closeConnections();
		}
		
	}
	
	private static void closeConnections() {
		hyperPoolArgs.getHyperHqlServicePool().destroyAllResource();
		hyperPoolArgs.getHyperClientPool().destroyAllResource();
	}

	private static void createHyperResources() {
		HyperClientPool hyperClientPool = new HyperClientPool("localhost", 38080);
		HyperHqlServicePool hyperHqlServicePool = new HyperHqlServicePool("localhost", 38080);
		
		hyperPoolArgs = new HyperPoolArgs(hyperClientPool, 
				hyperHqlServicePool, 
				10, 
				3, 
				3, 
				0, 
				0);
		
		daoImpl=new BottomLevelDaoImpl(hyperPoolArgs, nameSpace, table);
	}

	private static void printHelp() {
		System.out.println(
				"HyperDumpRestoreApp\n" +
				"-----------------------------\n" +
				"\n" +
				
				"Mandatory params:\n" +
				"\t hyperAddress - url or ip of target hypertable \n" +
				"\t hyperPort - port of hypertable \n"+
				"\t baseFolder - base folder of hyper dump hierarchy \n" +
				"\t nameSpace - name of namespace \n" +
				"\t table - name of table \n" +
				"\n" +
				
				"Optional params (default value is every): \n" +
				"\t year - number of [1999,infinity) if you set year you must set month too \n" +
				"\t month - number of [1,12] if you set month you must set year too \n" +
				"\t day - number of [1,31] OR number range from of [1,31] inclusive to of [2,32] exclusive like: 1-15 or 1-32\n" +
				"\t hour - number of [0, 23] OR number range from of [0, 23] inclusive to of [1, 24] exclusive like: 2-14 or 0-24\n" +
				"\t content - one of: DELETE_ONLY, INSERT_ONLY, BOTH \n" +
				"\n" +
				
				"minimal params to invoke :\n" +
				"\tHyperDumpRestoreApp hyperAddress=beta-app.inetrack.com hyperAddress=localhost hyperPort=38080 baseFolder=/backup/hyperDump/ nameSpace=InepexLbs table=Report\n\n"+
				"sample invoke :\n" +
				"\tHyperDumpRestoreApp hyperAddress=beta-app.inetrack.com hyperAddress=localhost hyperPort=38080 baseFolder=/backup/hyperDump/ nameSpace=InepexLbs table=Report year=2012 month=12 day=12-25 content=BOTH\n"
		);
	}
	
	private static void checkMandatories() {
		if(hyperAddress==null)
			mustBeSet("hyperAddress");
		if(hyperPort==null)
			mustBeSet("hyperPort");
		if(baseFolder==null)
			mustBeSet("baseFolder");
		if(table==null)
			mustBeSet("table");
		if(nameSpace==null)
			mustBeSet("nameSpace");
	}

	private static void parseParams(String[] args) {
		for(String param : args) {
			if(param.indexOf('=')==-1) {
				invalidParam(param);
			}
			
			String[] splittedParam=param.split("=");
			String name = splittedParam[0];
			String value = splittedParam[1];
			
			if("hyperAddress".equals(name)) {
				hyperAddress=value;
				
			} else if("hyperPort".equals(name)) {
				hyperPort=Integer.parseInt(value);
				
			} else if("baseFolder".equals(name)) {
				baseFolder=value;
				
			} else if("table".equals(name)) {
				table=value;
				filter.table(value);
				
			} else if("nameSpace".equals(name)) {
				nameSpace=value;
				filter.nameSpace(value);
				
			} else if("year".equals(name)) {
				year=value;
				
			} else if("month".equals(name)) {
				month=value;
				
			} else if("day".equals(name)) {
				if(value.indexOf('-')!=-1) {
					String[] splittedVal=value.split("-");
					filter.day(Integer.parseInt(splittedVal[0]), Integer.parseInt(splittedVal[1]));
				} else {
					filter.day(Integer.parseInt(value));
				}
				
			} else if("hour".equals(name)) {
				if(value.indexOf('-')!=-1) {
					String[] splittedVal=value.split("-");
					filter.hour(Integer.parseInt(splittedVal[0]), Integer.parseInt(splittedVal[1]));
				} else {
					filter.hour(Integer.parseInt(value));
				}
				
			} else if("content".equals(name)) {
				filter.content(HyperDumpReaderFilter.Content.valueOf(value));
				
			} else {
				paramNameMistyped(name);
			}
		}
		
		if(year!=null && month!=null) {
			filter.yearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
		} else {
			if(year!=null && month!=null) {
				invalidYearMonth();
			}
		}
	}
	
	private static void mustBeSet(String name) {
		System.err.println("Param "+name+" must be set!");
		System.err.println();
		printHelp();
		System.exit(-1);
	}

	private static void invalidYearMonth() {
		System.err.println("You must set both year and month params too. Or neither.");
		System.err.println();
		printHelp();
		System.exit(-1);
	}

	private static void paramNameMistyped(String name) {
		System.err.println("Invalid param name: " + name);
		System.err.println();
		printHelp();
		System.exit(-1);
	}

	private static void invalidParam(String param) {
		System.err.println("Invalid param: "+param+" The right param format is: name=value");
		System.err.println();
		printHelp();
		System.exit(-1);
	}
	
}
