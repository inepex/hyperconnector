package com.inepex.hyperconnector.thrift;


/**
 * Wrapper class for Hypertable pools and their settings to support dependency injection.
 * @author Gabor Dicso
 *
 */
public class HyperPoolArgs {
	protected HyperClientPool hyperClientPool;
	protected HyperHqlServicePool hyperHqlServicePool;
	protected long maxWaitMillis;
	protected int tryGetCount;
	protected int tryCreateCount;
	protected int insertFlags;
	protected int insertFlushInterval;

	protected HyperPoolArgs() {
	}

	public HyperPoolArgs(HyperClientPool hyperClientPool, HyperHqlServicePool hyperHqlServicePool, long maxWaitMillis, int tryGetCount, int tryCreateCount,
			int insertFlags, int insertFlushInterval) {
		this.hyperClientPool = hyperClientPool;
		this.hyperHqlServicePool = hyperHqlServicePool;
		this.maxWaitMillis = maxWaitMillis;
		this.tryGetCount = tryGetCount;
		this.tryCreateCount = tryCreateCount;
		this.insertFlags = insertFlags;
		this.insertFlushInterval = insertFlushInterval;
	}

	public HyperClientPool getHyperClientPool() {
		return hyperClientPool;
	}

	public HyperHqlServicePool getHyperHqlServicePool() {
		return hyperHqlServicePool;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public int getTryGetCount() {
		return tryGetCount;
	}

	public int getTryCreateCount() {
		return tryCreateCount;
	}

	public int getInsertFlags() {
		return insertFlags;
	}

	public int getInsertFlushInterval() {
		return insertFlushInterval;
	}

}
