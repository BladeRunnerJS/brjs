package org.bladerunnerjs.api.spec.engine;

import java.io.File;
import java.util.EnumSet;

import javax.naming.NamingException;
import javax.servlet.DispatcherType;
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
		return hasWar(specTest.brjs.file(warPath), appName);
	}
	
	public BuilderChainer hasWar(File warPath, String appName) {
		new WebAppContext((HandlerContainer) jettyServer.getHandler(), warPath.getPath(), "/" + appName);
		
		return builderChainer;
	}
	
	public BuilderChainer hasWarWithFilters(String warPath, String appName, Filter... filters) throws NamingException {
		return hasWarWithFilters(specTest.brjs.file(warPath), appName, filters);
	}
	
	public BuilderChainer hasWarWithFilters(File warPath, String appName, Filter... filters) throws NamingException {
		WebAppContext webappContext = new WebAppContext((HandlerContainer) jettyServer.getHandler(), warPath.getPath(), "/" + appName);
		for (Filter filter : filters) {
			webappContext.addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasStarted() throws Exception {
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");
		
		jettyServer.start();
		jettyServer.setStopAtShutdown(true);
		
		return builderChainer;
	}
	
}
