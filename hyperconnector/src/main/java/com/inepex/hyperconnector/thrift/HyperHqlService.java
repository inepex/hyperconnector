package com.inepex.hyperconnector.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.hypertable.thriftgen.HqlService;

public class HyperHqlService {

	private HqlService.Client client = null;

	public HyperHqlService(TProtocol protocol) {
		HqlService.Client.Factory factory = new HqlService.Client.Factory();
		client = factory.getClient(protocol);
	}
	
	public HqlService.Client getClient() {
		return client;
	}
}
