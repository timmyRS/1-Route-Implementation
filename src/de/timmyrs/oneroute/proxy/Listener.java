package de.timmyrs.oneroute.proxy;

import de.timmyrs.oneroute.enums.OneRoutePacket;
import de.timmyrs.oneroute.main.SomethingWentWrongException;
import de.timmyrs.oneroute.proxy.configuration.ListenerConfig;
import de.timmyrs.oneroute.utils.OneRouteUtils;
import de.timmyrs.oneroute.utils.PacketWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread
{
	private final ListenerConfig config;
	private final ServerSocket socket;

	public Listener(ListenerConfig config)
			throws SomethingWentWrongException
	{
		this.config = config;
		try
		{
			this.socket = new ServerSocket(config.port);
		}
		catch(IOException e)
		{
			throw new SomethingWentWrongException("Couldn't bind to port " + config.port + ": " + e.getMessage());
		}
		System.out.println("Listening for Targets on port " + config.port);
		new Thread(this, "Listener on port " + config.port).start();
	}

	public void run()
	{
		try
		{
			do
			{
				Socket sock = this.socket.accept();
				if(OneRouteUtils.isIPallowed(sock.getInetAddress(), this.config.blacklist, this.config.blacklist))
				{
					new Target(sock, this.config);
				}
				else
				{
					new PacketWriter(OneRoutePacket.AUTH_RESPONSE)
							.addBoolean(false)
							.addByte((byte) 0x01)
							.send(sock.getOutputStream());
					sock.close();
				}
				if(Thread.interrupted()) break;
			}
			while(!this.socket.isClosed());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}