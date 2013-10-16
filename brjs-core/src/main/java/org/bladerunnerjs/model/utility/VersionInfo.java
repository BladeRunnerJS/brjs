package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VersionInfo
{
	private final BRJS brjs;
	
	public VersionInfo(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	// TODO: replace this method with a save facility, so we can encapsulate all of the version file functionality in this class
	public File getFile()
	{
		return brjs.file("sdk/version.txt");
	}
	
	public String getVersionNumber()
	{
		return getValueFromVersionFile("Version");
	}
	
	public String getBuildDate()
	{
		return getValueFromVersionFile("BuildDate");
	}
	
	@Override
	public String toString()
	{
		return BRJS.PRODUCT_NAME + " version: " + getVersionNumber() + ", built: " + getBuildDate();
	}
	
	private String getValueFromVersionFile(String key)
	{
		File versionFile = getFile();
		
		if (versionFile.exists())
		{
			String contents;
			try
			{
				contents = FileUtils.readFileToString(versionFile);
			}
			catch (IOException e)
			{
				return "";
			}
			JsonObject json = new JsonParser().parse(contents).getAsJsonObject();
			String value = json.get(key).toString(); 
			if (value.startsWith("\""))
			{
				value = value.replaceFirst("\"", "");
			}
			if (value.endsWith("\""))
			{
				value = value.substring(0, value.length() -1);
			}
			return value;
		}
		return "";
	}
	
}
