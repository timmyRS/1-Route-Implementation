package de.timmyrs.oneroute.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOStreamProxy extends Thread
{
	private final InputStream in;
	private final OutputStream out;

	public IOStreamProxy(InputStream in, OutputStream out)
	{
		this.in = in;
		this.out = out;
		new Thread(this, "IO Stream Proxy").start();
		//new IOStreamProxyCloser(this);
	}

	public void run()
	{
		try
		{
			//noinspection InfiniteLoopStatement
			do
			{
				byte[] arr = new byte[this.in.available()];
				//noinspection ResultOfMethodCallIgnored
				this.in.read(arr, 0, arr.length);
				this.out.write(arr);
			}
			while(true);
		}
		catch(IOException ignored)
		{
		}
		try
		{
			this.in.close();
			this.out.close();
		}
		catch(IOException ignored)
		{
		}
	}
}