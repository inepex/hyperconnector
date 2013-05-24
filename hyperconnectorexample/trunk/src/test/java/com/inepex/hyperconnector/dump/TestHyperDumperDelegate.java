package com.inepex.hyperconnector.dump;

import java.util.concurrent.TimeUnit;

import com.inepex.hyperconnector.ApplicationDelegate;

public class TestHyperDumperDelegate implements ApplicationDelegate {

	private Runnable shutDownTask;
	
	@Override
	public void addShutDownTask(Runnable runnable) {
		this.shutDownTask=runnable;
	}

	@Override
	public void scheduleAtFixedRate(Runnable task, long initialDelay,long period, TimeUnit unit) {
	}
	
	public void doShutDown(){
		shutDownTask.run();
	}

	@Override
	public void logInserting(int cellCount, long timeSpentInMs) {
	}
}