package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.AppBuilder;
import org.bladerunnerjs.specutil.AspectBuilder;
import org.bladerunnerjs.specutil.BRJSBuilder;
import org.bladerunnerjs.specutil.BladeBuilder;
import org.bladerunnerjs.specutil.DirNodeBuilder;
import org.bladerunnerjs.specutil.JsLibBuilder;
import org.bladerunnerjs.specutil.LoggerBuilder;
import org.bladerunnerjs.specutil.NamedDirNodeBuilder;
import org.bladerunnerjs.specutil.NamedNodeBuilder;
import org.bladerunnerjs.testing.utility.LogMessageStore;


public class BuilderChainer {
	private final SpecTest modelTest;
	
	public BuilderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public NamedNodeBuilder and(NamedNode namedDirNode) { return new NamedNodeBuilder(modelTest, namedDirNode); }
	public BRJSBuilder and(BRJS brjs) { return new BRJSBuilder(modelTest, brjs); }
	public AppBuilder and(App app) { return new AppBuilder(modelTest, app); }
	public AspectBuilder and(Aspect aspect) { return new AspectBuilder(modelTest, aspect); }
	public BladeBuilder and(Blade blade) { return new BladeBuilder(modelTest, blade); }
	public JsLibBuilder and(JsLib jsLib) { return new JsLibBuilder(modelTest, jsLib); }
	public DirNodeBuilder and(DirNode dirNode) { return new DirNodeBuilder(modelTest, dirNode); }
	public NamedDirNodeBuilder and(NamedDirNode namedDirNode) { return new NamedDirNodeBuilder(modelTest, namedDirNode); }
	public LoggerBuilder and(LogMessageStore logStore) { return new LoggerBuilder(modelTest, logStore); }
	public NodeObserverBuilder and(EventObserver observer) { return new NodeObserverBuilder(modelTest, observer); }
	public AppServerBuilder and(ApplicationServer appServer) { return new AppServerBuilder(modelTest, appServer); }
}
