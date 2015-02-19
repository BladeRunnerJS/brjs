package org.bladerunnerjs.api.spec.engine;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.spec.utility.AppBuilder;
import org.bladerunnerjs.api.spec.utility.AppCommander;
import org.bladerunnerjs.api.spec.utility.AppConfCommander;
import org.bladerunnerjs.api.spec.utility.AppVerifier;
import org.bladerunnerjs.api.spec.utility.AspectBuilder;
import org.bladerunnerjs.api.spec.utility.AspectCommander;
import org.bladerunnerjs.api.spec.utility.AspectVerifier;
import org.bladerunnerjs.api.spec.utility.AssetLocationBuilder;
import org.bladerunnerjs.api.spec.utility.BRJSBuilder;
import org.bladerunnerjs.api.spec.utility.BRJSCommander;
import org.bladerunnerjs.api.spec.utility.BRJSVerifier;
import org.bladerunnerjs.api.spec.utility.BladeBuilder;
import org.bladerunnerjs.api.spec.utility.BladeCommander;
import org.bladerunnerjs.api.spec.utility.BladeVerifier;
import org.bladerunnerjs.api.spec.utility.BladesetBuilder;
import org.bladerunnerjs.api.spec.utility.BladesetCommander;
import org.bladerunnerjs.api.spec.utility.BladesetVerifier;
import org.bladerunnerjs.api.spec.utility.DirNodeBuilder;
import org.bladerunnerjs.api.spec.utility.DirNodeCommander;
import org.bladerunnerjs.api.spec.utility.DirNodeVerifier;
import org.bladerunnerjs.api.spec.utility.DirectoryVerifier;
import org.bladerunnerjs.api.spec.utility.JsLibBuilder;
import org.bladerunnerjs.api.spec.utility.JsLibCommander;
import org.bladerunnerjs.api.spec.utility.JsLibVerifier;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.LoggerBuilder;
import org.bladerunnerjs.api.spec.utility.LoggerVerifier;
import org.bladerunnerjs.api.spec.utility.MockAppVersionGenerator;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeBuilder;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeCommander;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeVerifier;
import org.bladerunnerjs.api.spec.utility.NamedNodeBuilder;
import org.bladerunnerjs.api.spec.utility.NamedNodeCommander;
import org.bladerunnerjs.api.spec.utility.NamedNodeVerifier;
import org.bladerunnerjs.api.spec.utility.NodePropertiesBuilder;
import org.bladerunnerjs.api.spec.utility.NodePropertiesCommander;
import org.bladerunnerjs.api.spec.utility.NodePropertiesVerifier;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.api.spec.utility.TestPackVerifier;
import org.bladerunnerjs.api.spec.utility.WebappTester;
import org.bladerunnerjs.api.spec.utility.WorkbenchBuilder;
import org.bladerunnerjs.api.spec.utility.WorkbenchCommander;
import org.bladerunnerjs.api.spec.utility.WorkbenchVerifier;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;


public abstract class SpecTest
{
	public static final String HTTP_REQUEST_PREFIX = "http://localhost";
	
	public static final String COMMON_JS_STYLE = "common-js";
	public static final String NAMESPACED_JS_STYLE = "namespaced-js";
	
	private String activeCharacterEncoding = "UTF-8";
	private String activeClientCharacterEncoding = "UTF-8";
	
	public LogMessageStore logging;
	public List<Throwable> exceptions;
	public boolean catchAndVerifyExceptions = true;
	public boolean cleanupTestSdkDirectory = true;
	public EventObserver observer;
	public File testSdkDirectory;
	public MockPluginLocator pluginLocator;
	public BRJS brjs;
	public int appServerPort;
	public WebappTester webappTester;
	public MockAppVersionGenerator appVersionGenerator;
	public Thread fileWatcherThread;
	
	@Before
	public void resetTestObjects()
	{
		BRJS.allowInvalidRootDirectories = false;
		
		appServerPort = ServerUtility.getTestPort();
		logging = new LogMessageStore();
		exceptions = new ArrayList<>();
		observer = mock(EventObserver.class);
		if (testSdkDirectory != null) FileUtils.deleteQuietly(testSdkDirectory);
		testSdkDirectory = BRJSTestModelFactory.createTestSdkDirectory();
		pluginLocator = new MockPluginLocator();
		webappTester = new WebappTester(testSdkDirectory);
		appVersionGenerator = new MockAppVersionGenerator();
	}
	
	@After
	public void cleanUp() {
		BRJS.allowInvalidRootDirectories = true;
		
		if (fileWatcherThread != null) {
			fileWatcherThread.interrupt();
		}
		
		if(brjs != null) {
			brjs.io().uninstallFileAccessChecker();
			brjs.close();
		}
		
		if (testSdkDirectory.exists() && cleanupTestSdkDirectory) {
			org.apache.commons.io.FileUtils.deleteQuietly(testSdkDirectory);
		}
		
		try (ServerSocket socket = new ServerSocket(appServerPort))
		{
			socket.getClass(); /* reference socket to prevent the compiler complaining that is isn't referenced */
		}
		catch (IOException ex)
		{
			Assert.fail("The app server is still running on port " + appServerPort + " for a test in " + this.getClass().getSimpleName()+". It should be stopped to prevent background threads.");
		}
	}
	
