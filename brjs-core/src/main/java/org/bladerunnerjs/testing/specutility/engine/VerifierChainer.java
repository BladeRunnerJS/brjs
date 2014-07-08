package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.testing.specutility.AppVerifier;
import org.bladerunnerjs.testing.specutility.AspectVerifier;
import org.bladerunnerjs.testing.specutility.BRJSVerifier;
import org.bladerunnerjs.testing.specutility.BladeVerifier;
import org.bladerunnerjs.testing.specutility.BladesetVerifier;
import org.bladerunnerjs.testing.specutility.DirNodeVerifier;
import org.bladerunnerjs.testing.specutility.DirectoryVerifier;
import org.bladerunnerjs.testing.specutility.JsLibVerifier;
import org.bladerunnerjs.testing.specutility.LoggerVerifier;
import org.bladerunnerjs.testing.specutility.NamedDirNodeVerifier;
import org.bladerunnerjs.testing.specutility.NamedNodeVerifier;
import org.bladerunnerjs.testing.specutility.TestPackVerifier;
import org.bladerunnerjs.testing.specutility.WorkbenchVerifier;
import org.bladerunnerjs.testing.utility.LogMessageStore;
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
	public WorkbenchVerifier and(Workbench workbench) { return new WorkbenchVerifier(specTest, workbench); }
	public JsLibVerifier and(JsLib jsLib) { return new JsLibVerifier(specTest, jsLib); }
	public DirNodeVerifier and(DirNode dirNode) { return new DirNodeVerifier(specTest, dirNode); }
	public NamedDirNodeVerifier and(NamedDirNode namedDirNode) { return new NamedDirNodeVerifier(specTest, namedDirNode); }
	public LoggerVerifier and(LogMessageStore logStore) { return new LoggerVerifier(specTest, logStore); }
	public AppServerVerifier and(ApplicationServer appServer) { return new AppServerVerifier(specTest, appServer); }
	public JettyServerVerifier and(Server jettyServer) { return new JettyServerVerifier(specTest, jettyServer); }
	public AppConfVerifier and(AppConf appConf) { return new AppConfVerifier(specTest, appConf); }
	public DirectoryVerifier and(File dir) { return new DirectoryVerifier(specTest, dir); }
	public StringVerifier and(StringBuffer stringBuffer) { return new StringVerifier(specTest, stringBuffer); }
	public TestPackVerifier and(TestPack testPack) { return new TestPackVerifier(specTest, testPack); }
}
