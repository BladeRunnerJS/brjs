package org.bladerunnerjs.spec;


import java.io.File;
import java.util.Date;
import java.util.Scanner;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class DashboardLoadingTest extends SpecTest
{
	private App app;
	private FileModificationRegistry modificationRegistry;
	
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
		testSdkDirectory = new File("../brjs-sdk/sdk/").getAbsoluteFile();
		System.out.print("BRJS initialization: ");
		long startTime = new Date().getTime();
		given(brjs).hasBeenAuthenticallyCreated();
		long endTime = new Date().getTime();
		System.out.println((endTime - startTime) + " ms");
		brjs.io().uninstallFileAccessChecker();
		app = brjs.app("dashboard");
		modificationRegistry = brjs.getFileModificationRegistry();
		cleanupTestSdkDirectory = false;
	}
	
	@Test
	public void dashboardTest() throws Exception
	{
		String requestPath = "v/dev/js/dev/combined/bundle.js";
		StringBuffer response = new StringBuffer();
		long startTime, endTime;
		
		System.out.print("Cold Request: ");
		startTime = new Date().getTime();
		when(app).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println((endTime - startTime) + " ms");
		
		System.out.print("Cold Request: ");
		modificationRegistry.incrementAllFileVersions();
		startTime = new Date().getTime();
		when(app).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println((endTime - startTime) + " ms (all files modified but BRJS initialized)");
		
		System.out.print("Warm Request: ");
		modificationRegistry.incrementChildFileVersions(app.aspect("default").file("index.html"));
		modificationRegistry.incrementChildFileVersions(brjs.sdkLib("br").file("src/br/Core.js"));
		startTime = new Date().getTime();
		when(app).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println((endTime - startTime) + " ms (some files modified)");
		
		System.out.print("Hot Request: ");
		startTime = new Date().getTime();
		when(app).requestReceived(requestPath, response);
		endTime = new Date().getTime();
		System.out.println((endTime - startTime) + " ms");
	}
	
}