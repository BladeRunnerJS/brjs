package org.bladerunnerjs.model;

import java.util.Date;


public class DefaultAppVersionGenerator implements AppVersionGenerator
{
	private String version = String.valueOf( new Date().getTime() );
	
	@Override
	public String getVersion()
	{
		return version;
	}
	
	@Override
	public String getVersionPattern()
	{
		return "([a-zA-Z0-9\\.\\-]+)";
	}

	@Override
	public void setVersion(String version)
	{
		checkVersionFormat(version);
		this.version = version;
	}
	
	private void checkVersionFormat(String version)
	{
		if (!version.matches(getVersionPattern())) {
			throw new IllegalArgumentException("The version did not match the pattern: '"+getVersionPattern()+"'.");
		}
	}

}
