package org.bladerunnerjs.testing.specutility.engine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;


public class RequestListVerifier
{

	private List<String> requests;
	private VerifierChainer verifierChainer;

	public RequestListVerifier(SpecTest specTest, List<String> requests)
	{
		this.requests = requests;
		verifierChainer = new VerifierChainer(specTest);
	}

	public VerifierChainer entriesEqual(String... expectedRequests)
	{
		Assert.assertArrayEquals( "incorrect entries, actual entries were: " + StringUtils.join(requests, ", "), expectedRequests , requests.toArray() );
		return verifierChainer;
	}

}
