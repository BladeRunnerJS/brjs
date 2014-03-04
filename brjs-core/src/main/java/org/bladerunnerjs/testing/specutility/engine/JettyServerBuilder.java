package org.bladerunnerjs.testing.specutility.engine;

import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServerBuilder {
	private final SpecTest specTest;
	private final Server jettyServer;
	private final BuilderChainer builderChainer;
	
	public JettyServerBuilder(SpecTest specTest, Server jettyServer) {
		this.specTest = specTest;
		this.jettyServer = jettyServer;
		builderChainer = new BuilderChainer(specTest);
		
		if(jettyServer.getHandler() == null) {
			jettyServer.setHandler(new ContextHandlerCollection());
		}
	}
	
	public BuilderChainer hasWar(String warPath, String appName) {
		new WebAppContext((HandlerContainer) jettyServer.getHandler(), specTest.brjs.workingDir().file(warPath).getPath(), "/" + appName);
		
		return builderChainer;
	}
	
	public BuilderChainer hasStarted() throws Exception {
		jettyServer.start();
		jettyServer.setStopAtShutdown(true);
		
		return builderChainer;
	}
}
