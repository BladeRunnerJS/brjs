package dummysdk;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.ServletModelAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;
import org.bladerunnerjs.testing.utility.WebappTester;


public class WorkbenchEndToEndTests
{

	private static final File INSTALL_ROOT = new File("src/test/resources/MockInstall");

	private static final int HTTP_PORT = ServerUtility.getTestPort();
	private static final String BASE_URL = "http://localhost:"+HTTP_PORT;
	private static final String APP1_URL = BASE_URL+"/test-app1";
	private static final String APPS = CutlassConfig.APPLICATIONS_DIR;
	
	private ApplicationServer appServer;
	private WebappTester tester;
	
	@Before
	public void setup() throws Exception
	{
		ServletModelAccessor.reset();
		File tempSdkInstall = FileUtility.createTemporarySdkInstall(INSTALL_ROOT).getParentFile();
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempSdkInstall));
		brjs.bladerunnerConf().setJettyPort(HTTP_PORT);
		appServer = brjs.applicationServer(HTTP_PORT);
		appServer.start();
		tester = new WebappTester(tempSdkInstall, 5000, 5000);
	}
	
	@After
	public void teardown() throws Exception
	{
		appServer.stop();
	}
	
	/* start of tests */
	
	
	@Test
	public void testRequestToJsBundleForWorkbench() throws Exception 
	{
		tester.whenRequestMadeTo(APP1_URL+"/a-bladeset/blades/blade1/workbench/js/js.bundle")
		.statusCodeIs(200).contentTypeIs("text/javascript")
		.responseIsConcatenationOfFiles(new String[]{
				APPS + "/test-app1/a-bladeset/blades/blade1/workbench/src/workbench/wb1.js",
				APPS + "/test-app1/a-bladeset/blades/blade1/workbench/src/workbench/wb2.js",
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPS + "/test-app1/a-bladeset/src/section/a/xmlDepend.js",
				CutlassConfig.SDK_DIR + "/libs/javascript/caplin/src/caplin/bootstrap.js",
				APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js",
				APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js",
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js",
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js"});
	}
}
