package com.inepex.hyperconnector.dump;

import static com.inepex.hyperconnector.dump.HyperDumperTestShared.deleteDir;
import static com.inepex.hyperconnector.dump.HyperDumperTestShared.getTestTicket;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.hypertable.thriftgen.Cell;
import org.junit.Test;

import com.inepex.example.entity.Ticket;
import com.inepex.example.entity.TicketMapper;
import com.inepex.hyperconnector.dumpreader.HyperDumpFiles;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderBufferedCellStream;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderBufferedCellStream.BufferedCellStreamException;
import com.inepex.hyperconnector.mapper.HyperMappingException;

public class HyperDumperTest {
	
	private static final TicketMapper mapper = new TicketMapper();
	
	@Test
	public void testBrokenFiles() throws Exception {
		String dumpFolder = "testBrokenFiles_dump";
		deleteDir(new File(dumpFolder));
		
		TestNowProvider nowProv = new TestNowProvider();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(HyperDumpFileProvider.UTC);
		cal.set(1999, 11, 20, 21, 19);
		nowProv.setNow(cal.getTimeInMillis());
		
		for(int i=0; i<3; i++){
			//'starting' it again
			TestHyperDumperDelegate delegate = new TestHyperDumperDelegate();
			HyperDumper hd = new HyperDumper(
					nowProv,
					delegate,
					dumpFolder);
			dumpTicket(hd, getTestTicket());
			delegate.doShutDown();
			
			//module 'stopped'
			nowProv.setNow(nowProv.now()+1000);
		}
		
		//append dummy bytes
		for(File dumpFile : HyperDumpFiles.collectMatchingFiles(dumpFolder, new HyperDumpReaderFilter()
			.nameSpace("InepexNs")
			.table("Report"))) {
			try (FileWriter fw = new FileWriter(dumpFile, true)) {
				fw.append('f');
				fw.append('g');
				fw.append('h');
				fw.append('2');
			}
		}
		
		List<Ticket> readTickets = readAllTicketsFromDumpWithSilentExceptions(dumpFolder);
		Assert.assertEquals(3, readTickets.size());
		
		Assert.assertTrue(deleteDir(new File(dumpFolder)));
	}
	
	@Test
	public void testShutdownRestart() throws Exception {
		String dumpFolder = "testShutdownRestart_dump";
		deleteDir(new File(dumpFolder));
		
		TestNowProvider nowProv = new TestNowProvider();
		
		for(int i=0; i<3; i++){
			//'starting' it again
			TestHyperDumperDelegate delegate = new TestHyperDumperDelegate();
			HyperDumper hd = new HyperDumper(
					nowProv,
					delegate,
					dumpFolder);
			dumpTicket(hd, getTestTicket());
			delegate.doShutDown();
			
			//module 'stopped'
			nowProv.setNow(nowProv.now()+1000);
		}
		
		List<Ticket> redReports = readAllTicketsFromDumpWithSilentExceptions(dumpFolder);
		Assert.assertEquals(3, redReports.size());
		
		Assert.assertTrue(deleteDir(new File(dumpFolder)));
	}

	@Test
	public void testDumpCell() throws Exception {
		TestNowProvider tnp = new TestNowProvider();
		HyperDumper hd = new HyperDumper(
				tnp,
				new TestHyperDumperDelegate(),
				"testDumpCell_folder");
		
		deleteDir(new File(hd.fileProvider.getBaseDumpFolder()));

		Ticket ticket = getTestTicket();
		dumpTicket(hd, ticket);
		
		tnp.setOneHourLater();
		tnp.setOneHourLater();
		hd.fileProvider.closeOldFiles();
		
		List<Ticket> redTickets = readAllTicketsFromDumpWithSilentExceptions(hd.fileProvider.getBaseDumpFolder());
		Assert.assertEquals(1, redTickets.size());
		Ticket redTicket = redTickets.get(0);
		Assert.assertEquals(ticket, redTicket);
		Assert.assertTrue(deleteDir(new File(hd.fileProvider.getBaseDumpFolder())));
		
	}
	
	private void dumpTicket(HyperDumper hd, Ticket ticket) throws HyperMappingException {
		List<Cell> cells = new ArrayList<Cell>();
		cells.addAll(mapper.hyperEntityToCellList(ticket));
		hd.dumpCells(cells, "InepexNs", "Report");
	}
	
	private List<Ticket> readAllTicketsFromDumpWithSilentExceptions(String baseFolder) throws Exception {
		List<Ticket> tickets = new LinkedList<Ticket>();
		for(File dumpFile : HyperDumpFiles.collectMatchingFiles(baseFolder, new HyperDumpReaderFilter()
										.nameSpace("InepexNs")
										.table("Report"))) {
			HyperDumpReaderBufferedCellStream stream = new HyperDumpReaderBufferedCellStream(dumpFile);
			stream.open();
			
			List<Cell> cells;
			while(true) {
				try {
					cells=stream.readCells();
					if(cells.isEmpty()) {
						break;
					}
					
					tickets.addAll(mapper.cellListToHyperEntityList(cells));
				} catch (BufferedCellStreamException e) {
					tickets.addAll(mapper.cellListToHyperEntityList(e.getAlreadyDecodedCells()));
					break;
				}
			}
			
			stream.close();
		}
		
		return tickets;
	}
	
	@Test
	public void testLongTimeWriting() throws Exception {
		TestNowProvider nowProv = new TestNowProvider();
		HyperDumper hd = new HyperDumper(
				nowProv,
				new TestHyperDumperDelegate(),
				"testLongTimeWriting_folder");
		deleteDir(new File(hd.fileProvider.getBaseDumpFolder()));
		
		Calendar c = Calendar.getInstance();
		c.set(2011, 10, 11, 11, 11);
		nowProv.setNow(c.getTimeInMillis());
		dumpTicket(hd, getTestTicket());

		c.set(2011, 10, 11, 12, 11);
		nowProv.setNow(c.getTimeInMillis());
		dumpTicket(hd, getTestTicket());		
		
		c.set(2011, 10, 12, 12, 11);
		nowProv.setNow(c.getTimeInMillis());
		dumpTicket(hd, getTestTicket());			
		
		c.set(2011, 11, 12, 12, 11);
		nowProv.setNow(c.getTimeInMillis());
		dumpTicket(hd, getTestTicket());	
		
		c.set(2012, 11, 12, 12, 11);
		nowProv.setNow(c.getTimeInMillis());
		dumpTicket(hd, getTestTicket());
		
		nowProv.setOneHourLater();
		nowProv.setOneHourLater();
		hd.fileProvider.closeOldFiles();
		
		List<Ticket> redReports = readAllTicketsFromDumpWithSilentExceptions(hd.fileProvider.getBaseDumpFolder());
		
		Assert.assertEquals(5, redReports.size());
		
		Assert.assertTrue(deleteDir(new File(hd.fileProvider.getBaseDumpFolder())));
	}
}

