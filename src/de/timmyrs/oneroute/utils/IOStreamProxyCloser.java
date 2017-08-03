package de.timmyrs.oneroute.utils;

public class IOStreamProxyCloser extends Thread
{
	private final IOStreamProxy ioStreamProxy;

	IOStreamProxyCloser(final IOStreamProxy ioStreamProxy)
	{
		this.ioStreamProxy = ioStreamProxy;
		new Thread(this, "IO Stream Proxy Closer").start();
	}

	@Override
	public void run()
	{
		try
		{
			do
			{
				Thread.sleep(15000);
				if((ioStreamProxy.lastData + 30) < (int) (System.currentTimeMillis() / 1000L))
				{
					ioStreamProxy.interrupt();
					break;
				}
			}
			while(!Thread.interrupted());
		}
		catch(InterruptedException ignored)
		{
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
