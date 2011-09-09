package com.inepex.hyperconnector.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.hypertable.thriftgen.HqlService;

import com.inepex.thrift.ThriftConnectionBase;

public class HyperHqlServiceConnection extends ThriftConnectionBase<HqlService.Client> {

	public HyperHqlServiceConnection(String serverAddress, int serverPort)
			throws Exception {
		super(serverAddress, serverPort);
	}

	@Override
	protected HqlService.Client getNewClient(TProtocol protocol) {
		HqlService.Client.Factory factory = new HqlService.Client.Factory();
		return factory.getClient(protocol);
	}
}
