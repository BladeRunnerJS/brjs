package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VersionInfo
{
	private final BRJS brjs;
	private final FileUtil fileUtil;
	
	public VersionInfo(BRJS brjs)
	{
		try {
			this.brjs = brjs;
			fileUtil = new FileUtil(brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
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
				contents = fileUtil.readFileToString(versionFile);
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
