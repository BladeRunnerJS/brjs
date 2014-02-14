import org.bladerunnerjs.model.App;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;

public class IntegrationWarCommandTest extends SpecTest {
	private App app;
	private Server server = new Server(ServerUtility.getTestPort());
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new WarCommand())
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
			app = brjs.app("app1");
	}
	
	@Test
	public void exportedWarCanBeDeployedOnAnAppServer() throws Exception {
		given(app).hasBeenPopulated()
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(server).hasWar("app1.war", "app")
			.and(server).hasStarted();
		when(server).receivesRequestFor("/app", response);
		then(response).containsText("Successfully loaded the application")
			.and(response).containsText("_css.bundle");
	}
}
