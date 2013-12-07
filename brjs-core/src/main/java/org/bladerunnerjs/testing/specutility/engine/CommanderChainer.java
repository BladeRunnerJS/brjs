package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.AppCommander;
import org.bladerunnerjs.testing.specutility.AspectCommander;
import org.bladerunnerjs.testing.specutility.BRJSCommander;
import org.bladerunnerjs.testing.specutility.BladeCommander;
import org.bladerunnerjs.testing.specutility.BladesetCommander;
import org.bladerunnerjs.testing.specutility.DirNodeCommander;
import org.bladerunnerjs.testing.specutility.JsLibCommander;
import org.bladerunnerjs.testing.specutility.NamedDirNodeCommander;
import org.bladerunnerjs.testing.specutility.NamedNodeCommander;
import org.bladerunnerjs.testing.specutility.WorkbenchCommander;


public class CommanderChainer {
	private final SpecTest modelTest;
	
	public CommanderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public NamedNodeCommander and(NamedNode namedDirNode) { return new NamedNodeCommander(modelTest, namedDirNode); }
	public BRJSCommander and(BRJS brjs) { return new BRJSCommander(modelTest, brjs); }
	public AppCommander and(App app) { return new AppCommander(modelTest, app); }
	public AspectCommander and(Aspect aspect) { return new AspectCommander(modelTest, aspect); }
	public BladesetCommander and(Bladeset bladeset) { return new BladesetCommander(modelTest, bladeset); }
	public BladeCommander and(Blade blade) { return new BladeCommander(modelTest, blade); }
	public WorkbenchCommander and(Workbench workbench) { return new WorkbenchCommander(modelTest, workbench); }
	public JsLibCommander and(JsLib jsLib) { return new JsLibCommander(modelTest, jsLib); }
	public DirNodeCommander and(DirNode dirNode) { return new DirNodeCommander(modelTest, dirNode); }
	public NamedDirNodeCommander and(NamedDirNode namedDirNode) { return new NamedDirNodeCommander(modelTest, namedDirNode); }
	public AppServerCommander and(ApplicationServer applicationServer) { return new AppServerCommander(modelTest, applicationServer); }
}
