package de.timmyrs.oneroute.target;

import de.timmyrs.oneroute.enums.OneRoutePacket;
import de.timmyrs.oneroute.utils.PacketWriter;

public class ControlConnectionHeartbeater extends Thread
{
	private final ControlConnection connection;

	ControlConnectionHeartbeater(final ControlConnection connection)
	{
		this.connection = connection;
		new Thread(this, "Control Connection Heartbeat for " + connection.proxyConfig.name).start();
	}

	@Override
	public void run()
	{
		try
		{
			do
			{
				Thread.sleep(15000);
				if((connection.lastData + 30) < (int) (System.currentTimeMillis() / 1000L))
				{
					connection.sock.close();
				}
				else
				{
					new PacketWriter(OneRoutePacket.PORTS_TO_OPEN)
							.addByte((byte) 0)
							.send(connection.sock.getOutputStream());
				}
			}
			while(!Thread.interrupted() && !connection.sock.isClosed());
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
