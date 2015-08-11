package org.bladerunnerjs.model;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DefaultAppVersionGenerator implements AppVersionGenerator
{
	private String version = null;
	private boolean appendTimetamp = true;
	
	@Override
	public String getVersion()
	{
		if (version == null) {
			return getTimestamp();
		}
		if (appendTimetamp) {
			return version+"-"+getTimestamp();
		}
		return version;
	}
	
	@Override
	public void appendTimetamp(boolean appendTimetamp)
	{
		this.appendTimetamp = appendTimetamp;
	}

	@Override
	public String getVersionPattern()
	{
		return "([a-zA-Z0-9\\.\\-_]+)";
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

	private String getTimestamp() {
		return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	}
}
