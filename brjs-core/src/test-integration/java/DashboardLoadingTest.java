
import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;



public class DashboardLoadingTest extends SpecTest
{

	private App dashboard;

	@Before
	public void initTestObjects() throws Exception {
		testSdkDirectory = new File("../cutlass-sdk/workspace/sdk/");
		given(brjs).hasBeenAuthenticallyCreated();
		dashboard = brjs.systemApp("dashboard");
	}
	
	@Test
	public void appIsNotHostedUnlessAppIsDeployed() throws Exception
	{
		StringBuffer response = new StringBuffer();
		when(dashboard).requestReceived("/default-aspect/js/prod/en_GB/combined/bundle.js", response);
//		System.err.println(response);
	}
	
}
