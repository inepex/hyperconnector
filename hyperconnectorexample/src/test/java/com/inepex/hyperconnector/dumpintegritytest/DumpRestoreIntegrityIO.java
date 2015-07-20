package com.inepex.hyperconnector.dumpintegritytest;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.hypertable.thriftgen.Cell;

import com.inepex.hyperconnector.dump.HyperDumperTestShared;
import com.inepex.hyperconnector.dumpreader.HyperDumpFiles;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderBufferedCellStream;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter;

public class DumpRestoreIntegrityIO {
	
	public static final String dumpFolderName="dum_restore_integrity_dump";
	
	public void deleteDumpFolderIfExists() {
		File dumpFolder = new File(dumpFolderName);
		if(dumpFolder.exists()) {
			boolean succ = HyperDumperTestShared.deleteDir(new File(dumpFolderName));
			if(!succ) {
				throw new Error("Can't delete dump folder!");
			}
			
			System.out.println("Removing dump folder...");
		} else {
			System.out.println("Dump doesn't exist!");
		}
	}
	
	public List<Cell> readCells(HyperDumpReaderFilter filter) throws Exception {
		List<Cell> cells = new LinkedList<Cell>();
		for(File dumpFile : HyperDumpFiles.collectMatchingFiles(dumpFolderName, filter)) {
			HyperDumpReaderBufferedCellStream stream = new HyperDumpReaderBufferedCellStream(dumpFile);
			
			stream.open();
			
			List<Cell> nextCells;
			while(true) {
				nextCells=stream.readCells();
				if(nextCells.isEmpty()) {
					break;
				}
				
				cells.addAll(nextCells);
			}
			
			stream.close();
		}
		
		return cells;
	}
}
