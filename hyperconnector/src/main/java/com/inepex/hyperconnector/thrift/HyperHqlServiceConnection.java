package com.inepex.hyperconnector.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hypertable.thrift.ThriftClient;
import org.hypertable.thriftgen.HqlService;
import org.hypertable.thriftgen.HqlService.Client;

import com.inepex.thrift.ThriftConnectionBase;

public class HyperHqlServiceConnection extends ThriftConnectionBase<HqlService.Client> {

	public HyperHqlServiceConnection(String serverAddress, int serverPort)
			throws Exception {
		super(serverAddress, serverPort);
	}

	@Override
	protected Client getNewClient() throws TTransportException, TException {
		return ThriftClient.create(serverAddress, serverPort);
	}

	@Override
	public TTransport getTransport() {
		return getClient().getInputProtocol().getTransport();
	}
}
