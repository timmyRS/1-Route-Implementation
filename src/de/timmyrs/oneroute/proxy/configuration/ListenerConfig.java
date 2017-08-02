package de.timmyrs.oneroute.proxy.configuration;

public class ListenerConfig
{
	public int port = 197;

	public String pass = "";

	public String[] blacklist = { "0.0.0.0/0", "[0:0:0:0:0:0:0:0]/0" };

	public String[] whitelist = { "1.1.1.1" };
}