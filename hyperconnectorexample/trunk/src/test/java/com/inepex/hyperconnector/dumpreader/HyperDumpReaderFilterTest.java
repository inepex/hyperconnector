package com.inepex.hyperconnector.dumpreader;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.inepex.hyperconnector.dump.HyperDumpFileProvider;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderFilter.Content;

public class HyperDumpReaderFilterTest {

	@Test
	public void testDefault() {
		HyperDumpReaderFilter filter = new HyperDumpReaderFilter();
		
		assertEquals(true, filter.isMatchingFolder("InepexLbs", 0));
		assertEquals(true, filter.isMatchingFolder("Report", 1));
		assertEquals(true, filter.isMatchingFolder("1999-11", 2));
		assertEquals(true, filter.isMatchingFolder("10", 3));
		assertEquals(true, filter.isMatchingFile("12_UTC"+HyperDumpFileProvider.EXTENSION));
		assertEquals(true, filter.isMatchingFile("12_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("12"+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("12"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("12_UTC.txt"));
	}
	
	@Test
	public void testIsMatchingFile() {
		HyperDumpReaderFilter filter = new HyperDumpReaderFilter()
			.content(Content.INSERT_ONLY)
			.hour(12, 21);
		
		assertEquals(false, filter.isMatchingFile("11_UTC"+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("11_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("11_UTC.txt"));
		
		assertEquals(true, filter.isMatchingFile("12_UTC"+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("12_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("12_UTC.txt"));
		
		assertEquals(true, filter.isMatchingFile("20_UTC"+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("20_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("20_UTC.txt"));
		
		assertEquals(false, filter.isMatchingFile("21_UTC"+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("21_UTC"+HyperDumpFileProvider.EXTRADELEXTENSION+HyperDumpFileProvider.EXTENSION));
		assertEquals(false, filter.isMatchingFile("21_UTC.txt"));
	}
}
