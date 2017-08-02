package de.timmyrs.oneroute.utils;

import de.timmyrs.oneroute.enums.OneRoutePacket;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PacketWriter
{
	private final ArrayList<Byte> bytes = new ArrayList<>();

	public PacketWriter(OneRoutePacket packet)
	{
		this.bytes.add(packet.id);
	}

	public void send(OutputStream stream) throws IOException
	{
		byte[] sendBytes = new byte[this.bytes.size() + 2];
		int i = 0;
		for(byte b : ByteBuffer.allocate(2).putShort((short) (this.bytes.size() & 0xFFFF)).array())
		{
			sendBytes[(i++)] = b;
		}
		for(Byte b : bytes)
		{
			sendBytes[(i++)] = b;
		}
		stream.write(sendBytes);
	}

	public PacketWriter addByte(byte value)
	{
		this.bytes.add(value);
		return this;
	}

	public PacketWriter addBoolean(boolean value)
	{
		this.bytes.add((byte) (value ? 1 : 0));
		return this;
	}

	public PacketWriter addUnsignedShort(int value)
	{
		for(byte b : ByteBuffer.allocate(2).putShort((short) (value & 0xFFFF)).array())
		{
			this.bytes.add(b);
		}
		return this;
	}

	public PacketWriter addString(String value)
	{
		addUnsignedShort(value.length());
		for(byte b : value.getBytes(StandardCharsets.UTF_8))
		{
			this.bytes.add(b);
		}
		return this;
	}
}