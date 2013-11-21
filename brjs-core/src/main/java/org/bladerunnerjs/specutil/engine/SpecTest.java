package org.bladerunnerjs.specutil.engine;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.BRJSPluginLocator;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;
import org.bladerunnerjs.specutil.AppBuilder;
import org.bladerunnerjs.specutil.AppCommander;
import org.bladerunnerjs.specutil.AppVerifier;
import org.bladerunnerjs.specutil.AspectBuilder;
import org.bladerunnerjs.specutil.AspectCommander;
import org.bladerunnerjs.specutil.AspectVerifier;
import org.bladerunnerjs.specutil.BRJSBuilder;
import org.bladerunnerjs.specutil.BRJSCommander;
import org.bladerunnerjs.specutil.DirectoryVerifier;
import org.bladerunnerjs.specutil.BRJSVerifier;
import org.bladerunnerjs.specutil.BladeBuilder;
import org.bladerunnerjs.specutil.BladeCommander;
import org.bladerunnerjs.specutil.BladeVerifier;
import org.bladerunnerjs.specutil.DirNodeBuilder;
import org.bladerunnerjs.specutil.DirNodeCommander;
import org.bladerunnerjs.specutil.DirNodeVerifier;
import org.bladerunnerjs.specutil.JsLibBuilder;
import org.bladerunnerjs.specutil.JsLibCommander;
import org.bladerunnerjs.specutil.JsLibVerifier;
import org.bladerunnerjs.specutil.LoggerBuilder;
import org.bladerunnerjs.specutil.LoggerVerifier;
import org.bladerunnerjs.specutil.NamedDirNodeBuilder;
import org.bladerunnerjs.specutil.NamedDirNodeCommander;
import org.bladerunnerjs.specutil.NamedDirNodeVerifier;
import org.bladerunnerjs.specutil.NamedNodeBuilder;
import org.bladerunnerjs.specutil.NamedNodeCommander;
import org.bladerunnerjs.specutil.NamedNodeVerifier;
import org.bladerunnerjs.specutil.NodePropertiesBuilder;
import org.bladerunnerjs.specutil.NodePropertiesCommander;
import org.bladerunnerjs.specutil.NodePropertiesVerifier;
import org.bladerunnerjs.specutil.TestPackVerifier;
import org.bladerunnerjs.specutil.WorkbenchBuilder;
import org.bladerunnerjs.specutil.WorkbenchCommander;
import org.bladerunnerjs.specutil.WorkbenchVerifier;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.testing.utility.WebappTester;
import org.junit.After;
import org.junit.Before;


public abstract class SpecTest
{
	public static final String HTTP_REQUEST_PREFIX = "http://localhost";
	
	public LogMessageStore logging;
	public ConsoleMessageStore output;
	public List<Throwable> exceptions;
	public boolean catchAndVerifyExceptions = true;
	public EventObserver observer;
	public File testSdkDirectory;
	public MockPluginLocator pluginLocator;
	public BRJS brjs;
	public int appServerPort;
	
	WebappTester webappTester;

		
	@Before
	public void resetTestObjects()
	{
		appServerPort = ServerUtility.getTestPort();
		
		logging = new LogMessageStore();
		output = new ConsoleMessageStore();
		exceptions = new ArrayList<>();
		observer = mock(EventObserver.class);
		testSdkDirectory = createTestSdkDirectory();
		pluginLocator = new MockPluginLocator();
		webappTester = new WebappTester(testSdkDirectory);
	}
	
	public BRJS createModel() 
	{	
		return new BRJS(testSdkDirectory, pluginLocator, new TestLoggerFactory(logging), new ConsoleStoreWriter(output));
	}
	
	public BRJS createNonTestModel() {
		return new BRJS(testSdkDirectory, new BRJSPluginLocator(), new TestLoggerFactory(logging), new ConsoleStoreWriter(output));
	}
	
	@After
	public void verifyNotifications() {
		then(observer).noNotifications();
	}
	
	@After
	public void verifyLogs() {
		then(logging).verifyNoUnhandledMessages()
			.and(logging).verifyLogsRecievedIfCaptureEnabled();
	}
	
