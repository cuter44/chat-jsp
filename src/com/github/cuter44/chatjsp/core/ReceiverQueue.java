package com.github.cuter44.chatjsp.core;

import java.util.LinkedList;

public class ReceiverQueue
{
  // TOKEN
    public static class Token
    {
        public synchronized void waitMessage()
            throws InterruptedException
        {
            this.wait();
        }

        public synchronized void supplyMessage()
        {
            this.notify();
        }
    }

    private LinkedList<Token> tokens;

  // SINGLETON
    private static class Singleton
    {
        public static ReceiverQueue instance = new ReceiverQueue();
    }

    public static synchronized Token register(boolean isWriter)
    {
        if (isWriter)
        {
            while (!Singleton.instance.tokens.isEmpty())
            {
                Token t = Singleton.instance.tokens.remove();
                t.supplyMessage();
            }
            return(null);
        }
        else
        {
            Token t = new Token();
            Singleton.instance.tokens.add(t);
            return(t);
        }
    }

    private ReceiverQueue()
    {
        this.tokens = new LinkedList<Token>();
    }
}
