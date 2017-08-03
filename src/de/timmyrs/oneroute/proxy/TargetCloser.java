package de.timmyrs.oneroute.proxy;

public class TargetCloser extends Thread
{
	private final Target target;

	TargetCloser(final Target target)
	{
		this.target = target;
		new Thread(this, "Target " + target.sock.getInetAddress().toString() + " Heartbeat").start();
	}

	@Override
	public void run()
	{
		try
		{
			do
			{
				Thread.sleep(15000);
				if((target.lastData + 30) < (int) (System.currentTimeMillis() / 1000L))
				{
					target.sock.close();
				}
			}
			while(!Thread.interrupted() && !target.sock.isClosed());
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
