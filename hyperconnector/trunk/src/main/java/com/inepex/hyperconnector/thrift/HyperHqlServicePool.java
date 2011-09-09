package com.inepex.hyperconnector.thrift;

import com.inepex.thrift.ResourcePool;

public class HyperHqlServicePool extends ResourcePool<HyperHqlServiceConnection> {
	private static final String illegalArg_ServerAddressMsg = "ServerAddress is null.";
	private static final String illegalArg_ServerPortMsg = "ServerPort must be between 1 and 65535.";
	private static final String resourceDestroyErrorMsgStart = "Error while destroying resource in HyperHqlServicePool: ";
	private String serverAddress = null;
	private int serverPort = -1;
	
	public HyperHqlServicePool(String serverAddress, int serverPort) {
		if (serverAddress == null)
			throw new IllegalArgumentException(illegalArg_ServerAddressMsg);
		if (serverPort < 1 || serverPort > 65535)
			throw new IllegalArgumentException(illegalArg_ServerPortMsg);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	@Override
	protected HyperHqlServiceConnection createResource() throws Exception {		
		HyperHqlServiceConnection hyperConnection = new HyperHqlServiceConnection(serverAddress, serverPort);
		hyperConnection.createClient();
		
		return hyperConnection;
	}

	@Override
	protected void destroyResource(HyperHqlServiceConnection resource) {
		try {
			resource.closeClient();
		} catch (Exception e) {
			System.out.println(resourceDestroyErrorMsgStart + e);
		}
	}

	@Override
	protected boolean isUsable(HyperHqlServiceConnection resource) {
		return !resource.isInvalid() && resource.getTransport().isOpen();
	}
}