	@After
	public void verifyExceptions() {
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	// exceptions
	protected ExceptionsBuilder given(List<Throwable> exceptions) { return new ExceptionsBuilder(this, exceptions); }
	public ExceptionsVerifier then(List<Throwable> exceptions) { return new ExceptionsVerifier(exceptions); }
	public StringBuilder unquoted(String string) { return new StringBuilder(string); }
	
	// logging
	public LoggerBuilder given(LogMessageStore logStore) { return new LoggerBuilder(this, logStore); }
	public LoggerVerifier then(LogMessageStore logStore) { return new LoggerVerifier(this, logStore); }
	
	// console
	public ConsoleWriterVerifier then(ConsoleMessageStore consoleMessageStore) { return new ConsoleWriterVerifier(this, consoleMessageStore); }
	
	// node observer
	public NodeObserverBuilder given(EventObserver observer) { return new NodeObserverBuilder(this, observer); }
	public NodeObserverVerifier then(EventObserver observer) { return new NodeObserverVerifier(this, observer); }
	
	public PluginLocatorBuilder given(MockPluginLocator pluginLocator) {return new PluginLocatorBuilder(this, pluginLocator); }
	
	// NamedDirNode
	public NamedNodeBuilder given(NamedNode namedDirNode) { return new NamedNodeBuilder(this, namedDirNode); }
	public NamedNodeCommander when(NamedNode namedDirNode) { return new NamedNodeCommander(this, namedDirNode); }
	public NamedNodeVerifier then(NamedNode namedDirNode) { return new NamedNodeVerifier(this, namedDirNode); }
	
	public DirectoryVerifier then(File dir) { return new DirectoryVerifier(this, dir); }
	
	// StringBuffer
	public StringVerifier then(StringBuffer stringBuffer) { return new StringVerifier(this, stringBuffer); }
	
	// BRJS
	public BRJSBuilder given(BRJS brjs) { return new BRJSBuilder(this, brjs); }
	public BRJSCommander when(BRJS brjs) { return new BRJSCommander(this, brjs); }
	public BRJSVerifier then(BRJS brjs) { return new BRJSVerifier(this, brjs); }
	
	// NodeProperties
	public NodePropertiesBuilder given(NodeProperties nodeProperties) { return new NodePropertiesBuilder(this, nodeProperties); }
	public NodePropertiesCommander when(NodeProperties nodeProperties) { return new NodePropertiesCommander(this, nodeProperties); }
	public NodePropertiesVerifier then(NodeProperties nodeProperties) { return new NodePropertiesVerifier(this, nodeProperties); }
	
	// App
	public AppBuilder given(App app) { return new AppBuilder(this, app); }
	public AppCommander when(App app) { return new AppCommander(this, app); }
	public AppVerifier then(App app) { return new AppVerifier(this, app); }
	
	// Aspect
	public AspectBuilder given(Aspect aspect) { return new AspectBuilder(this, aspect); }
	public AspectCommander when(Aspect aspect) { return new AspectCommander(this, aspect); }
	public AspectVerifier then(Aspect aspect) { return new AspectVerifier(this, aspect); }
	
	// Blade
	public AssetContainerBuilder<Blade> given(Blade blade) { return new BladeBuilder(this, blade); }
	public BladeCommander when(Blade blade) { return new BladeCommander(this, blade); }
	public BladeVerifier then(Blade blade) { return new BladeVerifier(this, blade); }
	
	// Workbench
	public WorkbenchBuilder given(Workbench workbench) { return new WorkbenchBuilder(this, workbench); }
	public WorkbenchCommander when(Workbench workbench) { return new WorkbenchCommander(this, workbench); }
	public WorkbenchVerifier then(Workbench workbench) { return new WorkbenchVerifier(this, workbench); }
	
	// JsLib
	public JsLibBuilder given(JsLib jsLib) { return new JsLibBuilder(this, jsLib); }
	public JsLibCommander when(JsLib jsLib) { return new JsLibCommander(this, jsLib); }
	public JsLibVerifier then(JsLib jsLib) { return new JsLibVerifier(this, jsLib); }
	
	// DirNode
	public DirNodeBuilder given(DirNode dirNode) { return new DirNodeBuilder(this, dirNode); }
	public DirNodeCommander when(DirNode dirNode) { return new DirNodeCommander(this, dirNode); }
	public DirNodeVerifier then(DirNode dirNode) { return new DirNodeVerifier(this, dirNode); }
	
	// NamedDirNode
	public NamedDirNodeBuilder given(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(this, namedDirNode); }
	public NamedDirNodeCommander when(NamedDirNode namedDirNode) { return new NamedDirNodeCommander(this, namedDirNode); }
	public NamedDirNodeVerifier then(NamedDirNode namedDirNode) { return new NamedDirNodeVerifier(this, namedDirNode); }
	
	// TestPack
	public TestPackVerifier then(TestPack testPack) { return new TestPackVerifier(this, testPack); }
	
	// App server
	public AppServerBuilder given(ApplicationServer appServer) { return new AppServerBuilder(this, appServer); }
	public AppServerCommander when(ApplicationServer appServer) { return new AppServerCommander(this, appServer); }
	public AppServerVerifier then(ApplicationServer appServer) { return new AppServerVerifier(this, appServer); }
	
	private File createTestSdkDirectory() {
		File sdkDir;
		
		try {
			sdkDir = FileUtility.createTemporaryDirectory("test");
			new File(sdkDir, "sdk").mkdirs();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sdkDir;
	}
	
}
