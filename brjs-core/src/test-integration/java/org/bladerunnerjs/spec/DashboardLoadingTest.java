package org.bladerunnerjs.spec;


import java.io.File;
import java.util.Date;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class DashboardLoadingTest extends SpecTest
{
	private App dashboard;

	@Before
	public void initTestObjects() throws Exception {
		testSdkDirectory = new File("../cutlass-sdk/workspace/sdk/");
		given(brjs).hasBeenAuthenticallyCreated();
		dashboard = brjs.systemApp("dashboard");
	}
	
	@Test
	public void dashboardTest() throws Exception
	{
		StringBuffer response = new StringBuffer();
		long startTime, endTime;
		
		startTime = new Date().getTime();
		when(dashboard).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		endTime = new Date().getTime();
		System.out.println("1st Request: " + (endTime - startTime) + " ms");
		
		startTime = new Date().getTime();
		when(dashboard).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		endTime = new Date().getTime();
		System.out.println("2nd Request: " + (endTime - startTime) + " ms");
	}
	
}