package com.inepex.hyperconnector.dump;

import static com.inepex.hyperconnector.dump.HyperDumperTestShared.deleteDir;
import static com.inepex.hyperconnector.dump.HyperDumperTestShared.getTestTicket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.hypertable.thriftgen.Cell;
import org.junit.Test;

import com.inepex.example.entity.Ticket;
import com.inepex.example.entity.TicketMapper;
import com.inepex.hyperconnector.dumpreader.HyperDumpReader;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter;
import com.inepex.hyperconnector.mapper.HyperMappingException;

public class HyperDumperTest {
	
	private static final TicketMapper mapper = new TicketMapper();
	
	@Test
	public void testShutdownRestart() throws HyperMappingException {
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
		
		List<Ticket> redReports = readAllTicketsFromDump(dumpFolder);
		Assert.assertEquals(3, redReports.size());
		
		Assert.assertTrue(deleteDir(new File(dumpFolder)));
	}

	@Test
	public void testDumpCell() throws HyperMappingException, IOException {
		TestNowProvider tnp = new TestNowProvider();
		HyperDumper hd = new HyperDumper(
				tnp,
				new TestHyperDumperDelegate(),
				"test_dump_folder");

		Ticket ticket = getTestTicket();
		dumpTicket(hd, ticket);
		
		tnp.setOneHourLater();
		tnp.setOneHourLater();
		hd.fileProvider.closeOldFiles();
		
		List<Ticket> redTickets = readAllTicketsFromDump(hd.fileProvider.getBaseDumpFolder());
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
	
	private List<Ticket> readAllTicketsFromDump(String baseFolder) throws HyperMappingException {
		HyperDumpReader hdr = new HyperDumpReader(baseFolder,
				new HyperDumpReaderFilter()
					.nameSpace("InepexNs")
					.table("Report"));
		
		List<Ticket> reports = new LinkedList<Ticket>();
		Iterator<List<Cell>> itertor = hdr.createCellIterator();
		while(itertor.hasNext()) {
			reports.addAll(mapper.cellListToHyperEntityList(itertor.next()));
		}
		return reports;
	}
	
	@Test
	public void testLongTimeWriting() throws IOException, HyperMappingException {
		TestNowProvider nowProv = new TestNowProvider();
		HyperDumper hd = new HyperDumper(
				nowProv,
				new TestHyperDumperDelegate(),
				"test_dump_folder");
		
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
		
		List<Ticket> redReports = readAllTicketsFromDump(hd.fileProvider.getBaseDumpFolder());
		
		Assert.assertEquals(5, redReports.size());
		
		Assert.assertTrue(deleteDir(new File(hd.fileProvider.getBaseDumpFolder())));
	}
}

