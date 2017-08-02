package de.timmyrs.oneroute.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.timmyrs.oneroute.proxy.Listener;
import de.timmyrs.oneroute.proxy.PortListener;
import de.timmyrs.oneroute.proxy.configuration.ListenerConfig;
import de.timmyrs.oneroute.proxy.configuration.ProxyConfiguration;
import de.timmyrs.oneroute.target.ControlConnection;
import de.timmyrs.oneroute.target.configuration.ProxyConfig;
import de.timmyrs.oneroute.target.configuration.TargetConfiguration;

import java.io.*;
import java.util.ArrayList;

public class Main
{
	public static final ArrayList<ControlConnection> controlConnections = new ArrayList<>();
	public static final ArrayList<PortListener> portListeners = new ArrayList<>();
	public static final byte protocolVersion = 1;
	private static final Gson gson = new Gson();

	public static void main(String[] args)
	{
		Configuration config = null;
		try
		{
			if(args.length > 0)
			{
				Configuration.file = new File(args[0]);
				if(!Configuration.file.exists())
				{
					if(Configuration.file.createNewFile())
					{
						BufferedWriter writer = new BufferedWriter(new FileWriter(Configuration.file));
						writer.write("{\"type\":\"proxy or target\"}");
						writer.flush();
						writer.close();
					}
					else
					{
						System.out.println(Configuration.file + " doesn't exist and couldn't be created.");
						return;
					}
				}
				JsonObject json = new JsonParser().parse(new BufferedReader(new FileReader(Configuration.file))).getAsJsonObject();
				if(json.get("type").getAsString().equals("proxy or target"))
				{
					System.out.println(Configuration.file + " doesn't have a \"type\" value set.");
				}
				else
				{
					switch(json.get("type").getAsString())
					{
						default:
							System.out.println("\"type\" has to be either \"proxy\" or \"target\".");
							return;
						case "proxy":
							config = gson.fromJson(new BufferedReader(new FileReader(Configuration.file)), ProxyConfiguration.class);
							for(ListenerConfig listenerConfig : ((ProxyConfiguration) config).listeners)
							{
								new Listener(listenerConfig);
							}
							break;
						case "target":
							config = gson.fromJson(new BufferedReader(new FileReader(Configuration.file)), TargetConfiguration.class);
							synchronized(controlConnections)
							{
								for(ProxyConfig p : ((TargetConfiguration) config).proxies)
								{
									controlConnections.add(new ControlConnection(p));
								}
							}
					}
				}
			}
			else
			{
				System.out.println("Please provide a configuration file (.json) as argument.");
			}
		}
		catch(SomethingWentWrongException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(config != null)
		{
			try
			{
				config.save();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}