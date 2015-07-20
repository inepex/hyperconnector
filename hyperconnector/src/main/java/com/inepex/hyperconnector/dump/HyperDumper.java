package com.inepex.hyperconnector.dump;

import java.io.IOException;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.KeyFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inepex.hyperconnector.ApplicationDelegate;

public class HyperDumper {
	
	private final static Logger _logger = LoggerFactory.getLogger(HyperDumper.class);
	
	final HyperDumpFileProvider fileProvider;
	
	public HyperDumper(NowProvider nowProvider,
			ApplicationDelegate delegate,
			String baseDumpFolder) {
		this.fileProvider=new HyperDumpFileProvider(nowProvider, delegate, baseDumpFolder);
	}
	
	public void dumpCells(List<Cell> cells, String namespace, String tableName) {
		if(cells==null || cells.isEmpty())
			return;
		
		TBinaryProtocol prot;
		boolean isDelete = KeyFlag.DELETE_CELL_VERSION.equals(cells.get(0).getKey().getFlag());
		
		try {
			prot = fileProvider.getStreamFor(namespace, tableName, isDelete);
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			return;
		}
		
		synchronized(prot) {
			for (Cell cell : cells) {
				try {
					cell.write(prot);
				} catch (TException e) {
					_logger.error(e.getMessage(), e);
				}
			}
		}
	}
}
