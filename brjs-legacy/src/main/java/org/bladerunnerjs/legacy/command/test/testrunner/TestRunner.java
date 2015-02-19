package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer;
import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.apache.tools.ant.types.FileSet;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.exception.BrowserStartupException;
import org.bladerunnerjs.api.spec.exception.NoBrowsersDefinedException;
import org.bladerunnerjs.legacy.conf.TestRunnerConfiguration;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.ProcessLogger;
import org.slf4j.impl.StaticLoggerBinder;

import com.esotericsoftware.yamlbeans.YamlException;
import com.google.common.base.Joiner;

public class TestRunner {
	public class Messages {
		public static final String SERVER_STOP_INSTRUCTION_MESSAGE = "Press Ctrl + C to stop the server";
	}
	
	private BRJS brjs = ThreadSafeStaticBRJSAccessor.root;
	private Logger logger = brjs.logger(TestRunner.class);
	public enum TestType {UTs, ATs, UTsAndATs, ITs, ALL};
	
	protected static final int DEFAULT_SLEEP_TIME = 500;
	//private static final int SERVER_POLL_TIME = 50;
	private static final int SERVER_READ_TIMEOUT = 2500;
	//private static final int SERVER_AND_BROWSER_TIMEOUT = 30000;
	
	private static final int BROWSER_TIMEOUT= 100000;

	private static final int SERVER_AND_BROWSER_TIMEOUT = 90000;
	private static final int SERVER_POLL_TIME = 1000;
	
	private static Pattern pattern = Pattern.compile(".*Captured Browsers: \\((\\d+)\\).*", Pattern.DOTALL);
	
	private static final String XML_TEST_RESULTS_PATH = "test-results/xml";
	private static final String HTML_TEST_RESULTS_PATH = "test-results/html";
	
	private final MemoizedFile XML_TEST_RESULTS_DIR;
	private final MemoizedFile HTML_TEST_RESULTS_DIR;
	
	private List<Process> childProcesses = new ArrayList<Process>();
	private List<ProcessLogger> childLoggers = new ArrayList<ProcessLogger>();
	private File jsTestDriverJar;
	private int portNumber;
	private List<String> browsers;
//	private MemoizedFile resultDir; //TODO:uncomment
	private boolean verbose;
	private boolean generateReports;
	private boolean noBrowserFlag;
	private long execStartTime;
	private long execEndTime;
	private TestRunnerConfiguration config;
	private List<TestRunResult> testResultList = new ArrayList<TestRunResult>();
	
	static boolean disableLogging = false;
	
	
	public TestRunner(MemoizedFile configFile, MemoizedFile resultDir, List<String> browserNames) throws FileNotFoundException, YamlException, IOException, NoBrowsersDefinedException {
		this(configFile, resultDir, browserNames, false, false, false);
	}
	
	public TestRunner(MemoizedFile configFile, MemoizedFile resultDir, List<String> browserNames, boolean testServerOnly, boolean noBrowserFlag, boolean generateReports) throws FileNotFoundException, YamlException, IOException, NoBrowsersDefinedException {
		verbose = determineIfVerbose();
		config = TestRunnerConfiguration.getConfiguration(configFile, browserNames);
		
		XML_TEST_RESULTS_DIR = brjs.file(XML_TEST_RESULTS_PATH);
		HTML_TEST_RESULTS_DIR = brjs.file(HTML_TEST_RESULTS_PATH);
		
		this.jsTestDriverJar = config.getJsTestDriverJarFile();
		this.portNumber = config.getPortNumber();
		try {
			this.browsers = getBrowsers(noBrowserFlag);
		}
		catch (NoBrowsersDefinedException e)
		{
			if (testServerOnly)
			{
				noBrowserFlag = true;
				logger.warn("No browsers configured, you must manually launch your browser. To use a browser for testing, visit the URL http://localhost:%d/capture", portNumber);
			}
			else
			{
				throw e;				
			}
		}
//		this.resultDir = resultDir;
		this.noBrowserFlag = noBrowserFlag;
		this.generateReports = generateReports;
		addShutDownHook();
	}

	private List<String> getBrowsers(boolean noBrowserFlag) throws NoBrowsersDefinedException, IOException {

		if(noBrowserFlag || isServerRunning())
		{
				return null;
		}
		
		return config.getBrowsers();
	}
	
