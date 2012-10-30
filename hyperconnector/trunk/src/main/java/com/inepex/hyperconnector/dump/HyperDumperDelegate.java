package com.inepex.hyperconnector.dump;

import java.util.concurrent.TimeUnit;

public interface HyperDumperDelegate {
	void addShutDownTask(Runnable runnable);
	void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit);
}
