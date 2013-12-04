package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.AppBuilder;
import org.bladerunnerjs.specutil.AspectBuilder;
import org.bladerunnerjs.specutil.BRJSBuilder;
import org.bladerunnerjs.specutil.BladeBuilder;
import org.bladerunnerjs.specutil.BladesetBuilder;
import org.bladerunnerjs.specutil.DirNodeBuilder;
import org.bladerunnerjs.specutil.JsLibBuilder;
import org.bladerunnerjs.specutil.LoggerBuilder;
import org.bladerunnerjs.specutil.NamedDirNodeBuilder;
import org.bladerunnerjs.specutil.NamedNodeBuilder;
import org.bladerunnerjs.specutil.TestPackNodeBuilder;
import org.bladerunnerjs.specutil.WorkbenchBuilder;
import org.bladerunnerjs.testing.utility.LogMessageStore;


public class BuilderChainer {
	private final SpecTest modelTest;
	
	public BuilderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public TestPackNodeBuilder and(TestPack testPack) { return new TestPackNodeBuilder(modelTest, testPack); }
	public NamedNodeBuilder and(NamedNode namedDirNode) { return new NamedNodeBuilder(modelTest, namedDirNode); }
	public BRJSBuilder and(BRJS brjs) { return new BRJSBuilder(modelTest, brjs); }
	public AppBuilder and(App app) { return new AppBuilder(modelTest, app); }
	public AspectBuilder and(Aspect aspect) { return new AspectBuilder(modelTest, aspect); }
	public AssetContainerBuilder<Bladeset> and(Bladeset bladeset) { return new BladesetBuilder(modelTest, bladeset); }
	public AssetContainerBuilder<Blade> and(Blade blade) { return new BladeBuilder(modelTest, blade); }
	public WorkbenchBuilder and(Workbench workbench) { return new WorkbenchBuilder(modelTest, workbench); }
	public JsLibBuilder and(JsLib jsLib) { return new JsLibBuilder(modelTest, jsLib); }
	public DirNodeBuilder and(DirNode dirNode) { return new DirNodeBuilder(modelTest, dirNode); }
	public NamedDirNodeBuilder and(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(modelTest, namedDirNode); }
	public LoggerBuilder and(LogMessageStore logStore) { return new LoggerBuilder(modelTest, logStore); }
	public NodeObserverBuilder and(EventObserver observer) { return new NodeObserverBuilder(modelTest, observer); }
	public AppServerBuilder and(ApplicationServer appServer) { return new AppServerBuilder(modelTest, appServer); }
	public AliasesFileBuilder and(AliasesFile aliasesFile) { return new AliasesFileBuilder(modelTest, aliasesFile); }
	public AliasDefinitionsFileBuilder and(AliasDefinitionsFile aliasDefinitionsFile) { return new AliasDefinitionsFileBuilder(modelTest, aliasDefinitionsFile); }
}
