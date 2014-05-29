package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.testing.specutility.AppBuilder;
import org.bladerunnerjs.testing.specutility.AspectBuilder;
import org.bladerunnerjs.testing.specutility.BRJSBuilder;
import org.bladerunnerjs.testing.specutility.BladeBuilder;
import org.bladerunnerjs.testing.specutility.BladesetBuilder;
import org.bladerunnerjs.testing.specutility.DirNodeBuilder;
import org.bladerunnerjs.testing.specutility.JsLibBuilder;
import org.bladerunnerjs.testing.specutility.LoggerBuilder;
import org.bladerunnerjs.testing.specutility.NamedDirNodeBuilder;
import org.bladerunnerjs.testing.specutility.NamedNodeBuilder;
import org.bladerunnerjs.testing.specutility.TestPackBuilder;
import org.bladerunnerjs.testing.specutility.WorkbenchBuilder;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.SpecTestDirObserver;
import org.eclipse.jetty.server.Server;


public class BuilderChainer {
	private final SpecTest specTest;
	
	public BuilderChainer(SpecTest specTest) {
		this.specTest = specTest;
	}
	
	public SpecTestBuilder and() { return new SpecTestBuilder(specTest); }
	public TestPackBuilder and(TestPack testPack) { return new TestPackBuilder(specTest, testPack); }
	public NamedNodeBuilder and(NamedNode namedDirNode) { return new NamedNodeBuilder(specTest, namedDirNode); }
	public BRJSBuilder and(BRJS brjs) { return new BRJSBuilder(specTest, brjs); }
	public BladerunnerConfBuilder and(BladerunnerConf bladerunnerConf) { return new BladerunnerConfBuilder(specTest, bladerunnerConf); }
	public AppBuilder and(App app) { return new AppBuilder(specTest, app); }
	public AppConfBuilder and(AppConf appConf) { return new AppConfBuilder(specTest, appConf); }
	public AspectBuilder and(Aspect aspect) { return new AspectBuilder(specTest, aspect); }
	public AssetContainerBuilder<Bladeset> and(Bladeset bladeset) { return new BladesetBuilder(specTest, bladeset); }
	public AssetContainerBuilder<Blade> and(Blade blade) { return new BladeBuilder(specTest, blade); }
	public WorkbenchBuilder and(Workbench workbench) { return new WorkbenchBuilder(specTest, workbench); }
	public JsLibBuilder and(JsLib jsLib) { return new JsLibBuilder(specTest, jsLib); }
	public DirNodeBuilder and(DirNode dirNode) { return new DirNodeBuilder(specTest, dirNode); }
	public NamedDirNodeBuilder and(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(specTest, namedDirNode); }
	public LoggerBuilder and(LogMessageStore logStore) { return new LoggerBuilder(specTest, logStore); }
	public NodeObserverBuilder and(EventObserver observer) { return new NodeObserverBuilder(specTest, observer); }
	public AppServerBuilder and(ApplicationServer appServer) { return new AppServerBuilder(specTest, appServer); }
	public JettyServerBuilder and(Server jettyServer) { return new JettyServerBuilder(specTest, jettyServer); }
	public AliasesFileBuilder and(AliasesFile aliasesFile) { return new AliasesFileBuilder(specTest, aliasesFile); }
	public AliasDefinitionsFileBuilder and(AliasDefinitionsFile aliasDefinitionsFile) { return new AliasDefinitionsFileBuilder(specTest, aliasDefinitionsFile); }
	public SpecTestDirObserverBuilder and(SpecTestDirObserver observer) { return new SpecTestDirObserverBuilder(specTest, observer); }
	
}
