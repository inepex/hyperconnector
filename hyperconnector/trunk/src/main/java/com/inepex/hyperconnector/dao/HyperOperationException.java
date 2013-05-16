package com.inepex.hyperconnector.dao;

public class HyperOperationException extends Exception {
	
	private static final long serialVersionUID = 4459264579039013328L;
	
	public static final String hyperPoolArgsNull = "HyperDaoBase error: hyperPoolArgs null.";
	public static final String hyperClientPoolNull = "HyperDaoBase error: hyperPoolArgs.getHyperClientPool() returned null.";
	public static final String hyperHqlServicePoolNull = "HyperDaoBase error: hyperPoolArgs.getHyperHqlServicePool() returned null.";
	public static final String hyperOperationFailed = "Hypertable operation failed.";
	public static final String hyperOperationFailed_ClientException = "Hypertable operation failed: ClientException.";
	public static final String hyperOperationFailed_ThriftCommError = "Hypertable operation failed: Thrift communication error.";
	public static final String hyperOperationFailed_Interrupted = "Hypertable operation failed: interrupted.";

	public HyperOperationException(String message) {
		super(message);
	}

	public HyperOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
