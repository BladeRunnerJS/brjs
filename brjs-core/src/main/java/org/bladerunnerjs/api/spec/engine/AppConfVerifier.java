package org.bladerunnerjs.api.spec.engine;

import static org.junit.Assert.*;

import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.model.exception.ConfigException;


public class AppConfVerifier
{
	AppConf appConf;
	
	public AppConfVerifier(SpecTest specTest, AppConf appConf) {
		this.appConf = appConf;
	}
	
	public void namespaceIs(String string) throws ConfigException
	{
		assertEquals("app conf has the wrong namespace", appConf.getRequirePrefix(), string);
	}
}
