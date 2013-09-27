package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.appserver.ApplicationServer;


public class AppServerCommander extends ModelCommander
{
	
	ApplicationServer appServer;
	BuilderChainer chainer;
	
	public AppServerCommander(SpecTest specTest, ApplicationServer appServer)
	{
		super(specTest);
		this.appServer = appServer;
		chainer = new BuilderChainer(modelTest);
	}

	public BuilderChainer started() throws Exception
	{
		call(new Command() {
			public void call() throws Exception {
				appServer.start();
			}
		});
		
		return chainer;
	}
	
	public BuilderChainer stopped() throws Exception
	{
		call(new Command() {
			public void call() throws Exception {
				appServer.stop();
			}
		});
		
		return chainer;
	}

}
