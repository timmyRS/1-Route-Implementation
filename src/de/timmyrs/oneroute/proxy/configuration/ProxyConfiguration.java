package de.timmyrs.oneroute.proxy.configuration;

import de.timmyrs.oneroute.main.Configuration;

public class ProxyConfiguration extends Configuration
{
	public final String type = "proxy";

	public ListenerConfig[] listeners = { new ListenerConfig() };
}