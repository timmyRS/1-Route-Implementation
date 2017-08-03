package de.timmyrs.oneroute.target;

import de.timmyrs.oneroute.enums.OneRoutePacket;
import de.timmyrs.oneroute.main.Main;
import de.timmyrs.oneroute.main.SomethingWentWrongException;
import de.timmyrs.oneroute.target.configuration.ProxyConfig;
import de.timmyrs.oneroute.target.configuration.SharedPort;
import de.timmyrs.oneroute.utils.PacketReader;
import de.timmyrs.oneroute.utils.PacketWriter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ControlConnection extends Thread
{
	final ProxyConfig proxyConfig;
	final ArrayList<ProxyConnection> proxyConnections = new ArrayList<>();
	final Socket sock;
	int lastData;
	private boolean authed = false;

	public ControlConnection(ProxyConfig proxyConfig) throws SomethingWentWrongException, IOException
	{
		try
		{
			this.sock = new Socket(proxyConfig.name, proxyConfig.port);
		}
		catch(IOException e)
		{
			throw new SomethingWentWrongException("Couldn't open Control Connection to " + proxyConfig.name + ": " + e.getMessage());
		}
		new PacketWriter(OneRoutePacket.AUTH)
				.addByte(Main.protocolVersion)
				.addString(proxyConfig.pass)
				.addByte((byte) 0x00)
				.send(this.sock
						.getOutputStream());
		this.proxyConfig = proxyConfig;
		new Thread(this, "Control Connection for " + proxyConfig.name).start();
	}

	public void run()
	{
		OutputStream out;
		try
		{
			BufferedInputStream in = new BufferedInputStream(this.sock.getInputStream());
			out = this.sock.getOutputStream();
			do
			{
				if(in.available() > 2)
				{
					lastData = (int) (System.currentTimeMillis() / 1000L);
					PacketReader reader = new PacketReader(in);
					byte packetId = reader.readByte();
					if(this.authed)
					{
						if(packetId == OneRoutePacket.UNACCEPTED_PORTS.id)
						{
							for(int i = reader.readByte(); i > 0; i--)
							{
								System.out.println("[" + this.proxyConfig.name + "] Couldn't open port " + reader.readUnsignedShort());
							}
						}
						else if(packetId == OneRoutePacket.CONNECT.id)
						{
							int proxyPort = reader.readUnsignedShort();
							SharedPort port = null;
							for(SharedPort sharedPort : this.proxyConfig.sharedPorts)
							{
								if(proxyPort != sharedPort.proxy_port)
									continue;
								port = sharedPort;
								break;
							}
							if(port != null)
							{
								try
								{
									ProxyConnection proxyConnection = new ProxyConnection(port, this);
									synchronized(this.proxyConnections)
									{
										this.proxyConnections.add(proxyConnection);
									}
								}
								catch(SomethingWentWrongException e)
								{
									System.out.println(e.getMessage());
								}
							}
						}
					}
					else if(packetId == OneRoutePacket.AUTH_RESPONSE.id)
					{
						if(reader.readBoolean())
						{
							this.authed = true;
							System.out.println("[" + this.proxyConfig.name + "] Authentication was successful.");
							PacketWriter writer = new PacketWriter(OneRoutePacket.PORTS_TO_OPEN)
									.addByte((byte) this.proxyConfig.sharedPorts.length);
							for(SharedPort port : proxyConfig.sharedPorts)
							{
								writer.addUnsignedShort(port.proxy_port);
							}
							writer.send(out);
							new ControlConnectionHeartbeater(this);
						}
						else
						{
							System.out.print("[" + this.proxyConfig.name + "] Authentication failed: ");
							switch(reader.readByte())
							{
								default:
									if(reader.length > 2)
									{
										System.out.println(reader.readString());
									}
									else
									{
										System.out.println("Unknown Reason");
									}
									break;
								case 1:
									System.out.println("IP-Address or Range not allowed.");
									break;
								case 2:
									System.out.println("Protocol Version not supported.");
									break;
								case 3:
									System.out.println("Incorrect Password.");
							}
							this.sock.close();
						}
					}
					reader.finish();
				}
				if(this.sock.isClosed()) break;
			}
			while(!Thread.interrupted());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		synchronized(this.proxyConnections)
		{
			for(ProxyConnection proxyConnection : this.proxyConnections)
			{
				proxyConnection.end();
			}
		}
		synchronized(Main.controlConnections)
		{
			Main.controlConnections.remove(this);
		}
		System.out.println("[" + this.sock.getInetAddress().toString() + "] Connections & ports have been closed.");
	}
}