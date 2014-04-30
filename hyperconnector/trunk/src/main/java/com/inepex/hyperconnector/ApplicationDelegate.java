package com.inepex.hyperconnector;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface ApplicationDelegate {
	void addShutDownTask(Runnable runnable);
	void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit);
	void logInserting(int cellCount, long timeSpentInMs);
	public Future<?> submit(Runnable runnable);
}
