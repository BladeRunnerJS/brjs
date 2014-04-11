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
		testSdkDirectory = new File("../cutlass-sdk/workspace/sdk/").getCanonicalFile();
		given(brjs).hasBeenAuthenticallyCreated();
		brjs.io().uninstallFileAccessChecker();
		dashboard = brjs.systemApp("dashboard");
	}
	
	@Test
	public void dashboardTest() throws Exception
	{
		String requestPath = "/default-aspect/js/dev/en_GB/combined/bundle.js";
		StringBuffer response = new StringBuffer();
		long startTime, endTime;
		
		startTime = new Date().getTime();
		when(dashboard).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println("Cold Request: " + (endTime - startTime) + " ms");
		
		touchFile(dashboard.aspect("default").file("index.html"));
		touchFile(brjs.sdkLib("br").file("src/br/Core.js"));
		
		startTime = new Date().getTime();
		when(dashboard).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println("Warm Request: " + (endTime - startTime) + " ms (some files modified)");
		
		startTime = new Date().getTime();
		when(dashboard).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println("Hot Request: " + (endTime - startTime) + " ms");
	}
	
	private void touchFile(File file) {
		brjs.getModificationInfo(file).resetLastModified();
		
		while(!file.equals(brjs.dir())) {
			file = file.getParentFile();
			brjs.getModificationInfo(file).resetLastModified();
		}
	}
}