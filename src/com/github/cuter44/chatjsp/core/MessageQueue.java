package com.github.cuter44.chatjsp.core;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.github.cuter44.chatjsp.dao.Message;

import com.github.cuter44.util.dump.Dump;

public class MessageQueue
{
  // SINGLETON
    private static class Singleton
    {
        public static MessageQueue instance = new MessageQueue(60);
    }

    private LinkedList<Message> buffer;
    private int sizeLimit;
    private ReentrantReadWriteLock lock;

    private MessageQueue(int limit)
    {
        this.buffer = new LinkedList<Message>();
        this.sizeLimit = limit;
        lock = new ReentrantReadWriteLock(true);
    }

    public static void putMessage(Message msg)
    {
        MessageQueue mq = Singleton.instance;

        try
        {
            mq.lock.writeLock().lock();
            mq.buffer.add(msg);
            if (mq.buffer.size() > mq.sizeLimit)
                mq.buffer.remove();

            ReceiverQueue.register(true);
        }
        finally
        {
            mq.lock.writeLock().unlock();
        }
    }

    public static List<Message> getMessage(Date last)
    {
        MessageQueue mq = Singleton.instance;
        List<Message> result = new ArrayList<Message>();

        try
        {
            mq.lock.readLock().lock();
            Iterator<Message> iter = mq.buffer.descendingIterator();
            while (iter.hasNext())
            {
                Message m = iter.next();
                if (last.before(m.t))
                    result.add(m);
                else
                    break;
            }
            return(result);
        }
        finally
        {
            mq.lock.readLock().unlock();
        }
    }
}
