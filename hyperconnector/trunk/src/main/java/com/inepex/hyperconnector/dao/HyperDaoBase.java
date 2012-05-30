package com.inepex.hyperconnector.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.thrift.TException;
import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ClientException;
import org.hypertable.thriftgen.ClientService;
import org.hypertable.thriftgen.HqlResult;
import org.hypertable.thriftgen.HqlService;
import org.hypertable.thriftgen.KeyFlag;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.thrift.HyperClientConnection;
import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServiceConnection;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;
import com.inepex.thrift.ResourceCreationException;

public abstract class HyperDaoBase {
	private static final String hyperPoolArgsNull = "HyperDaoBase error: hyperPoolArgs null.";
	private static final String hyperClientPoolNull = "HyperDaoBase error: hyperPoolArgs.getHyperClientPool() returned null.";
	private static final String hyperHqlServicePoolNull = "HyperDaoBase error: hyperPoolArgs.getHyperHqlServicePool() returned null.";
	private static final String hyperOperationFailed = "Hypertable operation failed.";
	private static final String hyperOperationFailed_ResourceCreation = "Hypertable operation failed: resource creation error.";
	private static final String hyperOperationFailed_ClientException = "Hypertable operation failed: ClientException.";
	private static final String hyperOperationFailed_ThriftCommError = "Hypertable operation failed: Thrift communication error.";
	private static final String hyperOperationFailed_Interrupted = "Hypertable operation failed: interrupted.";

	private static final byte[] deletedCellValue = new byte[0];

	// Hypertable pools and their settings
	private HyperClientPool hyperClientPool;
	private HyperHqlServicePool hyperHqlServicePool;
	private long maxWaitMillis;
	private int tryGetCount;
	private int tryCreateCount;
	private int insertFlags;
	private int insertFlushInterval;
	
	protected HyperDaoBase(HyperPoolArgs hyperPoolArgs) {
		if (hyperPoolArgs == null)
			throw new IllegalArgumentException(hyperPoolArgsNull);
		hyperClientPool = hyperPoolArgs.getHyperClientPool();
		if (hyperClientPool == null)
			throw new IllegalArgumentException(hyperClientPoolNull);
		hyperHqlServicePool = hyperPoolArgs.getHyperHqlServicePool();
		if (hyperHqlServicePool == null)
			throw new IllegalArgumentException(hyperHqlServicePoolNull);
		maxWaitMillis = hyperPoolArgs.getMaxWaitMillis();
		tryGetCount = hyperPoolArgs.getTryGetCount();
		tryCreateCount = hyperPoolArgs.getTryCreateCount();
		insertFlags = hyperPoolArgs.getInsertFlags();
		insertFlushInterval = hyperPoolArgs.getInsertFlushInterval();
	}

	public abstract String getNamespace();
	public abstract String getTable();

	protected static HyperOperationException getHOE(Throwable e, String message) {
		HyperOperationException hoe = new HyperOperationException(message);
		hoe.initCause(e);
		return hoe;
	}

	public HqlResult executeHqlQuery(String hqlQuery) throws HyperOperationException {
		HyperHqlServiceConnection hyperConn = null;
		try {
			hyperConn = hyperHqlServicePool.getResource(maxWaitMillis, tryGetCount, tryCreateCount);

			HqlService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(getNamespace());
			return client.hql_query(nsid, hqlQuery);
		} catch (InterruptedException e) {
			throw getHOE(e, hyperOperationFailed_Interrupted);
		} catch (ResourceCreationException e) {
			throw getHOE(e, hyperOperationFailed_ResourceCreation);
		} catch (ClientException e) {
			throw getHOE(e, hyperOperationFailed_ClientException);
		} catch (TException e) {
			throw getHOE(e, hyperOperationFailed_ThriftCommError);
		} catch (Throwable e) {
			throw getHOE(e, hyperOperationFailed);
		} finally {
			hyperHqlServicePool.returnResource(hyperConn);
		}
	}

	protected void insert(Cell cell) throws HyperOperationException {
		if (cell != null)
			insert(Arrays.asList(cell));
	}

	protected void insert(List<Cell> cells) throws HyperOperationException {
		HyperClientConnection hyperConn = null;
		try {
			hyperConn = hyperClientPool.getResource(maxWaitMillis, tryGetCount, tryCreateCount);

			ClientService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(getNamespace());
			long mutid = client.open_mutator(nsid, getTable(), insertFlags, insertFlushInterval);
			client.mutator_set_cells(mutid, cells);
			client.mutator_close(mutid);
		} catch (InterruptedException e) {
			throw getHOE(e, hyperOperationFailed_Interrupted);
		} catch (ResourceCreationException e) {
			throw getHOE(e, hyperOperationFailed_ResourceCreation);
		} catch (ClientException e) {
			throw getHOE(e, hyperOperationFailed_ClientException);
		} catch (TException e) {
			throw getHOE(e, hyperOperationFailed_ThriftCommError);
		} catch (Throwable e) {
			throw getHOE(e, hyperOperationFailed);
		} finally {
			hyperClientPool.returnResource(hyperConn);
		}
	}

	protected List<Cell> select(ScanSpec ss) throws HyperOperationException {
		HyperClientConnection hyperConn = null;
		try {
			hyperConn = hyperClientPool.getResource(maxWaitMillis, tryGetCount, tryCreateCount);

			ClientService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(getNamespace());
			long scid = client.open_scanner(nsid, getTable(), ss);
			List<Cell> cells = new ArrayList<Cell>();
			List<Cell> tmpCells = null;
			do {
				tmpCells = client.next_cells(scid);
				if (tmpCells != null)
					cells.addAll(tmpCells);
			} while (tmpCells != null && tmpCells.size() > 0);
			client.close_scanner(scid);

			return cells;
		} catch (InterruptedException e) {
			throw getHOE(e, hyperOperationFailed_Interrupted);
		} catch (ResourceCreationException e) {
			throw getHOE(e, hyperOperationFailed_ResourceCreation);
		} catch (ClientException e) {
			throw getHOE(e, hyperOperationFailed_ClientException);
		} catch (TException e) {
			throw getHOE(e, hyperOperationFailed_ThriftCommError);
		} catch (Throwable e) {
			throw getHOE(e, hyperOperationFailed);
		} finally {
			hyperClientPool.returnResource(hyperConn);
		}
	}
	
	protected void delete(ScanSpec ss) throws HyperOperationException {
		List<Cell> cells = select(ss);
		if (cells != null) {
			for (Cell cell : cells)
				if (cell != null && cell.getKey() != null) {
					cell.getKey().setFlag(KeyFlag.DELETE_CELL_VERSION);
					cell.setValue(deletedCellValue);
				}
			insert(cells);
		}
	}
	protected void setScanSpecOffset(ScanSpec ss, int cellLimit, int cellOffset){
		ss.setCell_limit(cellLimit);
		ss.setCell_offset(cellOffset);
	}
}
