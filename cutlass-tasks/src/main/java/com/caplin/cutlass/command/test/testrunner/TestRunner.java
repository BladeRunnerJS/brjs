package com.caplin.cutlass.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer;
import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.apache.tools.ant.types.FileSet;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;

import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.test.BrowserNotFoundException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

import com.caplin.cutlass.conf.TestRunnerConfiguration;

import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.ProcessLogger;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.file.RelativePath;
import com.esotericsoftware.yamlbeans.YamlException;
import com.google.common.base.Joiner;

public class TestRunner {
	public class Messages {
		public static final String SERVER_STOP_INSTRUCTION_MESSAGE = "Press Ctrl + C to stop the server";
	}
	
	private BRJS brjs = BRJSAccessor.root;
	private Logger logger = brjs.logger(LoggerType.COMMAND, TestRunner.class);
	public enum TestType {UTs, ATs, UTsAndATs, ITs, ALL};
	
	protected static final int DEFAULT_SLEEP_TIME = 500;
	//private static final int SERVER_POLL_TIME = 50;
	private static final int SERVER_READ_TIMEOUT = 2500;
	//private static final int SERVER_AND_BROWSER_TIMEOUT = 30000;
	
	private static final int BROWSER_TIMEOUT= 100000;

	private static final int SERVER_AND_BROWSER_TIMEOUT = 90000;
	private static final int SERVER_POLL_TIME = 1000;
	
	private static Pattern pattern = Pattern.compile(".*Captured Browsers: \\((\\d+)\\).*", Pattern.DOTALL);
	private static Runtime runTime = Runtime.getRuntime();
	
	private List<Process> childProcesses = new ArrayList<Process>();
	private List<ProcessLogger> childLoggers = new ArrayList<ProcessLogger>();
	private File jsTestDriverJar;
	private int portNumber;
	private List<String> browsers;
//	private File resultDir; //TODO:uncomment
	private boolean verbose;
	private boolean generateReports;
	private long execStartTime;
	private long execEndTime;
	private TestRunnerConfiguration config;
	private List<TestRunResult> testResultList = new ArrayList<TestRunResult>();
	
	static boolean disableLogging = false;
	
	
	public TestRunner(File configFile, File resultDir, List<String> browserNames) throws FileNotFoundException, YamlException, IOException {
		this(configFile, resultDir, browserNames, false);
	}
	
	public TestRunner(File configFile, File resultDir, List<String> browserNames, boolean generateReports) throws FileNotFoundException, YamlException, IOException {
		verbose = determineIfVerbose();
		config = TestRunnerConfiguration.getConfiguration(configFile, browserNames);
		
		this.jsTestDriverJar = config.getJsTestDriverJarFile();
		this.portNumber = config.getPortNumber();
		this.browsers = config.getBrowsers();
//		this.resultDir = resultDir;
		this.generateReports = generateReports;
		addShutDownHook();
	}
	
