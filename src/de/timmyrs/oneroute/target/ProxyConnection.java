package de.timmyrs.oneroute.target;

import de.timmyrs.oneroute.enums.OneRoutePacket;
import de.timmyrs.oneroute.main.Main;
import de.timmyrs.oneroute.main.SomethingWentWrongException;
import de.timmyrs.oneroute.target.configuration.SharedPort;
import de.timmyrs.oneroute.utils.IOStreamProxy;
import de.timmyrs.oneroute.utils.PacketReader;
import de.timmyrs.oneroute.utils.PacketWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ProxyConnection extends Thread
{
	private final SharedPort port;
	private final ControlConnection controlConnection;
	private final Socket proxySock;
	private Socket localSock;
	private IOStreamProxy toProxy;
	private IOStreamProxy toLocal;

	ProxyConnection(SharedPort port, ControlConnection controlConnection)
			throws SomethingWentWrongException, IOException
	{
		this.port = port;
		this.controlConnection = controlConnection;
		try
		{
			this.proxySock = new Socket(controlConnection.proxyConfig.name, controlConnection.proxyConfig.port);
		}
		catch(IOException e)
		{
			throw new SomethingWentWrongException("Couldn't open Proxy Connection to " + controlConnection.proxyConfig.name + ": " + e.getMessage());
		}
		new PacketWriter(OneRoutePacket.AUTH)
				.addByte(Main.protocolVersion)
				.addString(controlConnection.proxyConfig.pass)
				.addByte((byte) 0x01)
				.addUnsignedShort(port.proxy_port)
				.send(this.proxySock
						.getOutputStream());
		new Thread(this, "Proxy Connection to " + controlConnection.proxyConfig.name).start();
	}

	void end()
	{
		if(this.toLocal != null)
		{
			this.toLocal.interrupt();
		}
		if(this.toProxy != null)
		{
			this.toProxy.interrupt();
		}
		if(!this.proxySock.isClosed())
		{
			try
			{
				this.proxySock.close();
			}
			catch(IOException ignored)
			{
			}
		}
		if((this.localSock != null) && (!this.localSock.isClosed()))
		{
			try
			{
				this.localSock.close();
			}
			catch(IOException ignored)
			{
			}
		}
		synchronized(this.controlConnection.proxyConnections)
		{
			this.controlConnection.proxyConnections.remove(this);
		}
	}

	public void run()
	{
		try
		{
			InputStream in = this.proxySock.getInputStream();
			while(in.available() < 3)
			{
				Thread.sleep(10L);
			}
			PacketReader reader = new PacketReader(in);
			byte packetId = reader.readByte();
			if(packetId == OneRoutePacket.AUTH_RESPONSE.id)
			{
				if(reader.readBoolean())
				{
					reader.finish();
					try
					{
						this.localSock = new Socket("localhost", this.port.local_port);
					}
					catch(IOException e)
					{
						throw new SomethingWentWrongException("localhost:" + this.port.local_port + " rejected connection: " + e.getMessage());
					}
					this.toLocal = new IOStreamProxy(this.proxySock.getInputStream(), this.localSock.getOutputStream());
					this.toProxy = new IOStreamProxy(this.localSock.getInputStream(), this.proxySock.getOutputStream());
				}
				else
				{
					if(reader.readByte() == 0)
					{
						throw new SomethingWentWrongException("Authentication for Proxy Connection failed: " + reader.readString());
					}
					throw new SomethingWentWrongException("Authentication for Proxy Connection failed. I don't know how this could happen.");
				}
			}
		}
		catch(SomethingWentWrongException e)
		{
			System.out.println(e.getMessage());
			end();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}