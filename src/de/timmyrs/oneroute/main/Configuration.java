package de.timmyrs.oneroute.main;

import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Configuration
{
	static File file;

	void save()
			throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
		writer.flush();
		writer.close();
	}
}