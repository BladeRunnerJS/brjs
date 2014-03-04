package org.bladerunnerjs.testing.specutility.engine;

import org.apache.http.client.fluent.Request;
import org.eclipse.jetty.server.Server;

public class JettyServerCommander {
	private final Server jettyServer;
	private final CommanderChainer commanderChainer;
	
	public JettyServerCommander(SpecTest specTest, Server jettyServer) {
		this.jettyServer = jettyServer;
		commanderChainer = new CommanderChainer(specTest);
	}
	
	public CommanderChainer receivesRequestFor(String requestPath, StringBuffer response) throws Exception {
		int jettyPort = jettyServer.getConnectors()[0].getPort();
		response.append(Request.Get("http://localhost:" + jettyPort + requestPath).execute().returnContent());
		
		return commanderChainer;
	}
}
