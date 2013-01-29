package com.inepex.hyperconnector.dumpreader;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hypertable.thriftgen.Cell;

public class HyperDumpReader implements Iterable<HyperDumpReader.FileContent>{
	
	public static class FileContent {
		private String path;
		private List<Cell> content;
		private Exception readException;
		
		public void setPath(String path) {
			this.path = path;
		}
		
		public void setContent(List<Cell> content) {
			this.content = content;
		}
		
		public void setReadException(Exception readException) {
			this.readException = readException;
		}
		
		public String getPath() {
			return path;
		}
		
		public List<Cell> getContent() {
			return content;
		}
		
		public Exception getReadException() {
			return readException;
		}
		
		public boolean hasException() {
			return readException!=null;
		}
	}
	
	private final HyperDumpReaderFilter filter;
	private final String baseDumpFolder;
	
	public HyperDumpReader(String baseDumpFolder, HyperDumpReaderFilter filter) {
		this.baseDumpFolder=baseDumpFolder;
		this.filter=filter;
	}
	
	@Override
	public Iterator<HyperDumpReader.FileContent> iterator() {
		List<File> matchingFiles = collectMatchingFiles();
		return new HyperFileReaderIterator(matchingFiles);
	}
	
	private List<File> collectMatchingFiles() {
		List<File> matchingFiles = new LinkedList<File>();
		File startDir = new File(baseDumpFolder);
		addAllFilesResursive(startDir, matchingFiles, 0);
		return matchingFiles;
	}
	
	private void addAllFilesResursive(File dirToCheck, List<File> matchingFiles, int depthToCheck) {
		if(depthToCheck==filter.getFolderHierarchySize()) {
			//collect files
			for(File file : nameOrdered(dirToCheck.listFiles())) {
				if(file.isFile() && filter.isMatchingFile(file.getName())) {
					matchingFiles.add(file);
				}
			}
		} else {
			//collect directories
			for(File file : nameOrdered(dirToCheck.listFiles())) {
				if(file.isDirectory() && filter.isMatchingFolder(file.getName(), depthToCheck)) {
					addAllFilesResursive(file, matchingFiles, depthToCheck+1);
				}
			}
		}
	}

	private File[] nameOrdered(File[] fileList) {
		Arrays.sort(fileList);
		return fileList;
	}
}
