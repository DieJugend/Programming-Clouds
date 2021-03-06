package com.greatfree.concurrency;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.greatfree.util.UtilConfig;

/*
 * The class is a wrapper of the built-in class, ThreadPoolExecutor to ease the procedure of managing threads. 07/17/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class ThreadPool
{
	// The built-in thread pool from JDK. 08/10/2014, Bing Li
	private ThreadPoolExecutor threadPool;
	// The core pool size of the thread pool. 08/10/2014, Bing Li
	private int corePoolSize;
	// The max pool size of the thread pool. 08/10/2014, Bing Li
	private int maxPoolSize;
	// The time to keeo alive for an idle thread. 08/10/2014, Bing Li
	private long keepAliveTime;
	// The queue that all the threads are scheduled and kept. 08/10/2014, Bing Li
	private LinkedBlockingQueue<Runnable> taskQueue;

	/*
	 * Initialize the properties. 08/10/2014, Bing Li
	 */
	public ThreadPool(int corePoolSize, long keepAliveTime)
	{
		this.taskQueue = new LinkedBlockingQueue<Runnable>();
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = corePoolSize + UtilConfig.ADDTIONAL_THREAD_POOL_SIZE;
		this.keepAliveTime = keepAliveTime;
		this.threadPool = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveTime, TimeUnit.MILLISECONDS, this.taskQueue);
	}
	
	/*
	 * Shutdown the thread pool. 08/10/2014, Bing Li
	 */
	public synchronized void shutdown()
	{
		this.taskQueue.clear();
		this.threadPool.shutdownNow();
	}

	/*
	 * Get the core size of the thread pool. 08/10/2014, Bing Li
	 */
	public int getCorePoolSize()
	{
		return this.threadPool.getCorePoolSize();
	}

	/*
	 * Create a new thread pool if the previous one is shutdown. 08/10/2014, Bing Li
	 */
	public void reset()
	{
		if (this.threadPool.isShutdown())
		{
			if (this.keepAliveTime == Long.MAX_VALUE)
			{
				this.threadPool = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveTime, TimeUnit.MILLISECONDS, this.taskQueue);
			}
			else
			{
				this.threadPool = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveTime, TimeUnit.MILLISECONDS, this.taskQueue);
			}
		}
	}

	/*
	 * Execute the thread that derives from the class of Thread. 08/10/2014, Bing Li
	 */
	public void execute(Thread thread)
	{
		try
		{
			this.threadPool.execute(thread);
		}
		catch (RejectedExecutionException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Execute the thread that implements the interface of Runnable. 08/10/2014, Bing Li
	 */
	public void execute(Runnable thread)
	{
		try
		{
			this.threadPool.execute(thread);
		}
		catch (RejectedExecutionException e)
		{
			e.printStackTrace();
		}
	}
}
