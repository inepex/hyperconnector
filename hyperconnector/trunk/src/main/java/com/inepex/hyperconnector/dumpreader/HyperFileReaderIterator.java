package com.inepex.hyperconnector.dumpreader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.hypertable.thriftgen.Cell;
import org.xerial.snappy.SnappyInputStream;

public class HyperFileReaderIterator implements Iterator<HyperDumpReader.FileContent> {

	private final List<File> matchingFiles;
	private int currentIndex=0;
	
	public HyperFileReaderIterator(List<File> matchingFiles) {
		this.matchingFiles=matchingFiles;
	}

	@Override
	public boolean hasNext() {
		return currentIndex<matchingFiles.size();
	}

	@Override
	public HyperDumpReader.FileContent next() {
		if(!hasNext())
			throw new NullPointerException();
		
		File dumpfile = matchingFiles.get(currentIndex);
		currentIndex++;
		HyperDumpReader.FileContent content = new HyperDumpReader.FileContent();
		readAllCellsFromDump(dumpfile, content);
		return content;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	
	private void readAllCellsFromDump(File dumpFile, HyperDumpReader.FileContent content) {
		List<Cell> cells = new ArrayList<Cell>();
		try {
			byte[] data = readAndUncompressData(dumpFile);
			TMemoryInputTransport transport = new TMemoryInputTransport(
					data);
			TBinaryProtocol prot = new TBinaryProtocol(transport);
			
			while(prot.getTransport().getBytesRemainingInBuffer()>0) {
				Cell c = new Cell();
				c.read(prot);
				cells.add(c);
			}
			
		} catch(Exception e) {
			content.setReadException(e);
		}
		
		content.setContent(cells);
		content.setPath(dumpFile.getAbsolutePath());
	}
	
	private byte[] readAndUncompressData(File file) throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileInputStream fis=null;
		SnappyInputStream snIs=null;
		try {
			fis = new FileInputStream(file);
			snIs = new SnappyInputStream(fis);
			
			while(true) {
				int i = snIs.read();
				if(i!=-1) {
					bos.write((byte) i);
				} else {
					break;
				}
			}
		} finally {
			if(snIs!=null) {
				try {
					snIs.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			if(fis!=null) {
				try {
					fis.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return bos.toByteArray();
	}
}
