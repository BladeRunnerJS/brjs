package org.bladerunnerjs.model;

import java.util.Date;


public class DefaultAppVersionGenerator implements AppVersionGenerator
{
	private String devVersion = null;
	private String prodVersion = null;
	
	@Override
	public String getProdVersion()
	{
		if (prodVersion != null) {
			return prodVersion;
		}
		return String.valueOf( new Date().getTime() );
	}
	
	@Override
	public String getDevVersion()
	{
		if (devVersion != null) {
			return devVersion;
		}
		return "dev";
	}
	
	@Override
	public String getVersionPattern()
	{
		return "([a-zA-Z0-9\\.\\-]+)";
	}

	@Override
	public void setProdVersion(String version)
	{
		checkVersionFormat(version);
		prodVersion = version;
	}
	
	@Override
	public void setDevVersion(String version)
	{
		checkVersionFormat(version);
		devVersion = version;
	}
	
	private void checkVersionFormat(String version)
	{
		if (!version.matches(getVersionPattern())) {
			throw new IllegalArgumentException("The version did not match the pattern: '"+getVersionPattern()+"'.");
		}
	}

}
