package com.inepex.hyperconnector.dao;

import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_ClientException;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_Interrupted;
import static com.inepex.hyperconnector.dao.HyperOperationException.hyperOperationFailed_ThriftCommError;

import org.apache.thrift.TException;
import org.hypertable.thriftgen.ClientException;
import org.hypertable.thriftgen.HqlResult;
import org.hypertable.thriftgen.HqlService;
import org.hypertable.thriftgen.ScanSpec;

import com.inepex.hyperconnector.thrift.HyperHqlServiceConnection;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class HyperDaoHelper {
	
	public static void setScanSpecOffset(ScanSpec ss, int rowLimit, int offset){
        ss.setRow_limit(rowLimit);
        ss.setRow_offset(offset);
	}

	public static HqlResult executeHqlQuery(HyperPoolArgs hyperPoolArgs, String nameSpace, String hqlQuery) throws HyperOperationException {
		HyperHqlServiceConnection hyperConn = null;
		try {
			hyperConn = hyperPoolArgs.getHyperHqlServicePool()
					.getResource(
							hyperPoolArgs.getMaxWaitMillis(), 
							hyperPoolArgs.getTryGetCount(),
							hyperPoolArgs.getTryCreateCount());

			HqlService.Client client = hyperConn.getClient();
			long nsid = client.open_namespace(nameSpace);
			return client.hql_query(nsid, hqlQuery);
		} catch (InterruptedException e) {
			throw new HyperOperationException(hyperOperationFailed_Interrupted, e);
		} catch (ClientException e) {
			throw new HyperOperationException(hyperOperationFailed_ClientException, e);
		} catch (TException e) {
			throw new HyperOperationException(hyperOperationFailed_ThriftCommError);
		} catch (Throwable e) {
			throw new HyperOperationException(hyperOperationFailed, e);
		} finally {
			hyperPoolArgs.getHyperHqlServicePool().returnResource(hyperConn);
		}
	}
}
