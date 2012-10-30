package com.inepex.example.conf;

import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class HyperConfigurator {
	public static HyperPoolArgs getHPA() {
		// address and port
		String hypertableServerAddress = "localhost";
		int hypertableServerPort = 38080;

		// other settings
		int maxWaitMillis = 10;
		int tryGetCount = 3;
		int tryCreateCount = 3;
		int insertFlags = 0;
		int insertFlushInterval = 0;

		// Client pool and Service pool
		HyperClientPool hyperClientPool = new HyperClientPool(
				hypertableServerAddress, hypertableServerPort);
		HyperHqlServicePool hyperHqlServicePool = new HyperHqlServicePool(
				hypertableServerAddress, hypertableServerPort);

		// create the pool args
		HyperPoolArgs hyperPoolArgs = new HyperPoolArgs(hyperClientPool,
				hyperHqlServicePool, maxWaitMillis, tryGetCount,
				tryCreateCount, insertFlags, insertFlushInterval);

		return hyperPoolArgs;
	}
}
