package org.bladerunnerjs.api.spec.engine;

import java.io.File;
import javax.naming.NamingException;
import javax.servlet.Filter;

import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
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
		return hasWar(specTest.brjs.workingDir().file(warPath), appName);
	}
	
	public BuilderChainer hasWar(File warPath, String appName) {
		new WebAppContext((HandlerContainer) jettyServer.getHandler(), warPath.getPath(), "/" + appName);
		
		return builderChainer;
	}
	
	public BuilderChainer hasWarWithFilters(String warPath, String appName, Filter... filters) throws NamingException {
		return hasWarWithFilters(specTest.brjs.workingDir().file(warPath), appName, filters);
	}
	
	public BuilderChainer hasWarWithFilters(File warPath, String appName, Filter... filters) throws NamingException {
		WebAppContext webappContext = new WebAppContext((HandlerContainer) jettyServer.getHandler(), warPath.getPath(), "/" + appName);
		for (Filter filter : filters) {
			webappContext.addFilter(new FilterHolder(filter), "/*", 1);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasStarted() throws Exception {
		jettyServer.start();
		jettyServer.setStopAtShutdown(true);
		
		return builderChainer;
	}
	
}
