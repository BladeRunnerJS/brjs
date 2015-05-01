package org.bladerunnerjs.api.spec.engine;

import org.apache.http.client.HttpResponseException;
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
		String url = "http://localhost:" + jettyPort + requestPath;
		try {
			response.append(Request.Get(url).execute().returnContent());
		} catch (HttpResponseException ex) {
			throw new Exception("Unable to get a response for "+url+". If running from a built app it may not have been exported if the corresponding tag plugin wasn't used", ex);
		}
		
		return commanderChainer;
	}
}
