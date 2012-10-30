package com.inepex.hyperconnector.dumpreader;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hypertable.thriftgen.Cell;

public class HyperDumpReader {
	
	private final HyperDumpReaderFilter filter;
	private final String baseDumpFolder;
	
	public HyperDumpReader(String baseDumpFolder, HyperDumpReaderFilter filter) {
		this.baseDumpFolder=baseDumpFolder;
		this.filter=filter;
	}
	
	public Iterator<List<Cell>> createCellIterator() {
		List<File> matchingFiles = collectMatcingFiles();
		return new HyperFileReaderIterator(matchingFiles);
	}
	
	private List<File> collectMatcingFiles() {
		List<File> matchingFiles = new LinkedList<File>();
		File startDir = new File(baseDumpFolder);
		addAllFilesResursive(startDir, matchingFiles, 0);
		return matchingFiles;
	}
	
	private void addAllFilesResursive(File dirToCheck, List<File> matchingFiles, int depthToCheck) {
		if(depthToCheck==filter.getFolderHierarchySize()) {
			//collect files
			for(File file : dirToCheck.listFiles()) {
				if(file.isFile() && filter.isMatchingFile(file.getName())) {
					matchingFiles.add(file);
				}
			}
		} else {
			//collect directories
			for(File file : dirToCheck.listFiles()) {
				if(file.isDirectory() && filter.isMatchingFolder(file.getName(), depthToCheck)) {
					addAllFilesResursive(file, matchingFiles, depthToCheck+1);
				}
			}
		}
	}
}
