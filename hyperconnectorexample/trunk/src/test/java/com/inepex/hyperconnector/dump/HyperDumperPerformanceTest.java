package com.inepex.hyperconnector.dump;

import static com.inepex.hyperconnector.dump.HyperDumperTestShared.deleteDir;
import static com.inepex.hyperconnector.dump.HyperDumperTestShared.getTestTicket;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.mutable.MutableLong;
import org.apache.thrift.TException;
import org.hypertable.thriftgen.Cell;
import org.junit.Test;

import com.inepex.example.entity.TicketMapper;
import com.inepex.hyperconnector.dump.HyperDumpFileProvider.OpenedStreams;

public class HyperDumperPerformanceTest {

	private static final TicketMapper mapper = new TicketMapper();
	private static int ROUNDCOUNT=20;
	
	/**
	 * 30000 cells
	 * 
	 * 305ms for raw write out ResSize: 1164Kb
	 * 285ms for whole out ResSize: 1164Kb
	 * 299ms for cell by cell out ResSize: 1164Kb
	 * 309ms for cell by cell (timing) out ResSize: 1167Kb
	 */
	@Test
	public void performance() throws Exception {
		for(int cellCount : new int[]{10000}) {
			System.out.println(cellCount+" cells\n");
			
			//test data
			final List<Cell> cells = new ArrayList<Cell>(cellCount);
			final List<List<Cell>> cellLists = new ArrayList<List<Cell>>(cellCount);
			for(int i=0; i<cellCount; i++) {
				List<Cell> ticketAsCells = mapper.hyperEntityToCellList(getTestTicket());
				cells.addAll(ticketAsCells);
				cellLists.add(ticketAsCells);
			}
			
			doTest(cellCount, "raw write", new LoopContent() {
				
				private OpenedStreams streams;
				
				@Override
				public void doBefore(HyperDumper hd) throws Exception {
					streams=hd.fileProvider.createStreams(0, "nspc", "tnm", false);
				}
				
				@Override
				public void doIt(int i, HyperDumper hd, TestNowProvider nowProvider) throws TException {
					for(Cell cell : cellLists.get(i)) {
						cell.write(streams.binaryProtocol);
					}
				}
				
				@Override
				public void doAfter(HyperDumper hd, TestNowProvider nowProvider) {
					hd.fileProvider.closeStreams(streams);
				}
			});
			
			
			doTest(cellCount, "whole",new LoopContent() {
				@Override
				public void doAfter(HyperDumper hd, TestNowProvider nowProvider) {
					hd.dumpCells(cells, "nameSpace", "tableName");
				}
			});
			
			doTest(cellCount, "cell by cell", new LoopContent() {
				@Override
				public void doIt(int i, HyperDumper hd, TestNowProvider nowProvider) {
					hd.dumpCells(cellLists.get(i), "nameSpace", "tableName");
				}
			});
				
			doTest(cellCount, "cell by cell (timing)", new LoopContent() {
				@Override
				public void doIt(int i, HyperDumper hd, TestNowProvider nowProvider) {
					nowProvider.setNow(nowProvider.now()+5000);
					hd.dumpCells(cellLists.get(i), "nameSpace", "tableName");
				}
			});
			
			System.out.println();
		}
	}
	
	private void doTest(int cellCount, String label, LoopContent loopContent) throws Exception {
		long timeSpentSum=0;
		long dumpSizeSum=0;
		
		for(int round=0; round<ROUNDCOUNT; round++) {
			TestNowProvider nowProvider = new TestNowProvider();
			HyperDumper hd = new HyperDumper(nowProvider, new TestHyperDumperDelegate(), "perf_test_dump_");
			long start=System.currentTimeMillis();
			
			loopContent.doBefore(hd);
			for(int i=0; i<cellCount; i++) {
				loopContent.doIt(i, hd, nowProvider);
			}
			loopContent.doAfter(hd, nowProvider);
			hd.fileProvider.closeAllFile();
			
			timeSpentSum+=System.currentTimeMillis()-start;
			dumpSizeSum+= deleteDumpDirPrintSize(hd.fileProvider.getBaseDumpFolder());
		}
		
		System.out.println((timeSpentSum/ROUNDCOUNT)+"ms for "+label+" out ResSize: "+(dumpSizeSum/1024/ROUNDCOUNT)+"Kb");
	}
	
	private long deleteDumpDirPrintSize(String baseDumpFolder) throws IOException {
		final MutableLong sizeInBytes = new MutableLong();
		
		Files.walkFileTree(FileSystems.getDefault().getPath(baseDumpFolder), new SimpleFileVisitor<Path>() {
		            @Override
		            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		                sizeInBytes.add(attrs.size());
		                return FileVisitResult.CONTINUE;
		            }
		});
		
		Assert.assertTrue(deleteDir(new File(baseDumpFolder)));
		return sizeInBytes.longValue();
	}
	
	private static abstract class LoopContent{
		public void doBefore(HyperDumper hd)  throws Exception {
		}
		
		public void doIt(int i, HyperDumper hd, TestNowProvider nowProvider) throws Exception{
		} 
		
		public void doAfter(HyperDumper hd, TestNowProvider nowProvider) throws Exception {
		}
	}
}
