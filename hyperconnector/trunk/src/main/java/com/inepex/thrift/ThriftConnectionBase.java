package com.inepex.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public abstract class ThriftConnectionBase<T> {
	private String serverAddress = "";
	private int serverPort = 20002;

	private int connectionTimeout = 20000;
	private boolean invalid = false;
	
	private TTransport transport = null;
	private T client = null;
	
	public ThriftConnectionBase(String serverAddress, int serverPort) throws Exception {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		createClient();
	}
	
	protected abstract T getNewClient(TProtocol protocol);
	
	// Helper funcitons for accessing Inepex core
	public void createClient() throws Exception {
		try {			
			TSocket tSocket = new TSocket(serverAddress, serverPort, connectionTimeout);	
			transport = new TFramedTransport(tSocket);
			TProtocol protocol = new TBinaryProtocol(transport);
			client  = getNewClient(protocol);
			transport.open();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}	
	}
	
	public void closeClient() {	
		try {
			if (transport != null) {
				transport.flush();
				transport.close();
			}
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
	
	public TTransport getTransport() {
		return transport;
	}
	
	public T getClient() {
		return client;
	}
	
	public boolean isInvalid() {
		return invalid;
	}
	
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
}
