package org.bladerunnerjs.api.spec.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.spec.utility.AppVerifier;
import org.bladerunnerjs.api.spec.utility.AspectVerifier;
import org.bladerunnerjs.api.spec.utility.BRJSVerifier;
import org.bladerunnerjs.api.spec.utility.BladeVerifier;
import org.bladerunnerjs.api.spec.utility.BladesetVerifier;
import org.bladerunnerjs.api.spec.utility.DirNodeVerifier;
import org.bladerunnerjs.api.spec.utility.DirectoryVerifier;
import org.bladerunnerjs.api.spec.utility.JsLibVerifier;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.LoggerVerifier;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeVerifier;
import org.bladerunnerjs.api.spec.utility.NamedNodeVerifier;
import org.bladerunnerjs.api.spec.utility.TestPackVerifier;
import org.bladerunnerjs.api.spec.utility.WorkbenchVerifier;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.eclipse.jetty.server.Server;


public class VerifierChainer {
	private final SpecTest specTest;
	
	public VerifierChainer(SpecTest specTest) {
		this.specTest = specTest;
	}
	
	public ExceptionsVerifier and(List<Throwable> exceptions) { return new ExceptionsVerifier(specTest, exceptions); }
	public NodeObserverVerifier and(EventObserver observer) { return new NodeObserverVerifier(specTest, observer); }
	public NamedNodeVerifier and(NamedNode namedDirNode) { return new NamedNodeVerifier(specTest, namedDirNode); }
	public BRJSVerifier and(BRJS brjs) { return new BRJSVerifier(specTest, brjs); }
	public AppVerifier and(App app) { return new AppVerifier(specTest, app); }
	public AppConfVerifier then(AppConf appConf) { return new AppConfVerifier(specTest, appConf); }
	public AspectVerifier and(Aspect aspect) { return new AspectVerifier(specTest, aspect); }
	public BladeVerifier and(Blade blade) { return new BladeVerifier(specTest, blade); }
	public BladesetVerifier and(Bladeset bladeset) { return new BladesetVerifier(specTest, bladeset); }
	public WorkbenchVerifier and(Workbench<?> workbench) { return new WorkbenchVerifier(specTest, workbench); }
	public JsLibVerifier and(JsLib jsLib) { return new JsLibVerifier(specTest, jsLib); }
	public DirNodeVerifier and(DirNode dirNode) { return new DirNodeVerifier(specTest, dirNode); }
	public NamedDirNodeVerifier and(NamedDirNode namedDirNode) { return new NamedDirNodeVerifier(specTest, namedDirNode); }
	public LoggerVerifier and(LogMessageStore logStore) { return new LoggerVerifier(specTest, logStore); }
	public AppServerVerifier and(ApplicationServer appServer) { return new AppServerVerifier(specTest, appServer); }
	public JettyServerVerifier and(Server jettyServer) { return new JettyServerVerifier(specTest, jettyServer); }
	public AppConfVerifier and(AppConf appConf) { return new AppConfVerifier(specTest, appConf); }
	public DirectoryVerifier and(MemoizedFile dir) { return new DirectoryVerifier(specTest, dir); }
	public DirectoryVerifier and(File dir) { return new DirectoryVerifier(specTest, specTest.brjs.getMemoizedFile(dir)); }
	public StringVerifier and(StringBuffer stringBuffer) { return new StringVerifier(specTest, stringBuffer); }
	public TestPackVerifier and(TestPack testPack) { return new TestPackVerifier(specTest, testPack); }
}
