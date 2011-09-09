package com.inepex.hyperconnector.thrift;

import org.hypertable.thriftgen.ClientService;

import org.apache.thrift.protocol.TProtocol;

import com.inepex.thrift.ThriftConnectionBase;

public class HyperClientConnection extends ThriftConnectionBase<ClientService.Client> {

	public HyperClientConnection(String serverAddress, int serverPort)
			throws Exception {
		super(serverAddress, serverPort);
	}

	@Override
	protected ClientService.Client getNewClient(TProtocol protocol) {
		ClientService.Client.Factory factory = new ClientService.Client.Factory();
		return factory.getClient(protocol);
	}
}
