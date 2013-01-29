package com.inepex.hyperconnector.dumpreader;

import com.inepex.hyperconnector.dump.HyperDumpFileProvider;

public class HyperDumpReaderFilter {

	public static enum Content {
		DELETE_ONLY, BOTH, INSERT_ONLY;
	}
	
	private static int folderHierarchySize=4;
	private String nameSpace = ".*";
	private String table = ".*";
	private String yearmonth = ".*";
	private String day = ".*";
	private String hour = ".*";
	private Content content = Content.BOTH;
	private String fileMatcherString = "";
	
	public HyperDumpReaderFilter() {
		reCalcFileMatcher();
	}
	
	public int getFolderHierarchySize() {
		return folderHierarchySize;
	}
	
	public HyperDumpReaderFilter nameSpace(String nameSpace) {
		if(nameSpace==null)
			throw new IllegalArgumentException();
		
		this.nameSpace = nameSpace;
		return this;
	}
	
	public HyperDumpReaderFilter table(String table) {
		if(table==null)
			throw new IllegalArgumentException(); 
		this.table = table;
		return this;
	}
	
	public HyperDumpReaderFilter yearAndMonth(int year, int month) {
		if(year<1999 || month<1 || month>12)
			throw new IllegalArgumentException();
		
		StringBuffer sb = new StringBuffer();
		sb.append(year);
		sb.append("-");
		if(month<10)
			sb.append("0");
		sb.append(month);
		return this;
	}
	
	public HyperDumpReaderFilter day(int day) {
		if(day<1 || day>31)
			throw new IllegalArgumentException();
		
		if(day>=10)
			this.day=""+day;
		else
			this.day="0"+day;
		
		return this;
	}
	
	public HyperDumpReaderFilter day(int fromInclusive, int toExclusive) {
		checkRange(fromInclusive, toExclusive, 1, 32);
		day=expr(fromInclusive, toExclusive);
		return this;
	}
	
	public HyperDumpReaderFilter hour(int hour) {
		if(hour<0 || hour>=24)
			throw new IllegalArgumentException();
		
		if(hour>=10)
			this.hour=""+hour;
		else
			this.hour="0"+hour;
		
		reCalcFileMatcher();
		return this;
	}
	
	public HyperDumpReaderFilter hour(int fromInclusive, int toExclusive) {
		checkRange(fromInclusive, toExclusive, 0, 24);
		hour=expr(fromInclusive, toExclusive);
		reCalcFileMatcher();
		return this;
	}
	
	private void checkRange(int fromInclusive, int toExclusive, int minValueInclusive, int maxValueExclusive) {
		if(fromInclusive<minValueInclusive
				|| toExclusive<minValueInclusive
				|| fromInclusive>=toExclusive 
				|| fromInclusive>=maxValueExclusive 
				|| toExclusive>=maxValueExclusive)
			throw new IllegalArgumentException();
	}
	
	private String expr(int fromInclusive, int toExclusive) {
		StringBuffer sb = new StringBuffer();
		for(int i=fromInclusive; i<toExclusive; i++) {
			if(sb.length()>0)
				sb.append("|");
			if(i<10)
				sb.append("0");
			sb.append(i);
		}
		
		return sb.toString();
	}
	
	public HyperDumpReaderFilter content(Content content) {
		if(content==null)
			throw new IllegalArgumentException();
		this.content = content;
		reCalcFileMatcher();
		return this;
	}


	private void reCalcFileMatcher() {
		switch(content) {
		case INSERT_ONLY:
			fileMatcherString=insertOnlyMatcher();
			break;
		case DELETE_ONLY:
			fileMatcherString=deleteOnlyMatcher();
			break;
			
		case BOTH:
			fileMatcherString="("+insertOnlyMatcher()+")|("+deleteOnlyMatcher()+")";
			break;
		}
	}
	
	private String afterRestartPostfix() {
		return "(_[0-9]+)?";
	}
	
	private String insertOnlyMatcher() {
		return "("+hour+")_UTC"+HyperDumpFileProvider.EXTENSION+afterRestartPostfix();
	}
	
	private String deleteOnlyMatcher() {
		return "("+hour+")_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION+afterRestartPostfix();
	}
	
	public boolean isMatchingFolder(String name, int depth) {
		String matcher;
		switch(depth) {
		case 0:
			matcher=nameSpace;
			break;
		case 1:
			matcher=table;
			break;
		case 2: 
			matcher=yearmonth;
			break;
		case 3:
			matcher=day;
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		
		return name.matches(matcher);
	}

	public boolean isMatchingFile(String name) {
		return name.matches(fileMatcherString);
	}
}
