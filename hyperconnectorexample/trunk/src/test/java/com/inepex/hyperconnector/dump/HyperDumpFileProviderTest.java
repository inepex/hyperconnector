package com.inepex.hyperconnector.dump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import junit.framework.Assert;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;
import org.junit.Test;
import org.mockito.Mockito;

import com.inepex.hyperconnector.ApplicationDelegate;

public class HyperDumpFileProviderTest {
	
	@Test
	public void testThowsIOExceptionOnRemovedDumpfile() {
		TestNowProvider nowProvider= new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 21, 19);
		nowProvider.setNow(cal.getTimeInMillis());
		
		HyperDumpFileProvider prov = new HyperDumpFileProvider(
				nowProvider,
				Mockito.mock(ApplicationDelegate.class),
				"test_dump");

		//cleanup
		HyperDumperTestShared.deleteDir(new File(prov.getBaseDumpFolder()));
		
		try {
			TBinaryProtocol prot = prov.getStreamFor("namespace", "tableName", false);
			new Cell(new Key("a", "a", "a", null)).write(prot);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	
		HyperDumperTestShared.deleteDir(new File(prov.getBaseDumpFolder()));
		
		try {
			TBinaryProtocol prot = prov.getStreamFor("namespace", "tableName", false);
			new Cell(new Key("a", "a", "a", null)).write(prot);
			//should throw exceptions
			Assert.fail();
		} catch (TException | IOException e) {
			//ok
		}
		
		prov.closeAllFile();
		
		HyperDumperTestShared.deleteDir(new File(prov.getBaseDumpFolder()));
	}
	
	
	@Test
	public void testScheduling() {
		TestNowProvider nowProvider= new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 21, 19);
		nowProvider.setNow(cal.getTimeInMillis());
		
		HyperDumpFileProvider prov = new HyperDumpFileProvider(
				nowProvider,
				Mockito.mock(ApplicationDelegate.class),
				"test_dump");
		
		Calendar startTime = Calendar.getInstance();
		startTime.setTimeInMillis(prov.delayUntilFirstClosingStart()+cal.getTimeInMillis());
		startTime.setTimeZone(HyperDumpFileProvider.UTC);
		assertEquals(1999, startTime.get(Calendar.YEAR));
		assertEquals(11, startTime.get(Calendar.MONTH));
		assertEquals(20, startTime.get(Calendar.DAY_OF_MONTH));
		assertEquals(22, startTime.get(Calendar.HOUR_OF_DAY));
		assertEquals(5, startTime.get(Calendar.MINUTE));
	}
	
	@Test
	public void testFileName() {
		TestNowProvider nowProvider= new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 21, 19);
		nowProvider.setNow(cal.getTimeInMillis());
		
		HyperDumpFileProvider prov = new HyperDumpFileProvider(
				nowProvider,
				Mockito.mock(ApplicationDelegate.class),
				"test_dump");
		String path1 = prov.getBaseDumpFolder()+"/NameSpace/Table/1999-12/20/21_UTC.dump.snappy";
		String path2 = prov.getDumpFileName(cal.getTimeInMillis(), "NameSpace", "Table", false);
		assertEquals(path1.replace("/", "#").replace("\\", "#"), path2.replace("/", "#").replace("\\", "#"));
		path1 = prov.getBaseDumpFolder()+"/NameSpace/Table/1999-12/20/21_UTC.del.dump.snappy";
		path2 = prov.getDumpFileName(cal.getTimeInMillis(), "NameSpace", "Table", true);
		assertEquals(path1.replace("/", "#").replace("\\", "#"), path2.replace("/", "#").replace("\\", "#"));
	}
	
	@Test
	public void testClosings() throws IOException {
		TestNowProvider nowProv = new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 13, 19);
		nowProv.setNow(cal.getTimeInMillis());
		
		HyperDumpFileProvider prov = new HyperDumpFileProvider(
				nowProv,
				Mockito.mock(ApplicationDelegate.class),
				"test_dump");
		String namespace="namespace";
		String tableName="tablename";
		
		prov.getStreamFor(namespace, tableName+"1", false);
		prov.getStreamFor(namespace, tableName+"2", false);
		assertEquals(1, prov.hourlySize());
		assertEquals(2, prov.openedCountForNow());
		nowProv.setOneHourLater();
		
		prov.getStreamFor(namespace, tableName+"1", false);
		prov.getStreamFor(namespace, tableName+"2", false);
		assertEquals(2, prov.hourlySize());
		assertEquals(2, prov.openedCountForNow());
		
		prov.closeOldFiles();
		assertEquals(1, prov.hourlySize());
		assertEquals(2, prov.openedCountForNow());
		
		nowProv.setOneHourLater();
		prov.closeOldFiles();
		assertEquals(0, prov.hourlySize());
		assertEquals(0, prov.openedCountForNow());
		
		assertTrue(HyperDumperTestShared.deleteDir(new File(prov.getBaseDumpFolder())));
	}
	
	@Test
	public void testDumpFileSystem() throws IOException {
		TestNowProvider nowProv = new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 13, 19);
		nowProv.setNow(cal.getTimeInMillis());
		
		HyperDumpFileProvider prov = new HyperDumpFileProvider(
				nowProv,
				Mockito.mock(ApplicationDelegate.class),
				"test_dump");
		String namespace="namespace";
		String tableName="tablename";
		int fileCountShouldBe=6;
		
		for(int i=0; i<fileCountShouldBe; i++) {
			prov.getStreamFor(namespace, tableName, false);
			nowProv.setOneHourLater();
		}
		
		//closing opened streams
		nowProv.setOneHourLater();
		prov.closeOldFiles();

		//dirs exists
		StringBuffer sb = new StringBuffer();
		for(String dir : new String[]{prov.getBaseDumpFolder(), namespace, tableName, "1999-12", "20"}) {
			sb.append(dir);
			sb.append(File.separator);
			
			String path=sb.toString();
			File file = new File(path);
			assertTrue(path, file.exists());
			assertTrue(path, file.isDirectory());
		}
		
		//last dir
		File file = new File(sb.toString());
		assertEquals(fileCountShouldBe, file.listFiles().length);
		assertTrue(HyperDumperTestShared.deleteDir(new File(prov.getBaseDumpFolder())));	
	}

}
