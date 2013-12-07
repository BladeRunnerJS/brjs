package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.testing.utility.WebappTester;


public class WebappTesterCommander
{
	
	private WebappTester webappTester;
@SuppressWarnings("unused")
	private SpecTest specTest;
	private VerifierChainer verifierChainer;

	public WebappTesterCommander(SpecTest specTest, WebappTester webappTester)
	{
		this.specTest = specTest;
		this.webappTester = webappTester;
		verifierChainer = new VerifierChainer(specTest);
	}

	public VerifierChainer makesRequestWithLocale(String locale)
	{
		webappTester.requestLocale = locale;
		
		return verifierChainer;
	}

}
