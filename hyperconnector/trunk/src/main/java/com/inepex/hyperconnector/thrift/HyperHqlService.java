package com.inepex.hyperconnector.thrift;

import org.hypertable.thriftgen.HqlService;
import org.apache.thrift.protocol.TProtocol;

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
