package de.timmyrs.oneroute.proxy;

import de.timmyrs.oneroute.utils.IOStreamProxy;

import java.io.IOException;
import java.net.Socket;

class Client
{
	private final Socket sock;
	private final PortListener listener;
	private IOStreamProxy toTarget;
	private IOStreamProxy toClient;

	Client(Socket sock, PortListener listener)
	{
		this.sock = sock;
		this.listener = listener;
	}

	void proxyTo(Socket sock) throws IOException
	{
		this.toTarget = new IOStreamProxy(this.sock.getInputStream(), sock.getOutputStream());
		this.toClient = new IOStreamProxy(sock.getInputStream(), this.sock.getOutputStream());
	}

	void end()
	{
		if(this.toTarget != null)
		{
			this.toTarget.interrupt();
			this.toClient.interrupt();
			synchronized(this.listener.clients)
			{
				this.listener.clients.remove(this);
			}
		}
		if(!this.sock.isClosed())
		{
			try
			{
				this.sock.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}