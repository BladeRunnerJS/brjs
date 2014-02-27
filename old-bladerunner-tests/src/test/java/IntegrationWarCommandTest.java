import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class IntegrationWarCommandTest extends SpecTest {
	private App app;
	private Server warServer = new Server(ServerUtility.getTestPort());
	private StringBuffer pageResponse = new StringBuffer();
	private StringBuffer bundleResponse = new StringBuffer();
	private StringBuffer warResponse = new StringBuffer();
	private StringBuffer brjsResponse = new StringBuffer();
	private Aspect aspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new WarCommand())
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void exportedWarCanBeDeployedOnAnAppServer() throws Exception {
		given(app).hasBeenPopulated()
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app", pageResponse)
			.and(warServer).receivesRequestFor("/app/js/prod/en/combined/bundle.js", bundleResponse);
		then(pageResponse).containsText("Successfully loaded the application")
			.and(pageResponse).containsText("js/prod/en/combined/bundle.js")
			.and(bundleResponse).isNotEmpty();
	}
	
	@Ignore
	@Test
	public void exportedWarIndexPageIsTheSameAsBrjsHosted() throws Exception {
		given(app).hasBeenPopulated()
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app", warResponse)
			.and(app).requestReceived("/", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}
	
	@Ignore
	@Test
	public void exportedWarJsBundleIsTheSameAsBrjsHosted() throws Exception {
		given(app).hasBeenPopulated()
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/js/prod/en/combined/bundle.js", warResponse)
			.and(app).requestReceived("/default-aspect/js/prod/en/combined/bundle.js", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}

	@Ignore
	@Test
	public void exportedWarCssBundleIsTheSameAsBrjsHosted() throws Exception {
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("resources/style.css", "body { color: red; }")
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(warServer).hasWar("app1.war", "app")
			.and(warServer).hasStarted();
		when(warServer).receivesRequestFor("/app/css/common/bundle.css", warResponse)
			.and(app).requestReceived("/default-aspect/css/common/bundle.css", brjsResponse);
		then(warResponse).textEquals(brjsResponse);
	}
	
}
