package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.AppCommander;
import org.bladerunnerjs.testing.specutility.AppConfCommander;
import org.bladerunnerjs.testing.specutility.AspectCommander;
import org.bladerunnerjs.testing.specutility.BRJSCommander;
import org.bladerunnerjs.testing.specutility.BladeCommander;
import org.bladerunnerjs.testing.specutility.BladesetCommander;
import org.bladerunnerjs.testing.specutility.DirNodeCommander;
import org.bladerunnerjs.testing.specutility.JsLibCommander;
import org.bladerunnerjs.testing.specutility.NamedDirNodeCommander;
import org.bladerunnerjs.testing.specutility.NamedNodeCommander;
import org.bladerunnerjs.testing.specutility.WorkbenchCommander;
import org.eclipse.jetty.server.Server;


public class CommanderChainer {
	private final SpecTest modelTest;
	
	public CommanderChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public NamedNodeCommander and(NamedNode namedDirNode) { return new NamedNodeCommander(modelTest, namedDirNode); }
	public BRJSCommander and(BRJS brjs) { return new BRJSCommander(modelTest, brjs); }
	public AppCommander and(App app) { return new AppCommander(modelTest, app); }
	public AppConfCommander when(AppConf appConf) { return new AppConfCommander(modelTest, appConf); }
	public AspectCommander and(Aspect aspect) { return new AspectCommander(modelTest, aspect); }
	public BladesetCommander and(Bladeset bladeset) { return new BladesetCommander(modelTest, bladeset); }
	public BladeCommander and(Blade blade) { return new BladeCommander(modelTest, blade); }
	public WorkbenchCommander and(BladeWorkbench workbench) { return new WorkbenchCommander(modelTest, workbench); }
	public JsLibCommander and(JsLib jsLib) { return new JsLibCommander(modelTest, jsLib); }
	public DirNodeCommander and(DirNode dirNode) { return new DirNodeCommander(modelTest, dirNode); }
	public NamedDirNodeCommander and(NamedDirNode namedDirNode) { return new NamedDirNodeCommander(modelTest, namedDirNode); }
	public AppServerCommander and(ApplicationServer applicationServer) { return new AppServerCommander(modelTest, applicationServer); }
	public JettyServerCommander and(Server jettyServer) { return new JettyServerCommander(modelTest, jettyServer); }
}
