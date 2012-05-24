package com.inepex.hyperconnector.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.hypertable.thrift.ThriftClient;
import org.hypertable.thriftgen.ClientService;

public class HyperClient {

	private ClientService.Client client = null;
	
	public HyperClient(String serverAddress, int serverPort) {
		try {
			client = ThriftClient.create(serverAddress, serverPort);
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public ClientService.Client getClient() {
		return client;
	}
}
