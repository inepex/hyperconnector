package com.inepex.hyperconnector.dump;

public class TestNowProvider implements NowProvider {

	long now = System.currentTimeMillis();
	
	public void setOneHourLater() {
		now+=60*60*1000;
	}
	
	public void setNow(long now) {
		this.now = now;
	}

	@Override
	public long now() {
		return now;
	}
	
	public long nowPlusPlus() {
		return now++;
	}

}
