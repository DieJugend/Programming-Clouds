package com.greatfree.concurrency;

import com.greatfree.multicast.ServerMessage;

/*
 * The interface defines the method to create the instance of BoundNotificationQueue. It works with BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public interface BoundNotificationThreadCreatable<Notification extends ServerMessage, Binder extends MessageBindable<Notification>, NotificationThread extends BoundNotificationQueue<Notification, Binder>>
{
	public NotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, Binder binder);
}
