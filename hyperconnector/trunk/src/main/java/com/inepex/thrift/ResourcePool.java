package com.inepex.thrift;


import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class ResourcePool<T> {
	private static final int DEFAULT_MAX_RESOURCES = 20;

	long lastLogDate = 0;
	long minLogInterval = 1000l * 60;
	
	final int maxResources;
	
	private final Semaphore sem;
	private final Queue<T> resources;
	
	protected Queue<T> getResourceQueue() {
		return resources;
	}
	

	public ResourcePool() {
		this(DEFAULT_MAX_RESOURCES);
	}

	public ResourcePool(int maxResources) {
		this.maxResources = maxResources;
		sem = new Semaphore(maxResources, true);
		resources = new ConcurrentLinkedQueue<T>();
	}

	
	public T getResource(long maxWaitMillis, int tryGetCount, int tryCreateCount) throws InterruptedException,
			ResourceCreationException {
		// validate input
		if (tryCreateCount < 1)
			tryCreateCount = 1;
		if (tryGetCount < 1)
			tryGetCount = 1;
		if (maxWaitMillis < 1000)
			maxWaitMillis = 1000;

		// First, get permission to take or create a resource
		sem.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS);
		
		while (tryGetCount > 0){
			T res = resources.poll();

			// Then, actually take one if available...
			if (res != null) { 
				if (isUsable(res)) {
					writeStatusToConsole();
					return res;
				}
				else {
					// if resource is not usable free it up, and try to get another
					try {
						destroyResource(res);
					} catch (Exception e) {
						System.out.println("Error while trying to destroy resource.");
						e.printStackTrace();
					}
				}	
			}	
			tryGetCount--;
		}

		// ...or create one if none available
		while (true) {
			try {
				T resource = createResource();
				writeStatusToConsole();
				return resource;
			} catch (Exception e) {
				e.printStackTrace();
				// Don't hog the permit if we failed to create a resource for more times then tryCreateCount! 
				if (--tryCreateCount == 0) {
					sem.release();
					System.out.println("Could not create resource!");
					return null;
				}
			}
		}
		
	}

	protected abstract T createResource() throws Exception;
	protected abstract void destroyResource(T resource);
	
	/**
	 * Override this to tell the resource pool if the given resource should be recycled or not
	 * @param resource
	 * @return
	 */
	protected abstract boolean isUsable(T resource);

	public void returnResource(T res) {
		if (res == null)
			return;
		if (isUsable(res))
			resources.add(res);
		sem.release();
		writeStatusToConsole();
	}
	
	private void writeStatusToConsole(){
		
		if ((new Date().getTime()) - lastLogDate < minLogInterval)
			return;
		
		lastLogDate = new Date().getTime();
//		System.out.println(getStatus());
	}
	
	public void destroyAllResource(){
		for (T resource : resources) {
			destroyResource(resource);
		}
	}
	
	public String getStatus() {
		return "Resources in use: " + (maxResources - sem.availablePermits())  +"; Resources in queue: " + resources.size();
	}
	
}
