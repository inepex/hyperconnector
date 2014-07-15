package com.inepex.hyperconnector.dumpreader;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HyperDumpFiles {
	
	public static List<File> collectMatchingFiles(String baseDumpFolder, HyperDumpReaderFilter filter) {
		List<File> matchingFiles = new LinkedList<File>();
		File startDir = new File(baseDumpFolder);
		addAllFilesResursive(startDir, matchingFiles, 0, filter);
		return matchingFiles;
	}
	
	private static void addAllFilesResursive(File dirToCheck, List<File> matchingFiles, int depthToCheck, HyperDumpReaderFilter filter) {
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
					addAllFilesResursive(file, matchingFiles, depthToCheck+1, filter);
				}
			}
		}
	}

	private static File[] nameOrdered(File[] fileList) {
		Arrays.sort(fileList);
		return fileList;
	}
}
