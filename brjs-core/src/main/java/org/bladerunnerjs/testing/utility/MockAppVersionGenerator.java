package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.AppVersionGenerator;


public class MockAppVersionGenerator implements AppVersionGenerator
{

	private String prodVersion = "prod";
	private String devVersion = "dev";

	@Override
	public String getProdVersion()
	{
		return prodVersion;
	}

	@Override
	public String getDevVersion()
	{
		return devVersion;
	}

	@Override
	public String getVersionPattern()
	{
		return "("+devVersion+"|"+prodVersion+")";
	}

	public void setProdVersion(String version)
	{
		prodVersion = version;
	}
	
	public void setDevVersion(String version)
	{
		devVersion = version;
	}
	
}
