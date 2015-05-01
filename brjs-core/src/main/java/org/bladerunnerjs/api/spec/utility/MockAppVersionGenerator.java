package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.model.AppVersionGenerator;


public class MockAppVersionGenerator implements AppVersionGenerator
{

	String version = "dev";
	
	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public String getVersionPattern()
	{
		return "("+version+")";
	}

	@Override
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	@Override
	public void appendTimetamp(boolean appendTimetamp)
	{		
	}
	
}
