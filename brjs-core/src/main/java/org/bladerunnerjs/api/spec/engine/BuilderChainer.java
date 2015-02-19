package org.bladerunnerjs.api.spec.engine;

import java.io.File;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
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
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.spec.utility.AppBuilder;
import org.bladerunnerjs.api.spec.utility.AspectBuilder;
import org.bladerunnerjs.api.spec.utility.BRJSBuilder;
import org.bladerunnerjs.api.spec.utility.BladeBuilder;
import org.bladerunnerjs.api.spec.utility.BladesetBuilder;
import org.bladerunnerjs.api.spec.utility.DirNodeBuilder;
import org.bladerunnerjs.api.spec.utility.JsLibBuilder;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.LoggerBuilder;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeBuilder;
import org.bladerunnerjs.api.spec.utility.NamedNodeBuilder;
import org.bladerunnerjs.api.spec.utility.TestPackBuilder;
import org.bladerunnerjs.api.spec.utility.WorkbenchBuilder;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.engine.NamedNode;
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
	public FileTestBuilder and(MemoizedFile file) { return new FileTestBuilder(specTest, file); }
	public FileTestBuilder and(File file) { return new FileTestBuilder(specTest, file); }
	public BladerunnerConfBuilder and(BladerunnerConf bladerunnerConf) { return new BladerunnerConfBuilder(specTest, bladerunnerConf); }
	public AppBuilder and(App app) { return new AppBuilder(specTest, app); }
	public AppConfBuilder and(AppConf appConf) { return new AppConfBuilder(specTest, appConf); }
	public AspectBuilder and(Aspect aspect) { return new AspectBuilder(specTest, aspect); }
	public AssetContainerBuilder<Bladeset> and(Bladeset bladeset) { return new BladesetBuilder(specTest, bladeset); }
	public AssetContainerBuilder<Blade> and(Blade blade) { return new BladeBuilder(specTest, blade); }
	public WorkbenchBuilder and(Workbench<?> workbench) { return new WorkbenchBuilder(specTest, workbench); }
	public JsLibBuilder and(JsLib jsLib) { return new JsLibBuilder(specTest, jsLib); }
	public DirNodeBuilder and(DirNode dirNode) { return new DirNodeBuilder(specTest, dirNode); }
	public NamedDirNodeBuilder and(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(specTest, namedDirNode); }
	public TemplateGroupBuilder and(TemplateGroup templateGroup) { return new TemplateGroupBuilder(specTest, templateGroup); }
	public LoggerBuilder and(LogMessageStore logStore) { return new LoggerBuilder(specTest, logStore); }
	public NodeObserverBuilder and(EventObserver observer) { return new NodeObserverBuilder(specTest, observer); }
	public AppServerBuilder and(ApplicationServer appServer) { return new AppServerBuilder(specTest, appServer); }
	public JettyServerBuilder and(Server jettyServer) { return new JettyServerBuilder(specTest, jettyServer); }
	public AliasesFileBuilder and(AliasesFile aliasesFile) { return new AliasesFileBuilder(specTest, aliasesFile); }
	public AliasDefinitionsFileBuilder and(AliasDefinitionsFile aliasDefinitionsFile) { return new AliasDefinitionsFileBuilder(specTest, aliasDefinitionsFile); }
	
}