	public void runServer() throws Exception {
		boolean serverStarted = startServer();
		
		if(serverStarted) {
			long startTime = System.currentTimeMillis();
			
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME); // slight pause before we display message in case there is any browser output
				logger.println("Server running on port " + config.getPortNumber() + ", " + Messages.SERVER_STOP_INSTRUCTION_MESSAGE);
				logger.println("Connect a browser to the server by visiting http://localhost:"+config.getPortNumber()+"/capture");
				logger.println("");
				
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
	
	public boolean runTests(MemoizedFile directory, TestType testType) throws Exception {
		execStartTime= System.currentTimeMillis();
		
		try {
			startServer();
			
			if (XML_TEST_RESULTS_DIR.exists())
			{
				FileUtils.deleteDirectory(XML_TEST_RESULTS_DIR);
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
			LogLevel logLevel = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogLevel();
			isVerbose = (logLevel == LogLevel.DEBUG);
		}
		catch(NoSuchMethodError e) {
			// the tests are being run through the dashboard, where we will be using J2EE logging
			isVerbose = false;
		}
		
		return isVerbose;
	}
	
	private void displayTimeInfo() throws FileNotFoundException, IOException
	{
		long duration = execEndTime-execStartTime;
		logger.warn("\n");
		if (getTestResultList().size() > 1)
		{
			printReport();
		}
		logger.info("- Time Taken: " + duration/1000 + "secs");		
		if (generateReports && getTestResultList().size() > 0)
		{
			convertResultsToHTML();
		}
	}

	private void printReport() {
		logger.warn("== Runner Report ==");
		if(!getSuccess())
		{
			logger.warn("- Tests Failed :");
			List<TestRunResult> failedTests = getFailedTestList();
			if (failedTests.size() > 0)
			{
				for (TestRunResult failedTest : failedTests)
				{
					logger.warn("  " + getTestPath(getJsTestDriverConf(failedTest.getTestDirectory())));
				}
			} else
			{
				logger.warn("- Tests Failed");
			}
		} else {
			logger.warn("- Tests Passed");
		}
		logger.warn("\n");
	}
	
	private void convertResultsToHTML() throws FileNotFoundException, IOException
	{
		logger.info("\n");
		
		//This is here due to a bug in ant, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=384757#c13 for more details.
		System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");

		if (!HTML_TEST_RESULTS_DIR.exists())
		{
			HTML_TEST_RESULTS_DIR.mkdirs();
		}	
		Project project = new Project();
		project.setName("tmpProject");
		project.init();
		Target target = new Target();
		target.setName("junitreport");
		project.addTarget(target);

		FileSet fs = new FileSet();
		fs.setDir(XML_TEST_RESULTS_DIR);
		fs.createInclude().setName("TEST-*.xml");
		XMLResultAggregator aggregator = new XMLResultAggregator();
		aggregator.setProject(project);
		aggregator.addFileSet(fs);
		aggregator.setTodir(XML_TEST_RESULTS_DIR);
		
		AggregateTransformer transformer = aggregator.createReport();
		transformer.setTodir(HTML_TEST_RESULTS_DIR);		
		target.addTask(aggregator);
		
		logger.warn("Writing HTML reports to " + HTML_TEST_RESULTS_DIR + ".");
		
		MemoizedFile[] xmlTestResultFiles = XML_TEST_RESULTS_DIR.listFiles();
		if (xmlTestResultFiles != null) {
			for (MemoizedFile xmlTestResultFile : xmlTestResultFiles) {
				normaliseXML(xmlTestResultFile);
			}
		}
		
		project.executeTarget("junitreport");
	}

	// XML needs to be normalised because the test suite name may not match the XML file name.
	// Delete this method once <https://issues.apache.org/bugzilla/show_bug.cgi?id=57557> is fixed.
	public static void normaliseXML(MemoizedFile xmlTestResultFile) throws IOException, FileNotFoundException {
		String xmlTestResultFileContent = IOUtils.toString(new FileInputStream(xmlTestResultFile));
		String newTestSuite = xmlTestResultFile.getName().replace("TEST-", "");
		newTestSuite = newTestSuite.replace(".xml", "");
		xmlTestResultFileContent = xmlTestResultFileContent.replaceAll("(.*testsuite name=\")([^\"]*)(\".*)", "$1" + newTestSuite + "$3");
		FileOutputStream xmlFileStream = new FileOutputStream(xmlTestResultFile, false);
		xmlFileStream.write(xmlTestResultFileContent.getBytes());
		xmlFileStream.close();
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
			logger.console("Server already running, not bothering to start a new instance...");
		}
		else {
			startServerProcess();
			if(this.noBrowserFlag == false)
			{
				startBrowserProcesses();
			}
			serverStarted = true;
		}
		
		return serverStarted;
	}
	
	public void runAllTestsInDirectory(MemoizedFile baseDirectory, MemoizedFile directory, TestType testType) throws Exception {
		runAllTestsInDirectory(baseDirectory, directory, testType, false);
	}
	
	public void runAllTestsInDirectory(MemoizedFile baseDirectory, MemoizedFile directory, TestType testType, boolean resetServer) throws Exception {
		if (baseDirectory == null || !baseDirectory.exists()) {
			String failureMessage = "Base directory '" + baseDirectory +"' does not exist";
			logger.warn(failureMessage);
			throw new IOException(failureMessage);
		}
		MemoizedFile[] dirContents = directory.listFiles();
		reverseDirectoryContentsIfContainsTestDir(dirContents);
		for(MemoizedFile file : dirContents) {
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
	
	private void reverseDirectoryContentsIfContainsTestDir(MemoizedFile[] dirContents) throws Exception
	{
		boolean containsTestDir = false;
		for (MemoizedFile f : dirContents)
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

	private void runTestAndRecordDuration(MemoizedFile baseDirectory, TestRunResult testRun, MemoizedFile testDir, boolean resetServer) throws Exception {
		testRun.setStartTime(System.currentTimeMillis());
		testRun.setSuccess(runTest(baseDirectory, getJsTestDriverConf(testDir), resetServer));
		testRun.setEndTime(System.currentTimeMillis());
	}
	
	private boolean runTest(MemoizedFile baseDirectory, MemoizedFile configFile, boolean resetServer) throws Exception  {
		logger.warn("\n");
		logger.warn("Testing " + getTestPath(configFile) + " " + getTestTypeFromDirectoryName(configFile.getParentFile()) + ":");
		
		try {
			if (!XML_TEST_RESULTS_DIR.exists())
			{
				XML_TEST_RESULTS_DIR.mkdirs();
			}
			JsTestDriverBundleCreator.createRequiredBundles(brjs, configFile);
			String javaOpts = getJavaOpts();
			javaOpts += (!javaOpts.equals("")) ? "$$" : "";

			/* use this for JSTD 1.3.4+ */
//			String baseCmd = "java$$"+javaOpts+"-cp$$%s$$com.google.jstestdriver.JsTestDriver --raiseOnFailure$$true$$--config$$%s$$--tests$$all$$--testOutput$$\"%s\"$$%s$$--runnerMode$$%s";
			/* use this for JSTD 1.3.3 */
			String baseCmd = "java$$"+javaOpts+"-cp$$%s$$com.google.jstestdriver.JsTestDriver --config$$%s$$--tests$$all$$--testOutput$$%s$$%s$$--browserTimeout$$%s$$--runnerMode$$%s$$";
			
			if (resetServer) { baseCmd = baseCmd + " --reset"; }
								
			/*
			 *  TODO: (PCTCUT-361) the test results dir is relative to the working dir - which wont always be brjs-sdk
			 *  - needs to be relative but dynamically calculated  - convertResultsToHTML() method may also need changing
			 */
			
			String classPath = getClassPath(jsTestDriverJar.getParentFile());
			String[] args = CmdCreator.cmd(brjs.file("sdk"), baseCmd, classPath, configFile.getPath(), XML_TEST_RESULTS_DIR,
				verboseFlag(), browserTimeout(), "INFO");
			logger.debug("Running command: " + CmdCreator.printCmd(args));
			ProcessBuilder builder = new ProcessBuilder( args );
			builder.directory( brjs.file("sdk") );
			Process process =  builder.start();
			childProcesses.add(process);
			
			ProcessLogger processLogger = new ProcessLogger(brjs, process, LogLevel.WARN, LogLevel.ERROR, null);
			int exitCode = process.waitFor();
			processLogger.waitFor();
			
			if(!childProcesses.remove(process)) {
				logger.error("Failed to remove runTest process from child processes list");
			}
			logger.debug("Exit code is " + exitCode);
			if(exitCode != 0) {
				logger.warn("Tests Failed.");
				return false;
			}
			logger.warn("Tests Passed.");
		}
		catch(Exception e) {
			logger.error("Unexpected Exception:\n%s", ExceptionUtils.getStackTrace(e));
			return false;
		}
		
		return true;
	}

	private String getTestPath(MemoizedFile testDirectory) {
		while (testDirectory != null && !testDirectory.getName().startsWith("test-"))
		{
			testDirectory = testDirectory.getParentFile();
		}
		String testPath = testDirectory.getAbsolutePath();
		if (testPath.contains("apps" + File.separator))
		{
			return StringUtils.substringAfterLast(testPath, "apps" + File.separator);
		}
		if (testPath.contains("sdk"+File.separator)) 
		{
			return StringUtils.substringAfterLast(testPath, "sdk" + File.separator);
		}
		return testPath;
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
		String[] args = CmdCreator.cmd(brjs.file("sdk"), "java$$-cp$$%s$$com.google.jstestdriver.JsTestDriver --config$$%s$$--port$$%s$$%s$$--browserTimeout$$%s$$--runnerMode$$%s",
			classPath, jsTestDriverJar.getAbsolutePath().replaceAll("\\.jar$", ".conf"), portNumber, verboseFlag(), browserTimeout(), "INFO" );
		logger.debug("Running command: " + CmdCreator.printCmd(args));
		ProcessBuilder builder = new ProcessBuilder( args );
		builder.directory( brjs.file("sdk") );
		Process process =  builder.start();
		childLoggers.add(new ProcessLogger(brjs, process, LogLevel.INFO, LogLevel.ERROR, "server"));
		childProcesses.add(process);
		waitForServer(0);
	}
	
	private void startBrowserProcesses () throws Exception {
		logger.debug("Starting browser processes...");
		int browserNo = 1;
		for(String browser : browsers) {
			String[] args = CmdCreator.cmd(brjs.file("sdk"), "%s http://localhost:%s/capture?strict", browser, portNumber);
			logger.debug("Running command: " + CmdCreator.printCmd(args));
			try 
			{
				ProcessBuilder builder = new ProcessBuilder( args );
				builder.directory( brjs.file("sdk") );
				Process process =  builder.start();
				childProcesses.add(process);
				childLoggers.add(new ProcessLogger(brjs, process, LogLevel.DEBUG, LogLevel.INFO, "browser #" + browserNo++));
			}
			catch (IOException e)
			{
				throw new BrowserStartupException(e, args, config.getRelativeDir().getPath());
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
				logger.debug("Trying to connect to server...");
				connection.connect();
				String pageData = IOUtils.toString((InputStream) connection.getContent(), connection.getContentEncoding());
				logger.debug("Server response code: : " + connection.getResponseCode());
				if(connection.getResponseCode() == 200) {
					actualBrowserCount = getCapturedBrowerCount(pageData);
					logger.debug("Found " + actualBrowserCount + " connected browsers");
					if(actualBrowserCount == expectedBrowserCount) {
						hasConnected = true;
					}
				}
				
			}
			catch (IOException e) {
				logger.debug("Connection resulted in exception: " + e.toString());
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
	
	private boolean isServerRunning() {
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
				try{
					socket.close();					
				}
				catch(IOException e){
					throw new RuntimeException(e);
				}
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
	
	private String getTestTypeFromDirectoryName(MemoizedFile directoryName)
	{
		if(directoryName.getName().equalsIgnoreCase("test-unit"))
		{
			return "(UTs)";
		}
		else if(directoryName.getName().equalsIgnoreCase("test-acceptance"))
		{
			return "(ATs)";
		}
		else if(directoryName.getName().equalsIgnoreCase("test-integration"))
		{
			return "(ITs)";
		}
		else 
		{
			if (directoryName.getParentFile().getName().equalsIgnoreCase("test-unit") ||
					directoryName.getParentFile().getName().equalsIgnoreCase("test-acceptance") ||
					directoryName.getParentFile().getName().equalsIgnoreCase("test-integration")) 
				return getTestTypeFromDirectoryName(directoryName.getParentFile());
			else 
				throw new RuntimeException("The test directory name does not indicate a valid test type.");
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
	
	private boolean isValidTestDir(MemoizedFile dir, TestType validTestTypes) throws Exception {
		TestType dirType = getDirType(dir);
		boolean isJsTestDriverTestDir = false;
		
		if(isValidTestDir(validTestTypes, dirType)) {
			isJsTestDriverTestDir = getJsTestDriverConf(dir).exists();
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
		logger.error("ERROR: " + ex.toString());
	}
	
	private MemoizedFile getJsTestDriverConf(MemoizedFile baseDir) {
		MemoizedFile testTechDir = baseDir.file("js-test-driver");
		MemoizedFile defaultTestTechDir = baseDir;
		if ( new File(testTechDir, "jsTestDriver.conf").exists() ) {
			return testTechDir.file("jsTestDriver.conf");
		}
		return defaultTestTechDir.file("jsTestDriver.conf");
	}
}