	public BRJS createModel() throws InvalidSdkDirectoryException 
	{	
		return BRJSTestModelFactory.createModel(testSdkDirectory, pluginLocator, new TestLoggerFactory(logging), appVersionGenerator);
	}
	
	public BRJS createNonTestModel() throws InvalidSdkDirectoryException {
		return BRJSTestModelFactory.createNonTestModel(testSdkDirectory, logging);
	}
	
	public BRJS createNonTestModelWithTestFileObserver() throws InvalidSdkDirectoryException {
		return BRJSTestModelFactory.createNonTestModel(testSdkDirectory, logging, new TestLoggerFactory(logging));
	}
	
	public String getActiveCharacterEncoding() {
		return activeCharacterEncoding;
	}
	
	public void setActiveCharacterEncoding(String activeCharacterEncoding) {
		this.activeCharacterEncoding = activeCharacterEncoding;
	}
	
	public String getActiveClientCharacterEncoding() {
		return activeClientCharacterEncoding;
	}
	
	public void setActiveClientCharacterEncoding(String activeClientCharacterEncoding) {
		this.activeClientCharacterEncoding = activeClientCharacterEncoding;
	}
	
	@After
	public void verifyLogs() {
		then(logging).verifyNoUnhandledMessages()
			.and(logging).verifyLogsReceivedIfCaptureEnabled();
	}
	
