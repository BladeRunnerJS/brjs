package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.appserver.ApplicationServer;
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


public class BuilderChainer {
	private final SpecTest modelTest;
	
	public BuilderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public TestPackBuilder and(TestPack testPack) { return new TestPackBuilder(modelTest, testPack); }
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
	public SpecTestDirObserverBuilder and(SpecTestDirObserver observer) { return new SpecTestDirObserverBuilder(modelTest, observer); }
	
}
