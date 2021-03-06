package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.concurrency.Threader;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client that connects to a respective port on the crawling server. Usually, each port aims to provide one particular service. 11/23/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlMessageProducer
{
	private Threader<MessageProducer<CrawlDispatcher>, CrawlMessageProducerDisposer> producerThreader;
	
	private CrawlMessageProducer()
	{
	}
	
	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 11/23/2014, Bing Li
	 */
	private static CrawlMessageProducer instance = new CrawlMessageProducer();
	
	public static CrawlMessageProducer CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the server is shutdown. 11/23/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.producerThreader.stop();
	}

	/*
	 * Initialize the message producers. It is invoked when the connection modules of the server is started since clients can send requests or notifications only after it is started. 08/22/2014, Bing Li
	 */
	public void Init()
	{
		// Initialize the crawling message producer. A threader is associated with the message producer such that the producer is able to work in a concurrent way. 11/23/2014, Bing Li
		this.producerThreader = new Threader<MessageProducer<CrawlDispatcher>, CrawlMessageProducerDisposer>(new MessageProducer<CrawlDispatcher>(new CrawlDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new CrawlMessageProducerDisposer());
		// Start the associated thread for the crawling message producer. 11/23/2014, Bing Li
		this.producerThreader.start();
	}
	
	/*
	 * Assign messages, requests or notifications, to the bound crawling message dispatcher such that they can be responded or dealt with. 11/23/2014, Bing Li
	 */
	public void produceMessage(OutMessageStream<ServerMessage> message)
	{
		this.producerThreader.getFunction().produce(message);
	}
}
