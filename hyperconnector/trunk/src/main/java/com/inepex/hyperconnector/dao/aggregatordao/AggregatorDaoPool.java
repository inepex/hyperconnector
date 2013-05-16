package com.inepex.hyperconnector.dao.aggregatordao;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class AggregatorDaoPool {

	private static final int poolStartSize = 4;
	
	private static final Object lock = new Object();
	private static ScheduledExecutorService service;
	
	public static ScheduledExecutorService getService() {
		if(service!=null)
			return service;
		
		synchronized (lock) {
			if(service==null) {
				service = Executors.newScheduledThreadPool(poolStartSize, new ThreadFactory() {
					
					private final AtomicInteger counter = new AtomicInteger();
					
					@Override
					public Thread newThread(Runnable r) {
						Thread th = new Thread(r);
						th.setName("aggreagtorDao-"+counter.incrementAndGet());
						return th;
					}
				});
			}
		}
		
		return service;
	}
}
