package com.inepex.hyperconnector.dumpintegritytest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hypertable.thriftgen.Cell;

import com.inepex.hyperconnector.dump.HyperDumperTestShared;
import com.inepex.hyperconnector.dumpreader.HyperDumpReader;
import com.inepex.hyperconnector.dumpreader.HyperDumpReader.FileContent;
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
		List<Cell> ret = new ArrayList<Cell>();
		HyperDumpReader reader = new HyperDumpReader(dumpFolderName, filter);
		for(FileContent fc: reader) {
			if(fc.hasException())
				throw fc.getReadException();
			
			ret.addAll(fc.getContent());
		}
		
		return ret;
	}
}
