package com.inepex.hyperconnector.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hypertable.thrift.ThriftClient;

import com.inepex.thrift.ThriftConnectionBase;

public class HyperClientConnection extends ThriftConnectionBase<ThriftClient> {

	public HyperClientConnection(String serverAddress, int serverPort)
			throws Exception {
		super(serverAddress, serverPort);
	}

	@Override
	protected ThriftClient getNewClient() throws TTransportException, TException {		
		return ThriftClient.create(serverAddress, serverPort);
	}

	@Override
	public TTransport getTransport() {
		return getClient().getInputProtocol().getTransport();
	}
}