	@After
	public void verifyExceptions() {
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// BRJS
	public SpecTestBuilder given() { return new SpecTestBuilder(this); }
	

	// File
	public FileTestBuilder given(MemoizedFile file) { return new FileTestBuilder(this, file); }
	public FileTestBuilder when(MemoizedFile file) { return new FileTestBuilder(this, file); }
	public FileTestBuilder given(File file) { return new FileTestBuilder(this, file); }
	public FileTestBuilder when(File file) { return new FileTestBuilder(this, file); }
	
	// exceptions
	protected ExceptionsBuilder given(List<Throwable> exceptions) { return new ExceptionsBuilder(this, exceptions); }
	public ExceptionsVerifier then(List<Throwable> exceptions) { return new ExceptionsVerifier(this, exceptions); }
	public StringBuilder unquoted(String string) { return new StringBuilder(string); }
	public StringBuilder doubleQuoted(String string) { return new StringBuilder("\"" + string + "\""); }
	
	// logging
	public LoggerBuilder given(LogMessageStore logStore) { return new LoggerBuilder(this, logStore); }
	public LoggerVerifier then(LogMessageStore logStore) { return new LoggerVerifier(this, logStore); }
	
	// node observer
	public NodeObserverBuilder given(EventObserver observer) { return new NodeObserverBuilder(this, observer); }
	public NodeObserverVerifier then(EventObserver observer) { return new NodeObserverVerifier(this, observer); }
	
	// NamedDirNode
	public NamedNodeBuilder given(NamedNode namedDirNode) { return new NamedNodeBuilder(this, namedDirNode); }
	public NamedNodeCommander when(NamedNode namedDirNode) { return new NamedNodeCommander(this, namedDirNode); }
	public NamedNodeVerifier then(NamedNode namedDirNode) { return new NamedNodeVerifier(this, namedDirNode); }
	
	
	public TemplateGroupBuilder given(TemplateGroup templateGroup) { return new TemplateGroupBuilder(this, templateGroup); }
	
	// Directory
	public DirectoryVerifier then(MemoizedFile dir) { return new DirectoryVerifier(this, dir); }
	public DirectoryVerifier then(File dir) { return new DirectoryVerifier(this, brjs.getMemoizedFile(dir)); }
	
	// StringBuffer
	public StringVerifier then(StringBuffer stringBuffer) { return new StringVerifier(this, stringBuffer); }
	public StringVerifier then(String string) { return new StringVerifier(this, string); }
	
	// BRJS
	public BRJSBuilder given(BRJS brjs) { return new BRJSBuilder(this, brjs); }
	public BRJSCommander when(BRJS brjs) { return new BRJSCommander(this, brjs); }
	public BRJSVerifier then(BRJS brjs) { return new BRJSVerifier(this, brjs); }
	
	// BladerunnerConf
	public BladerunnerConfBuilder given(BladerunnerConf bladerunnerConf) { return new BladerunnerConfBuilder(this, bladerunnerConf); }
	
	// NodeProperties
	public NodePropertiesBuilder given(NodeProperties nodeProperties) { return new NodePropertiesBuilder(this, nodeProperties); }
	public NodePropertiesCommander when(NodeProperties nodeProperties) { return new NodePropertiesCommander(this, nodeProperties); }
	public NodePropertiesVerifier then(NodeProperties nodeProperties) { return new NodePropertiesVerifier(this, nodeProperties); }
	
	// App
	public AppBuilder given(App app) { return new AppBuilder(this, app); }
	public AppCommander when(App app) { return new AppCommander(this, app); }
	public AppVerifier then(App app) { return new AppVerifier(this, app); }
	
	// AppConf
	public AppConfBuilder given(AppConf appConf) { return new AppConfBuilder(this, appConf); }
	public AppConfCommander when(AppConf appConf) { return new AppConfCommander(this, appConf); }
	public AppConfVerifier then(AppConf appConf) { return new AppConfVerifier(this, appConf); }
	
	// Aspect
	public AspectBuilder given(Aspect aspect) { return new AspectBuilder(this, aspect); }
	public AspectCommander when(Aspect aspect) { return new AspectCommander(this, aspect); }
	public AspectVerifier then(Aspect aspect) { return new AspectVerifier(this, aspect); }
	
	// Blade
	public AssetContainerBuilder<Blade> given(Blade blade) { return new BladeBuilder(this, blade); }
	public BladeCommander when(Blade blade) { return new BladeCommander(this, blade); }
	public BladeVerifier then(Blade blade) { return new BladeVerifier(this, blade); }

	// Bladeset
	public AssetContainerBuilder<Bladeset> given(Bladeset bladeset) { return new BladesetBuilder(this, bladeset); }
	public BladesetCommander when(Bladeset bladeset) { return new BladesetCommander(this, bladeset); }
	public BladesetVerifier then(Bladeset bladeset) { return new BladesetVerifier(this, bladeset); }
	
	// Workbench
	public WorkbenchBuilder given(Workbench<?> workbench) { return new WorkbenchBuilder(this, workbench); }
	public WorkbenchCommander when(Workbench<?> workbench) { return new WorkbenchCommander(this, workbench); }
	public WorkbenchVerifier then(Workbench<?> workbench) { return new WorkbenchVerifier(this, workbench); }
	
	// JsLib
	public JsLibBuilder given(JsLib jsLib) { return new JsLibBuilder(this, jsLib); }
	public JsLibCommander when(JsLib jsLib) { return new JsLibCommander(this, jsLib); }
	public JsLibVerifier then(JsLib jsLib) { return new JsLibVerifier(this, jsLib); }
	
	// AssetLocation
	public AssetLocationBuilder given(AssetLocation assetLocation) { return new AssetLocationBuilder(this, assetLocation); }
	
	// DirNode
	public DirNodeBuilder given(DirNode dirNode) { return new DirNodeBuilder(this, dirNode); }
	public DirNodeCommander when(DirNode dirNode) { return new DirNodeCommander(this, dirNode); }
	public DirNodeVerifier then(DirNode dirNode) { return new DirNodeVerifier(this, dirNode); }
	
	// NamedDirNode
	public NamedDirNodeBuilder given(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(this, namedDirNode); }
	public NamedDirNodeCommander when(NamedDirNode namedDirNode) { return new NamedDirNodeCommander(this, namedDirNode); }
	public NamedDirNodeVerifier then(NamedDirNode namedDirNode) { return new NamedDirNodeVerifier(this, namedDirNode); }
	
	// TestPack
	public TestPackCommander when(TestPack testPack) { return new TestPackCommander(this, testPack); }
	public TestPackVerifier then(TestPack testPack) { return new TestPackVerifier(this, testPack); }
	
	// ApplicationServer
	public AppServerBuilder given(ApplicationServer appServer) { return new AppServerBuilder(this, appServer); }
	public AppServerCommander when(ApplicationServer appServer) { return new AppServerCommander(this, appServer); }
	public AppServerVerifier then(ApplicationServer appServer) { return new AppServerVerifier(this, appServer); }

	// Content Plugins
	public ContentPluginCommander when(ContentPlugin contentPlugin) { return new ContentPluginCommander(this, contentPlugin); }
	
	// JettyServer
	public JettyServerBuilder given(Server jettyServer) { return new JettyServerBuilder(this, jettyServer); }
	public JettyServerCommander when(Server jettyServer) { return new JettyServerCommander(this, jettyServer); }
	public JettyServerVerifier then(Server jettyServer) { return new JettyServerVerifier(this, jettyServer); }
	
	// Webapp Tester
	public WebappTesterCommander when(WebappTester webappTester) { return new WebappTesterCommander(this, webappTester); } 

	// AliasesFile
	public AliasesFileBuilder given(AliasesFile aliasesFile) { return new AliasesFileBuilder(this, aliasesFile); }
	
	// AliasDefinitionsFile
	public AliasDefinitionsFileBuilder given(AliasDefinitionsFile aliasDefinitionsFile) { return new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile); }
	
	//TODO: we might find we need a better way to deal with multiple methods that want to return different verifiers based on a List
	public RequestListVerifier thenRequests(List<String> requests) { return new RequestListVerifier(this, requests); }
	
}
