package org.bladerunnerjs.spec;


import java.io.File;
import java.util.Date;
import java.util.Scanner;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.AdhocTimer;
import org.junit.Before;
import org.junit.Test;

public class DashboardLoadingTest extends SpecTest
{
	private App dashboard;
	
	public static void main(String[] args) throws Exception {
		try(Scanner keyboard = new Scanner(System.in)) {
			System.out.println("Press enter to continue");
			keyboard.nextLine();
			System.out.println("Running test...");
		}
		
		DashboardLoadingTest test = new DashboardLoadingTest();
		test.resetTestObjects();
		test.initTestObjects();
		test.dashboardTest();
		test.cleanUp();
	}
	
	@Before
	public void initTestObjects() throws Exception {
		testSdkDirectory = new File("../brjs-sdk/workspace/sdk/").getCanonicalFile();
		given(brjs).hasBeenAuthenticallyCreated();
		brjs.io().uninstallFileAccessChecker();
		dashboard = brjs.systemApp("dashboard");
	}
	
	@Test
	public void dashboardTest() throws Exception
	{
		String requestPath = "v/dev/js/dev/combined/bundle.js";
		StringBuffer response = new StringBuffer();
		long startTime, endTime;
		
		AdhocTimer.init();
		startTime = new Date().getTime();
	
		when(dashboard).requestReceived(requestPath, response);
		
		endTime = new Date().getTime();
		System.out.println("Cold Request: " + (endTime - startTime) + " ms");
		AdhocTimer.dump();
		
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
		brjs.getFileInfo(file).resetLastModified();
		
		while(!file.equals(brjs.dir())) {
			file = file.getParentFile();
			brjs.getFileInfo(file).resetLastModified();
		}
	}
}