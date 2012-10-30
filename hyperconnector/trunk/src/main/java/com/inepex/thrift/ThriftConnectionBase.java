package com.inepex.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


public abstract class ThriftConnectionBase<T> {
	protected String serverAddress = "";
	protected int serverPort = 20002;

	private boolean invalid = false;
	
	private T client = null;
	
	public ThriftConnectionBase(String serverAddress, int serverPort) throws Exception {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		createClient();
	}
	
	protected abstract T getNewClient() throws TTransportException, TException;
	
	// Helper funcitons for accessing Inepex core
	public void createClient() throws Exception {
		try {			
			client  = getNewClient();
		} catch (Exception e) {
			throw new Exception(e);
		}	
	}
	
	public abstract TTransport getTransport();
	
	public void closeClient() {
		getTransport().close();
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
