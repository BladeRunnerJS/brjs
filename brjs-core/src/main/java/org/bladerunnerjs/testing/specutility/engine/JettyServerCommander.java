package org.bladerunnerjs.testing.specutility.engine;

import org.apache.http.client.fluent.Request;
import org.eclipse.jetty.server.Server;

public class JettyServerCommander {
	private final Server jettyServer;
	
	public JettyServerCommander(SpecTest specTest, Server jettyServer) {
		this.jettyServer = jettyServer;
	}
	
	public void receivesRequestFor(String requestPath, StringBuffer response) throws Exception {
		int jettyPort = jettyServer.getConnectors()[0].getPort();
		response.append(Request.Get("http://localhost:" + jettyPort + requestPath).execute().returnContent());
	}
}
