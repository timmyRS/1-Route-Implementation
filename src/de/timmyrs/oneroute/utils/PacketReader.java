package de.timmyrs.oneroute.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PacketReader
{
	private final InputStream is;
	public int length = 0;

	public PacketReader(InputStream is) throws IOException
	{
		this.is = is;
		this.length = is.available();
		this.length = readUnsignedShort();
	}

	public void finish() throws IOException
	{
		if(this.length > 0)
		{
			System.out.print("Ignored " + this.length + " byte(s):");
			do
			{
				System.out.printf(" %02x", this.is.read());
				this.length -= 1;
			}
			while(this.length > 0);
			System.out.print("\n");
		}
	}

	public byte readByte() throws IOException
	{
		if(this.length < 1)
		{
			throw new RuntimeException("Packet isn't big enough to read byte.");
		}
		this.length -= 1;
		return (byte) this.is.read();
	}

	public boolean readBoolean() throws IOException
	{
		if(this.length < 1)
		{
			throw new RuntimeException("Packet isn't big enough to read boolean.");
		}
		this.length -= 1;
		return this.is.read() == 1;
	}

	public int readUnsignedShort() throws IOException
	{
		if(this.length < 2)
		{
			throw new RuntimeException("Packet isn't big enough to read unsigned short.");
		}
		this.length -= 2;
		return (this.is.read() & 0xFF) << 8 | this.is.read() & 0xFF;
	}

	public String readString() throws IOException
	{
		String result = "";
		int strlength = readUnsignedShort();
		if(strlength > -1)
		{
			if(strlength > this.length)
			{
				throw new RuntimeException("Packet isn't big enough to read string.");
			}
			byte[] bytes = new byte[strlength];
			int i = 0;
			this.length -= strlength;
			while(strlength > 0)
			{
				strlength--;
				bytes[(i++)] = (byte) this.is.read();
			}
			result = new String(bytes, StandardCharsets.UTF_8);
		}
		return result;
	}
}