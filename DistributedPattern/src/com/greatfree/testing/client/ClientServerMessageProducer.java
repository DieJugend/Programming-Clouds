package com.greatfree.testing.client;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.concurrency.Threader;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular server that connects to a respective port on the client. Usually, each port aims to provide one particular service. 11/07/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerMessageProducer
{
	// The Threader aims to associate with the message producer to guarantee the producer can work concurrently. 11/07/2014, Bing Li
	private Threader<MessageProducer<ClientServerDispatcher>, ClientServerDispatcherDisposer> producerThreader;
	
	private ClientServerMessageProducer()
	{
	}
	
	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 11/07/2014, Bing Li
	 */
	private static ClientServerMessageProducer instance = new ClientServerMessageProducer();
	
	public static ClientServerMessageProducer CLIENT()
	{
		if (instance == null)
		{
			instance = new ClientServerMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the server is shutdown. 11/07/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.producerThreader.stop();
	}

	/*
	 * Initialize the message producers. It is invoked when the connection modules of the server is started since clients can send requests or notifications only after it is started. 11/07/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the message producer. A threader is associated with the message producer such that the producer is able to work in a concurrent way. 09/20/2014, Bing Li
		this.producerThreader = new Threader<MessageProducer<ClientServerDispatcher>, ClientServerDispatcherDisposer>(new MessageProducer<ClientServerDispatcher>(new ClientServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new ClientServerDispatcherDisposer());
		// Start the associated thread for the message producer. 09/20/2014, Bing Li
		this.producerThreader.start();
	}
	
	/*
	 * Assign messages, requests or notifications, to the bound message dispatcher such that they can be responded or dealt with. 11/07/2014, Bing Li
	 */
	public void produceMessage(OutMessageStream<ServerMessage> message)
	{
		this.producerThreader.getFunction().produce(message);
	}
}
