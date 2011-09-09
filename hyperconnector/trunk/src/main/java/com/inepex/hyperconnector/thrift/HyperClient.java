package com.inepex.hyperconnector.thrift;

import org.hypertable.thriftgen.ClientService;
import org.apache.thrift.protocol.TProtocol;

public class HyperClient {

	private ClientService.Client client = null;
	
	public HyperClient(TProtocol protocol) {
		ClientService.Client.Factory factory = new ClientService.Client.Factory();
		client = factory.getClient(protocol);
	}

	public ClientService.Client getClient() {
		return client;
	}
}
