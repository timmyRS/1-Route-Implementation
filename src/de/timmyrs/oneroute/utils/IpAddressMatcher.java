package de.timmyrs.oneroute.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

class IpAddressMatcher
{
	private final int nMaskBits;
	private final InetAddress requiredAddress;

	IpAddressMatcher(String ipAddress)
	{
		if (ipAddress.indexOf(47) > 0)
		{
			String[] addressAndMask = split(ipAddress);
			assert (addressAndMask != null);
			ipAddress = addressAndMask[0];
			this.nMaskBits = Integer.parseInt(addressAndMask[1]);
		}
		else
		{
			this.nMaskBits = -1;
		}
		this.requiredAddress = parseAddress(ipAddress);
	}

	private static String[] split(String toSplit)
	{
		if (hasLength(toSplit))
		{
			int offset = toSplit.indexOf("/");
			if (offset < 0)
			{
				return null;
			}

			String beforeDelimiter = toSplit.substring(0, offset);
			String afterDelimiter = toSplit.substring(offset + 1);
			return new String[] { beforeDelimiter, afterDelimiter };
		}

		return null;
	}

	private static boolean hasLength(String str)
	{
		return (str != null) && (str.length() > 0);
	}

	boolean matches(String address)
	{
		InetAddress remoteAddress = parseAddress(address);
		if (!this.requiredAddress.getClass().equals(remoteAddress.getClass()))
		{
			return false;
		}
		if (this.nMaskBits < 0)
		{
			return remoteAddress.equals(this.requiredAddress);
		}

		byte[] remAddr = remoteAddress.getAddress();
		byte[] reqAddr = this.requiredAddress.getAddress();
		int oddBits = this.nMaskBits % 8;
		int nMaskBytes = this.nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
		byte[] mask = new byte[nMaskBytes];
		Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

		if (oddBits != 0)
		{
			int i = (1 << oddBits) - 1;
			i <<= 8 - oddBits;
			mask[(mask.length - 1)] = (byte)i;
		}
		for (int i = 0; i < mask.length; i++)
		{
			if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i]))
			{
				return false;
			}
		}
		return true;
	}

	private InetAddress parseAddress(String address)
	{
		try
		{
			return InetAddress.getByName(address);
		}
		catch (UnknownHostException var3)
		{
			throw new IllegalArgumentException("Failed to parse address " + address, var3);
		}
	}
}