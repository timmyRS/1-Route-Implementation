package de.timmyrs.oneroute.target.configuration;

import de.timmyrs.oneroute.main.Configuration;

public class TargetConfiguration extends Configuration
{
	public final String type = "target";
	public ProxyConfig[] proxies = { new ProxyConfig() };
}