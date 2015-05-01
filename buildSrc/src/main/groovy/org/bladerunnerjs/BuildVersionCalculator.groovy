package org.bladerunnerjs

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.BuildAdapter
import org.gradle.BuildResult

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class BuildVersionCalculator
{

	public static final DIRTY_TAG = "-DEV"
	
	static String calculateMajorVersion(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		p.exec {
			commandLine 'git', 'describe', '--abbrev=0'
			standardOutput = stdout
			errorOutput = stderr
		}
		def majorVersion = stdout.toString().trim()
		def version = calculateVersion(p)
		if (!version.startsWith(majorVersion))
		{
			throw new GradleException("majorVersion '${majorVersion} isn't the same as the major part of version '${version}', something may be wrong with tags and commits. Try using 'git describe' to debug.")
		}
		return majorVersion
	}
	
	static String calculateVersion(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		try
		{
			p.exec {
				commandLine 'git', 'describe', '--long', "--dirty=${DIRTY_TAG}"
				standardOutput = stdout
				errorOutput = stderr
			}
			return stdout.toString().trim().replaceFirst("-g",".g"); // replace the -g<commit> with .g<commit>
		}
		catch (ex)
		{
			p.logger.error "Error calculating version using 'git describe'. Command stderr was:  '${stderr.toString()}'."

			stdout = new ByteArrayOutputStream()
			stderr = new ByteArrayOutputStream()
			p.exec {
				commandLine 'git', 'rev-parse', '--short', 'HEAD'
				standardOutput = stdout
				errorOutput = stderr
			}
			return "v0.0-${stdout.toString().trim()}${DIRTY_TAG}"
		}
	}
	
	static boolean isVersionDirty(Project p)
	{
		return calculateVersion(p).endsWith(DIRTY_TAG)
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
		
		hostname = "localhost";
		p.logger.warn "The hostname was calculated as '${hostname}', you should set your hostname so this build can be traced back to this machine."
		return hostname
	}

	static String calculateCurrentBranch(Project p)
	{
		def stdout = new ByteArrayOutputStream()
		def stderr = new ByteArrayOutputStream()
		p.exec {
			commandLine 'git', 'rev-parse', '--abbrev-ref', 'HEAD'
			standardOutput = stdout
			errorOutput = stderr
		}
		def branch = stdout.toString().trim()
		return branch
	}
	
	
	////////////////////////////////////////////////////////
	
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
