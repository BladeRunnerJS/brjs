package org.bladerunnerjs

import org.gradle.api.GradleException
import org.gradle.api.Project

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class BuildVersionCalculator
{

	static String calculateVersion(Project p)
	{
		def versionString = attemptToGetDescribeString(p)
		if ( !versionString.equals("") )
		{
			return versionString
		}
		
		versionString = getVersionFallback(p)
		if ( !versionString.equals("") )
		{
			return versionString
		}
		
		throw new GradleException("Unable to detirmine buildVersion")
	}
	
	static String calculateBuildDate(Project p)
	{
		DateFormat timestampFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm zz")
		Calendar cal = Calendar.getInstance()
		def buildDate = timestampFormat.format(cal.getTime())
	
		return buildDate;
	}
	
	static String calculateBuildHostname(Project p)
	{
		String hostname = getHostnameUsingHostnameCommand(p)
		if ( !hostname.equals("") && !hostname.contains("localhost") )
		{
			return hostname
		}
		
		hostname = getHostnameUsingReverseLookup(p)
		if ( !hostname.equals("") && !hostname.contains("localhost") )
		{
			return hostname
		}
		
		throw new GradleException("Unable to detirmine hostname")
	}
	
	
	////////////////////////////////////////////////////////
	
	private static String attemptToGetDescribeString(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		try 
		{
			p.exec {
				commandLine 'git', 'describe', '--tags', '--long', '--dirty'
				standardOutput = stdout
				errorOutput = stderr
			}
			return stdout.toString().trim()
		}
		catch (ex)
		{
			p.logger.error "Error calculating version using 'git describe'. Command stderr was:  '${stderr.toString()}'"
			return "" 			
		}
	}
	
	private static String getVersionFallback(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		try
		{
			p.exec {
				commandLine 'git', 'rev-parse', '--short', 'HEAD'
				standardOutput = stdout
				errorOutput = stderr
			}
			return "0.0.0-"+stdout.toString().trim()
		}
		catch (ex)
		{
			p.logger.error "Error calculating version using 'git rev-parse'. Command stderr was:  '${stderr.toString()}'"
			return ""
		}
	}
	
	private static String getHostnameUsingHostnameCommand(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		try
		{
			p.exec {
				commandLine 'hostname'
				standardOutput = stdout
				errorOutput = new ByteArrayOutputStream()
			}
			return stdout.toString().trim()
		}
		catch(ex)
		{
			p.logger.error "Error running hostname command. Command stderr was:  '${stderr.toString()}'"
			return ""
		}
	}
	
	private static String getHostnameUsingReverseLookup(Project p)
	{
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (ex) {
			p.logger.error "Error finding hostname. Exception was: '${ex.toString()}'"
			return ""
		}
	}
	
}
