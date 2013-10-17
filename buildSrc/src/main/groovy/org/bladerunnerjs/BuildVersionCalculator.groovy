package org.bladerunnerjs

import org.gradle.api.Project

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class BuildVersionCalculator
{

	static String calculateVersion(Project p)
	{
		return "WIP"
	}
	
	static String calculateBuildDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy")
		DateFormat timestampFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm zz")
		Calendar cal = Calendar.getInstance()
		def buildDate = dateFormat.format(cal.getTime())
	
		return buildDate;
	}
	
}