	public void runServer() throws Exception {
		boolean serverStarted = startServer();
		
		if(serverStarted) {
			long startTime = System.currentTimeMillis();
			
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME); // slight pause before we display message in case there is any browser output
				logger.info("Server running, " + Messages.SERVER_STOP_INSTRUCTION_MESSAGE);
				logger.info("");
				
				while(System.in.available() == 0) {
					Thread.sleep(DEFAULT_SLEEP_TIME);
				}
			}
			finally {
				stopChildProcesses();
				long duration = System.currentTimeMillis() - startTime;
				logger.info("Server running for " + ((duration / 1000) / 60) + "min(s)");
			}
		}
	}
	
	public boolean runTests(File directory, TestType testType) throws Exception {
		execStartTime= System.currentTimeMillis();
		
		try {
			startServer();
			
			File testResultsDir = new File("../"+CutlassConfig.XML_TEST_RESULTS_DIR);
			if (testResultsDir.exists())
			{
				FileUtils.deleteDirectory(testResultsDir);
			}
			
			runAllTestsInDirectory(directory, directory, testType, true);
			if(testResultList.size() == 0) {
				logger.warn("Could not find any tests of type '" +testType.toString() + "' inside " +directory);
				return false;
			}
			return getSuccess();
		}
		finally {
			stopChildProcesses();
			execEndTime = System.currentTimeMillis();
			displayTimeInfo();
		}
	}
	
	private boolean determineIfVerbose() {
		boolean isVerbose;
		
		try {
			LogLevel logLevel = StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger().getLogLevel();
			isVerbose = (logLevel == LogLevel.DEBUG);
		}
		catch(NoSuchMethodError e) {
			// the tests are being run through the dashboard, where we will be using J2EE logging
			isVerbose = false;
		}
		
		return isVerbose;
	}
	
	private void displayTimeInfo()
	{
		long duration = execEndTime-execStartTime;
		logger.info("\n");
		if (getTestResultList().size() > 1)
		{
			printReport();
		}
		logger.info("- Time Taken: " + duration/1000 + "secs");		
		if (generateReports)
		{
			convertResultsToHTML();
		}
	}

	private void printReport() {
		logger.info("== Runner Report ==");
		if(!getSuccess())
		{
			logger.info("- Tests Failed :");
			List<TestRunResult> failedTests = getFailedTestList();
			if (failedTests.size() > 0)
			{
				for (TestRunResult failedTest : failedTests)
				{
					logger.info("  " + getFriendlyTestPath(failedTest.getBaseDirectory(),
						new File(failedTest.getTestDirectory(), "js-test-driver/jsTestDriver.conf")));
				}
			} else
			{
				logger.info("- Tests Failed");
			}
		} else {
			logger.info("- Tests Passed");
		}
		logger.info("\n");
	}
	
	private void convertResultsToHTML()
	{
		logger.info("\n");
		
		//This is here due to a bug in ant, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=384757#c13 for more details.
		System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");

		File htmlReportsDir = new File("../"+CutlassConfig.HTML_TEST_RESULTS_DIR);
		if (!htmlReportsDir.exists())
		{
			htmlReportsDir.mkdirs();
		}	
		Project project = new Project();
		project.setName("tmpProject");
		project.init();
		Target target = new Target();
		target.setName("junitreport");
		project.addTarget(target);

		FileSet fs = new FileSet();
		fs.setDir(new File("../"+CutlassConfig.XML_TEST_RESULTS_DIR));
		fs.createInclude().setName("TEST-*.xml");
		XMLResultAggregator aggregator = new XMLResultAggregator();
		aggregator.setProject(project);
		aggregator.addFileSet(fs);
		aggregator.setTodir(new File("../"+CutlassConfig.XML_TEST_RESULTS_DIR));
		
		AggregateTransformer transformer = aggregator.createReport();
		transformer.setTodir(new File("../"+CutlassConfig.HTML_TEST_RESULTS_DIR));		
		target.addTask(aggregator);
		
		logger.info("Writing HTML reports to " + "../"+CutlassConfig.HTML_TEST_RESULTS_DIR + ".");
		project.executeTarget("junitreport");
	}
	
	private boolean getSuccess() {
		boolean success = true;
		
		for(TestRunResult testRunResult : testResultList) {
			if(!testRunResult.getSuccess()) {
				success = false;
				break;
			}
		}
		
		return success;
	}

	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{
				logger.debug("running shutdown hook");
				try {
					stopChildProcesses();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}});
	}
	
	private boolean startServer() throws Exception {
		boolean serverStarted = false;
		
		if(isServerRunning()) {
			logger.info("Server already running, not bothering to start a new instance...");
		}
		else {
			startServerProcess();
			startBrowserProcesses();
			serverStarted = true;
		}
		
		return serverStarted;
	}
	
	public void runAllTestsInDirectory(File baseDirectory, File directory, TestType testType) throws Exception {
		runAllTestsInDirectory(baseDirectory, directory, testType, false);
	}
	
	public void runAllTestsInDirectory(File baseDirectory, File directory, TestType testType, boolean resetServer) throws Exception {
		if (baseDirectory == null || !baseDirectory.exists()) {
			String failureMessage = "Base directory '" + baseDirectory +"' does not exist";
			logger.warn(failureMessage);
			throw new IOException(failureMessage);
		}
		
		File[] dirContents = FileUtility.sortFileArray(directory.listFiles());
		reverseDirectoryContentsIfContainsTestDir(dirContents);
		for(File file : dirContents) {
			if(file.isDirectory() && !file.isHidden()) {		
				if(isValidTestDir(file, testType)) {
					logger.debug("Found valid test directory : '" +file +"'");
					
					TestRunResult testRun = new TestRunResult(baseDirectory, file, getDirType(file));
					runTestAndRecordDuration(baseDirectory, testRun, file, resetServer);
					testResultList.add(testRun);
				}
				else {
					logger.debug("Skipping '" +directory +"', no tests found");
					runAllTestsInDirectory(baseDirectory, file, testType, resetServer);
				}
			}
		}
	}
	
	private void reverseDirectoryContentsIfContainsTestDir(File[] dirContents) throws Exception
	{
		boolean containsTestDir = false;
		for (File f : dirContents)
		{
			if (isValidTestDir(f, TestType.ALL)) {
				containsTestDir = true;
				break;
			}
		}
		if (containsTestDir)
		{
			ArrayUtils.reverse(dirContents);			
		}
	}

	private void runTestAndRecordDuration(File baseDirectory, TestRunResult testRun, File testDir, boolean resetServer) throws Exception {
		testRun.setStartTime(System.currentTimeMillis());
		testRun.setSuccess(runTest(baseDirectory, new File(testDir + File.separator + "js-test-driver" + File.separator + "jsTestDriver.conf"), resetServer));
		testRun.setEndTime(System.currentTimeMillis());
	}
	
	private boolean runTest(File baseDirectory, File configFile, boolean resetServer) throws Exception  {
		logger.info("\n");
		logger.info("Testing " + getFriendlyTestPath(baseDirectory, configFile) + ":");
		
		try {
			File testResultsDir = new File("../"+CutlassConfig.XML_TEST_RESULTS_DIR);
			if (!testResultsDir.exists())
			{
				testResultsDir.mkdirs();
			}
			BundleStubCreator.createRequiredStubs(configFile);
			String javaOpts = getJavaOpts();
			javaOpts += (!javaOpts.equals("")) ? "$$" : "";

			/* use this for JSTD 1.3.4+ */
//			String baseCmd = "java$$"+javaOpts+"-cp$$%s$$com.google.jstestdriver.JsTestDriver --raiseOnFailure$$true$$--config$$%s$$--tests$$all$$--testOutput$$\"%s\"$$%s$$--runnerMode$$%s";
			/* use this for JSTD 1.3.3 */
			String baseCmd = "java$$"+javaOpts+"-cp$$%s$$com.google.jstestdriver.JsTestDriver --config$$%s$$--tests$$all$$--testOutput$$%s$$%s$$--browserTimeout$$%s$$--runnerMode$$%s$$";
			
			if (resetServer) { baseCmd = baseCmd + " --reset"; }
								
			/*
			 *  TODO: (PCTCUT-361) the test results dir is relative to the working dir - which wont always be cutlass-sdk
			 *  - needs to be relative but dynamically calculated  - convertResultsToHTML() method may also need changing
			 */
			
			String classPath = getClassPath(jsTestDriverJar.getParentFile());
			String[] args = CmdCreator.cmd(baseCmd, classPath, configFile.getPath(), "../"+CutlassConfig.XML_TEST_RESULTS_DIR,
				verboseFlag(), browserTimeout(), "INFO");
			logger.debug("Running command: " + CmdCreator.printCmd(args));
			Process process = runTime.exec(args);
			childProcesses.add(process);
			
			ProcessLogger processLogger = new ProcessLogger(brjs, process, null);
			int exitCode = process.waitFor();
			processLogger.waitFor();
			
			if(!childProcesses.remove(process)) {
				logger.error("failed to remove runTest process from child processes list");
			}
			logger.debug("exit code is " + exitCode);
			if(exitCode != 0) {
				logger.info("Tests Failed.");
				return false;
			}
			logger.info("Tests Passed.");
		}
		catch(Exception e) {
			logger.info("Unexpected Exception:", e);
			return false;
		}
		
		return true;
	}

	protected String getJavaOpts() {
		String javaopts = "";
		logger.debug("System env JAVA_OPTS is '" + System.getenv("JAVA_OPTS") +"'");
		if(System.getenv("JAVA_OPTS") != null && !System.getenv("JAVA_OPTS").equals("null") && !System.getenv("JAVA_OPTS").equals(""))
		{
			javaopts = System.getenv("JAVA_OPTS");
		}
		logger.debug("JAVA_OPTS passed through as '" +javaopts +"'");
		return javaopts;
	}
	
	private void startServerProcess() throws Exception {
		logger.info("Starting server process...");
		String classPath = getClassPath(jsTestDriverJar.getParentFile());
		String[] args = CmdCreator.cmd("java$$-cp$$%s$$com.google.jstestdriver.JsTestDriver --config$$%s$$--port$$%s$$%s$$--browserTimeout$$%s$$--runnerMode$$%s",
			classPath, jsTestDriverJar.getAbsolutePath().replaceAll("\\.jar$", ".conf"), portNumber, verboseFlag(), browserTimeout(), "INFO" );
		logger.debug("Running command: " + CmdCreator.printCmd(args));
		Process process = runTime.exec(args);
		childLoggers.add(new ProcessLogger(brjs, process, "server"));
		childProcesses.add(process);
		waitForServer(0);
	}
	
	private void startBrowserProcesses () throws Exception {
		logger.debug("Starting browser processes...");
		int browserNo = 1;
		for(String browser : browsers) {
			String[] args = CmdCreator.cmd("%s http://localhost:%s/capture?strict", browser, portNumber);
			logger.debug("Running command: " + CmdCreator.printCmd(args));
			try 
			{
				Process process = runTime.exec(args);
				childProcesses.add(process);
				childLoggers.add(new ProcessLogger(brjs, process, "browser #" + browserNo++));	
			}
			catch (IOException e)
			{
				String browserString = browser == null ? "" : "'" + browser + "' "; 
				throw new BrowserNotFoundException(browserString, config.getRelativeDir().getPath());
			}
			
		}
		waitForServer(browsers.size());
	}
	
	private void stopChildProcesses () throws Exception {
		for(Process childProcess : childProcesses) {
			logger.debug("Stopping child process...");
			childProcess.destroy();
		}
		
		for(Process childProcess : childProcesses) {
			logger.debug("Waiting for child proccess to stop...");
			childProcess.waitFor();
		}
		
		logger.debug("All child processes stopped");
		stopChildLoggers();
	}
	
	private void stopChildLoggers() {
		for(ProcessLogger processLogger : childLoggers) {
			processLogger.stop();
		}
	}
	
	private void waitForServer(int expectedBrowserCount) throws Exception {
		long endTime = System.currentTimeMillis() + SERVER_AND_BROWSER_TIMEOUT;
		boolean hasConnected = false;
		int actualBrowserCount = -1;
		
		logger.debug("Waiting for server (expecting " + expectedBrowserCount + " browser instances to be connected)...");
		while(!hasConnected && (System.currentTimeMillis() < endTime)) {
			HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + portNumber + "/").openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(SERVER_READ_TIMEOUT);
			
			try {
				logger.debug("trying to connect to server...");
				connection.connect();
				String pageData = IOUtils.toString((InputStream) connection.getContent(), connection.getContentEncoding());
				logger.debug("server response code: : " + connection.getResponseCode());
				if(connection.getResponseCode() == 200) {
					actualBrowserCount = getCapturedBrowerCount(pageData);
					logger.debug("found " + actualBrowserCount + " connected browsers");
					if(actualBrowserCount == expectedBrowserCount) {
						hasConnected = true;
					}
				}
				
			}
			catch (IOException e) {
				logger.debug("connection resulted in exception: " + e.toString());
			}
			finally {
				if(!hasConnected) {
					Thread.sleep(SERVER_POLL_TIME);
				}
			}
		}
		
		if(!hasConnected) {
			if(actualBrowserCount == -1) {
				throw new IOException("server not started: unable to connect to the server.");
			}
			else {
				throw new IOException("incorrect number of browser instances connected to the server: expected " + expectedBrowserCount +
					" but there were actually " + actualBrowserCount + " instances connected.");
			}
			
		}
	}
	
	private boolean isServerRunning() throws Exception {
		logger.debug("Checking to see if server is running...");
		ServerSocket socket = null;
		boolean isServerRunning = false;
		
		try {
			socket = new ServerSocket(portNumber);
		}
		catch(IOException e) {
			isServerRunning = true;
		}
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		
		return isServerRunning;
	}
	
	private int browserTimeout() 
	{
		return BROWSER_TIMEOUT;
	}
	
	private String verboseFlag() {
		return (verbose) ? "--verbose" : "";
	}
	
	private String getClassPath(File testRunnnerDependencies) {
		List<String> classPath = new ArrayList<>();
		
		for(File jarFile : FileUtils.listFiles(testRunnnerDependencies, new String[] {"jar"}, false)) {
			if(!jarFile.getName().startsWith("js-test-driver-bundler-plugin")) {
				classPath.add(jarFile.getAbsolutePath());
			}
		}
		
		return Joiner.on(System.getProperty("path.separator")).join(classPath);
	}
	
	private String getFriendlyTestPath(File baseDir, File testDir)
	{
		File testTypeDir = testDir.getParentFile().getParentFile();
		File projectDir = testTypeDir.getParentFile();
		String testPath = (projectDir.equals(baseDir)) ? projectDir.getName() : RelativePath.getRelativePath(baseDir, projectDir);
		
		return testPath + " " + (getTestTypeFromDirectoryName(testTypeDir.getName()));
	}
	
	private String getTestTypeFromDirectoryName(String directoryName)
	{
		if(directoryName.equalsIgnoreCase("test-unit"))
		{
			return "(UTs)";
		}
		else if(directoryName.equalsIgnoreCase("test-acceptance"))
		{
			return "(ATs)";
		}
		else if(directoryName.equalsIgnoreCase("test-integration"))
		{
			return "(ITs)";
		}
		else 
		{
			return null;
		}
	}
	
	private int getCapturedBrowerCount(String pageData) {
		Matcher matcher = pattern.matcher(pageData);
		int browserCount = -1;
		
		if(matcher.matches()) {
			browserCount = Integer.parseInt(matcher.group(1));
		}
		
		return browserCount;
	}
	
	private boolean isValidTestDir(File dir, TestType validTestTypes) throws Exception {
		TestType dirType = getDirType(dir);
		boolean isJsTestDriverTestDir = false;
		
		if(isValidTestDir(validTestTypes, dirType)) {
			isJsTestDriverTestDir = new File(dir.getCanonicalPath() + File.separator + "js-test-driver" +
				File.separator + "jsTestDriver.conf").exists();
		}
		
		return isJsTestDriverTestDir;
	}
	
	private boolean isValidTestDir(TestType validTestTypes, TestType dirType)
	{
		if (dirType != null)
		{
			if(dirType == validTestTypes || validTestTypes == TestType.ALL)
			{
				return true;
			}
			if(validTestTypes == TestType.UTsAndATs && (dirType == TestType.UTs || dirType == TestType.ATs))
			{
				return true;
			}
		}
		return false;
	}
	
	private TestType getDirType(File dir) 
	{
		if(dir.getName().equals("test-unit")) 
		{
			return TestType.UTs;
		}
		else if(dir.getName().equals("test-acceptance")) 
		{
			return TestType.ATs;
		}
		else if(dir.getName().equals("test-integration")) 
		{
			return TestType.ITs;
		}
		else {
			return null;
		}
	}	
	
	protected boolean hasTestRun() {
		if (testResultList.size() > 0) {
			return true;
		}
		return false;
	}
	
	protected List<TestRunResult> getTestResultList() {
		return testResultList;
	}
	
	protected List<TestRunResult> getFailedTestList() {
		List<TestRunResult> failedTests = new ArrayList<TestRunResult>();
		List<TestRunResult> testResults = getTestResultList();
		for (TestRunResult result : testResults) {
			if (!result.getSuccess()) {
				failedTests.add(result);
			}
		}
		return failedTests;
	}

	public void showExceptionInConsole(Exception ex) {
		logger.info("ERROR: " + ex.toString());
	}
}
