import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.plugin.plugins.commands.standard.ServeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class IntegrationServeCommandTest extends SpecTest {
ApplicationServer appServer;
private App app;
private Aspect aspect;
private BladerunnerConf bladerunnerConf;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new ServeCommand())
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
		appServer = brjs.applicationServer(appServerPort);
		brjs.bladerunnerConf().setJettyPort(appServerPort);
		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladerunnerConf = brjs.bladerunnerConf();
	}
	
	@Ignore
	@Test
	public void weCanServeTheIndexPageUsingTheUTF16Encoding() throws Exception
	{
		given(bladerunnerConf).defaultOutputEncodingIs("UTF-16")
			.and(app).hasBeenPopulated()
			.and(aspect).indexPageHasContent("$£€");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestForUrlReturns("/app1/", "$£€");
	}
	
	@Ignore
	@Test
	public void weCanServeContentUsingTheUTF16Encoding() throws Exception
	{
		// TODO
	}
}
