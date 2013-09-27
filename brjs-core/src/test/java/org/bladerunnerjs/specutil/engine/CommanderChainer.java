package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.AppCommander;
import org.bladerunnerjs.specutil.AspectCommander;
import org.bladerunnerjs.specutil.BRJSCommander;
import org.bladerunnerjs.specutil.BladeCommander;
import org.bladerunnerjs.specutil.DirNodeCommander;
import org.bladerunnerjs.specutil.JsLibCommander;
import org.bladerunnerjs.specutil.NamedDirNodeCommander;
import org.bladerunnerjs.specutil.NamedNodeCommander;


public class CommanderChainer {
	private final SpecTest modelTest;
	
	public CommanderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public NamedNodeCommander and(NamedNode namedDirNode) { return new NamedNodeCommander(modelTest, namedDirNode); }
	public BRJSCommander and(BRJS brjs) { return new BRJSCommander(modelTest, brjs); }
	public AppCommander and(App app) { return new AppCommander(modelTest, app); }
	public AspectCommander and(Aspect aspect) { return new AspectCommander(modelTest, aspect); }
	public BladeCommander and(Blade blade) { return new BladeCommander(modelTest, blade); }
	public JsLibCommander and(JsLib jsLib) { return new JsLibCommander(modelTest, jsLib); }
	public DirNodeCommander and(DirNode dirNode) { return new DirNodeCommander(modelTest, dirNode); }
	public NamedDirNodeCommander and(NamedDirNode namedDirNode) { return new NamedDirNodeCommander(modelTest, namedDirNode); }
	public AppServerCommander and(ApplicationServer applicationServer) { return new AppServerCommander(modelTest, applicationServer); }
}
