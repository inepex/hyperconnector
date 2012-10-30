package com.inepex.hyperconnector.dump;

import java.util.concurrent.TimeUnit;

public class TestHyperDumperDelegate implements HyperDumperDelegate {

	private Runnable shutDownTask;
	
	@Override
	public void addShutDownTask(Runnable runnable) {
		this.shutDownTask=runnable;
	}

	@Override
	public void scheduleAtFixedRate(Runnable task, long initialDelay,
			long period, TimeUnit unit) {
	}
	
	public void doShutDown(){
		shutDownTask.run();
	}
}