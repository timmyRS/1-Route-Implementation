package de.timmyrs.oneroute.utils;

import java.net.InetAddress;

public class OneRouteUtils
{
	public static boolean isIPallowed(InetAddress rawip, String[] blacklist, String[] whitelist)
	{
		return isIPallowed(rawip.getHostName(), blacklist, whitelist);
	}

	public static boolean isIPallowed(String ip, String[] blacklist, String[] whitelist)
	{
		boolean allowed = true;
		for (String blacklisted : blacklist)
		{
			if (!new IpAddressMatcher(blacklisted).matches(ip))
				continue;
			allowed = false;
			break;
		}

		if (!allowed)
		{
			for (String whitelisted : whitelist)
			{
				if (!new IpAddressMatcher(whitelisted).matches(ip))
					continue;
				allowed = true;
				break;
			}
		}

		return allowed;
	}
}