package com.inepex.thrift;

import org.apache.thrift.transport.TTransportException;

import com.inepex.hyperconnector.thrift.HyperHqlServiceConnection;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;

/**
 * @author istvan
 * This class is used to do a call on a Thrift interface in a safe way.
 * callMethod and result callback methods should be called synchronously
 * @param <T> The result type of the Thrift call
 */
public abstract class SafeHyperHqlServiceCall<T> {	

	protected HyperHqlServiceConnection hyperConnection = null;
	protected Exception lastException = null;
	protected boolean callSucceeded = false;
	protected T result = null;
	
	public void doCall(int tryCount, HyperHqlServicePool pool) {
		try {
			while (tryCount > 0) {
				try {
					hyperConnection = pool.getResource(10000, 3, 3);		
					result = callMethod();
					return;
				} catch (TTransportException tte) {
					tte.printStackTrace();
					hyperConnection.setInvalid(true);
				} catch (Exception e) {
					e.printStackTrace();
					hyperConnection.setInvalid(true);
				} finally {
					tryCount--;
					if (hyperConnection != null)
						pool.returnResource(hyperConnection);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}			
	}
	
	protected abstract T callMethod() throws Exception;
	
	public Exception getLastException() {
		return lastException;
	}

	public void setLastException(Exception lastException) {
		this.lastException = lastException;
	}

	public boolean isCallSucceeded() {
		return callSucceeded;
	}

	public void setCallSucceeded(boolean callSucceeded) {
		this.callSucceeded = callSucceeded;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}
