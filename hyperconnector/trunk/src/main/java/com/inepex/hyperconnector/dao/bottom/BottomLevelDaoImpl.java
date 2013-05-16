package com.inepex.hyperconnector.dao.bottom;

import static com.inepex.hyperconnector.dao.HyperOperationException.hyperClientPoolNull;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperHqlServicePoolNull;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_ClientException;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_Interrupted;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_ThriftCommError;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperPoolArgsNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.ClientException;
import org.hypertable.thriftgen.ClientService;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.thrift.HyperClientConnection;
import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class BottomLevelDaoImpl implements BottomLevelDao {

	private final String nameSpace;
	private final String tableName;
	
	// Hypertable pools and their settings
	private HyperClientPool hyperClientPool;
	private HyperHqlServicePool hyperHqlServicePool;
	private long maxWaitMillis;
	private int tryGetCount;
	private int tryCreateCount;
	private int insertFlags;
	private int insertFlushInterval;
	
	public BottomLevelDaoImpl(HyperPoolArgs hyperPoolArgs, String nameSpace, String tableName) {
		if (hyperPoolArgs == null)
			throw new IllegalArgumentException(hyperPoolArgsNull);
		
		hyperClientPool = hyperPoolArgs.getHyperClientPool();
		if (hyperClientPool == null)
			throw new IllegalArgumentException(hyperClientPoolNull);
		
		hyperHqlServicePool = hyperPoolArgs.getHyperHqlServicePool();
		if (hyperHqlServicePool == null)
			throw new IllegalArgumentException(hyperHqlServicePoolNull);
		
		if(nameSpace==null)
			throw new IllegalArgumentException();
		this.nameSpace=nameSpace;
		
		if(tableName==null)
			throw new IllegalArgumentException();
		this.tableName=tableName;
		
		maxWaitMillis = hyperPoolArgs.getMaxWaitMillis();
		tryGetCount = hyperPoolArgs.getTryGetCount();
		tryCreateCount = hyperPoolArgs.getTryCreateCount();
		insertFlags = hyperPoolArgs.getInsertFlags();
		insertFlushInterval = hyperPoolArgs.getInsertFlushInterval();
	}
	
	@Override
	public String getNamespace() {
		return nameSpace;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public void insert(List<Cell> cells) throws HyperOperationException {
		if(cells==null || cells.isEmpty())
			return;
		
		HyperClientConnection hyperConn = null;
		
		try {
			hyperConn = hyperClientPool.getResource(maxWaitMillis, tryGetCount, tryCreateCount);

			ClientService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(nameSpace);
			long mutid = client.open_mutator(nsid, tableName, insertFlags, insertFlushInterval);
			client.mutator_set_cells(mutid, cells);
			client.mutator_close(mutid);
		} catch (InterruptedException e) {
			throw new HyperOperationException(hyperOperationFailed_Interrupted, e);
		} catch (ClientException e) {
			throw new HyperOperationException(hyperOperationFailed_ClientException, e);
		} catch (TException e) {
			throw new HyperOperationException(hyperOperationFailed_ThriftCommError, e);
		} catch (Throwable e) {
			throw new HyperOperationException(hyperOperationFailed, e);
		} finally {
			hyperClientPool.returnResource(hyperConn);
		}
	}

	@Override
	public List<Cell> select(ScanSpec ss) throws HyperOperationException {
		HyperClientConnection hyperConn = null;
		try {
			hyperConn = hyperClientPool.getResource(maxWaitMillis, tryGetCount, tryCreateCount);

			ClientService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(nameSpace);
			long scid = client.open_scanner(nsid, tableName, ss);
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
			throw new HyperOperationException(hyperOperationFailed_Interrupted, e);
		} catch (ClientException e) {
			throw new HyperOperationException(hyperOperationFailed_ClientException, e);
		} catch (TException e) {
			throw new HyperOperationException(hyperOperationFailed_ThriftCommError, e);
		} catch (Throwable e) {
			throw new HyperOperationException(hyperOperationFailed, e);
		} finally {
			hyperClientPool.returnResource(hyperConn);
		}
	}
}
