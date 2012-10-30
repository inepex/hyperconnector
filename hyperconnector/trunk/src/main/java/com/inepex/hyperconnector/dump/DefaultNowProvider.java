package com.inepex.hyperconnector.dump;

public class DefaultNowProvider implements NowProvider {

	@Override
	public long now() {
		return System.currentTimeMillis();
	}

}
