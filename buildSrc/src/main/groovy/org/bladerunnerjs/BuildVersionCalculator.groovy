package org.bladerunnerjs

import org.gradle.api.Project

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class BuildVersionCalculator
{
	
	Project project
	
	BuildVersionCalculator(Project project)
	{
		this.project = project;
	}
	
	//TODO: make this a static util class and stop passing in the project obj since we wont need it in Git land
	String calculateMajorVersion()
	{
		def branchDir = (project.parent != null) ? project.parent : project;
		return "WIP"//branchDir.getName()
	}
	
	String calculateMinorVersion()
	{
		def envP4Changelist = System.getenv()['GO_REVISION_P4_CHANGELIST']
		def changelist = ''
		def minorVersion
		if (envP4Changelist == null) {
			minorVersion = 'dev'
		} else {
			minorVersion = envP4Changelist
		}
		
		return minorVersion
	}

	String calculateVersion()
	{
		def majorVersion = calculateMajorVersion()
		def minorVersion = calculateMinorVersion()
		
		return majorVersion + ((minorVersion != '') ? '-'+minorVersion : '')
	}
	
	/* buildDate is the date, buildTimestamp is the current timestamp
	 * for a dev build the buildDate is used for the timestamp to prevent Gradle
	 * from running the generateVersionFile task on every build
	 */
	String calculateBuildDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy")
		DateFormat timestampFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm zz")
		Calendar cal = Calendar.getInstance()
		def buildDate = dateFormat.format(cal.getTime())
	
		return buildDate;
	}
	
	String calculateBuildTimestamp()
	{
		if (calculateMinorVersion().endsWith("dev"))
		{
			Calendar cal = Calendar.getInstance()
			DateFormat timestampFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm zz")
			return timestampFormat.format(cal.getTime())
		}
		else
		{
			calculateBuildDate()
		}
	}
	
}
