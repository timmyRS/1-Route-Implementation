package de.timmyrs.oneroute.proxy;

import de.timmyrs.oneroute.enums.OneRoutePacket;
import de.timmyrs.oneroute.utils.PacketWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PortListener extends Thread
{
	final int port;
	final ArrayList<Client> clients = new ArrayList<>();
	final ArrayList<Client> waitingClients = new ArrayList<>();
	final Target target;
	private final ServerSocket socket;

	PortListener(int port, Target target)
			throws IOException
	{
		this.port = port;
		this.target = target;
		this.socket = new ServerSocket(port);
		new Thread(this, "Port Listener on port " + port).start();
	}

	public void run()
	{
		try
		{
			do
			{
				Socket sock = this.socket.accept();
				synchronized(this.waitingClients)
				{
					this.waitingClients.add(new Client(sock, this));
				}
				new PacketWriter(OneRoutePacket.CONNECT)
						.addUnsignedShort(this.port)
						.send(this.target.sock
								.getOutputStream());
				if(Thread.interrupted()) break;
			}
			while(!this.socket.isClosed());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(!this.socket.isClosed())
		{
			try
			{
				this.socket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}