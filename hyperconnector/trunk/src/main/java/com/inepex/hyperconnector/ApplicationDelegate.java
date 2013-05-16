package com.inepex.hyperconnector;

import java.util.concurrent.TimeUnit;

public interface ApplicationDelegate {
	void addShutDownTask(Runnable runnable);
	void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit);
}
