package com.inepex.hyperconnector.dumpreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.hypertable.thriftgen.Cell;
import org.xerial.snappy.SnappyInputStream;

public class HyperDumpReaderBufferedCellStream implements AutoCloseable{
	
	public static class BufferedCellStreamException extends Exception {
		private final List<Cell> alreadydecodedCells;

		public BufferedCellStreamException(List<Cell> alreadydecodedCells, Throwable cause) {
			super(cause);
			this.alreadydecodedCells=alreadydecodedCells;
		}
		
		public List<Cell> getAlreadydecodedCells() {
			return alreadydecodedCells;
		}
	}
	
	private static final int cellReadLimit=30_000;
	private final File dumpFile;
	
	private FileInputStream fis;
	private SnappyInputStream snIs;
	private TIOStreamTransport tioStreamTransport;
	private TBinaryProtocol tBinaryProtocol;
	
	public HyperDumpReaderBufferedCellStream(File file) {
		this.dumpFile=file;
	}
	
	public void open() throws Exception {
		try {
			fis = new FileInputStream(dumpFile);
			snIs = new SnappyInputStream(fis);
			tioStreamTransport=new TIOStreamTransport(snIs);
			tBinaryProtocol=new TBinaryProtocol(tioStreamTransport);
		} catch (Exception e) {
			close();
			throw e;
		}
	}

	@Override
	public void close() {
		if(tioStreamTransport!=null) {
			try {
				tioStreamTransport.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			tioStreamTransport=null; 
		}
		
		if(snIs!=null) {
			try {
				snIs.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			snIs=null;
		}
		
		if(fis!=null) {
			try {
				fis.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			fis=null;
		}
	}
	
	public List<Cell> readCells() throws BufferedCellStreamException {
		try {
			if(noMoreToRead()) {
				close();
				return Collections.emptyList();
			}
		} catch (IOException e) {
			throw new BufferedCellStreamException(Collections.<Cell>emptyList(), e);
		}
		
		List<Cell> cells = new ArrayList<Cell>(cellReadLimit);
		try {
			int i=0;
			while(i<cellReadLimit && !noMoreToRead()) {
				Cell c = new Cell();
				c.read(tBinaryProtocol);
				cells.add(c);
				i++;
			}
		} catch (Exception e) {
			throw new BufferedCellStreamException(cells, e);
		}
		
		return cells;
	}
	
	private boolean noMoreToRead() throws IOException {
		return snIs==null || snIs.available()<=0;
	}
}