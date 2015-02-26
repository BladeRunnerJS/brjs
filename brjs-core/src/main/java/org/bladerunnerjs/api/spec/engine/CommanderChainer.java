package org.bladerunnerjs.api.spec.engine;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.spec.utility.AppCommander;
import org.bladerunnerjs.api.spec.utility.AppConfCommander;
import org.bladerunnerjs.api.spec.utility.AspectCommander;
import org.bladerunnerjs.api.spec.utility.BRJSCommander;
import org.bladerunnerjs.api.spec.utility.BladeCommander;
import org.bladerunnerjs.api.spec.utility.BladesetCommander;
import org.bladerunnerjs.api.spec.utility.DirNodeCommander;
import org.bladerunnerjs.api.spec.utility.JsLibCommander;
import org.bladerunnerjs.api.spec.utility.NamedDirNodeCommander;
import org.bladerunnerjs.api.spec.utility.NamedNodeCommander;
import org.bladerunnerjs.api.spec.utility.WorkbenchCommander;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.model.engine.NamedNode;
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
