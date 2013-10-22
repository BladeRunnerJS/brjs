caplin.testing.GwtTestRunner.initialize();

describe("App Detail Screen", function() {
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	/*TODO: remove the need for the spaces in the then blocks */
	it("has correctly bound controls", function() {
		given("dash.loaded = true");
			and("dash.model.appDetailScreen.newBladesetButton.label = 'new-bladeset-button'");
			and("dash.model.appDetailScreen.importBladesFromAppButton.label = 'import-blades-from-app-button'");
			and("dash.model.appDetailScreen.resetDatabaseButton.label = 'reset-database-button'");
			and("dash.model.appDetailScreen.launchAppButton.label = 'launch-app-button'");
			and("dash.model.appDetailScreen.exportWarButton.label = 'export-war-button'");
			and("dash.model.appDetailScreen.bladesets.length = 1");
			and("dash.model.appDetailScreen.bladesets[0].bladesetName = 'mybladeset'");
			and("dash.model.appDetailScreen.bladesets[0].blades.length = 1");
			and("dash.model.appDetailScreen.bladesets[0].blades[0].bladeName = 'myblade'");
		then("dash.view.(#appDetailScreen .newBladeSetBtn button).text = '   new-bladeset-button     '");
			and("dash.view.(#appDetailScreen .importBladesFromAppBtn button).text = '   import-blades-from-app-button     '");
			and("dash.view.(#appDetailScreen .resetDatabaseBtn button).text = '      reset-database-button  '");
			and("dash.view.(#appDetailScreen .launchAppBtn button).text = '      launch-app-button  '");
			and("dash.view.(#appDetailScreen .exportWarBtn button).text = '      export-war-button  '");
			and("dash.view.(#appDetailScreen .bladeset .bladeset-title .bladeset-title-text).text = 'mybladeset'");
			and("dash.view.(#appDetailScreen .bladeset .blades .blade .blade-title).text = 'myblade'");
	});
	
	it("invokes the newBladeset() method when the 'new bladeset' button is clicked", function() {
		given("dash.loaded = true");
			and("dash.model.appDetailScreen.newBladeset.invocationCount = 0");
			and("dash.model.appDetailScreen.newBladesetButton.enabled = true");
		when("dash.view.(#appDetailScreen .newBladeSetBtn button).clicked => true");
		then("dash.model.appDetailScreen.newBladeset.invocationCount = 1");
	});
	
	it("invokes the importBladesFromApp() method when the 'import blades from app' button is clicked", function() {
		given("dash.loaded = true");
			and("dash.model.appDetailScreen.importBladesFromApp.invocationCount = 0");
			and("dash.model.appDetailScreen.importBladesFromAppButton.enabled = true");
		when("dash.view.(#appDetailScreen .importBladesFromAppBtn button).clicked => true");
		then("dash.model.appDetailScreen.importBladesFromApp.invocationCount = 1");
	});
	
	it("invokes the resetDatabase() method when the 'reset database' button is clicked", function() {
		given("dash.loaded = true");
			and("dash.model.appDetailScreen.resetDatabase.invocationCount = 0");
			and("dash.model.appDetailScreen.resetDatabaseButton.enabled = true");
		when("dash.view.(#appDetailScreen .resetDatabaseBtn button).clicked => true");
		then("dash.model.appDetailScreen.resetDatabase.invocationCount = 1");
	});
	
	it("invokes the launchApp() method when the 'launch app' button is clicked", function() {
		given("dash.loaded = true");
			and("dash.windowOpened.ignoreEvents = true");
			and("dash.model.appDetailScreen.launchApp.invocationCount = 0");
			and("dash.model.appDetailScreen.launchAppButton.enabled = true");
		when("dash.view.(#appDetailScreen .launchAppBtn button).clicked => true");
		then("dash.model.appDetailScreen.launchApp.invocationCount = 1");
	});
	
	// TODO: this test has temporarily been commented out since it tries to change the window location, and there is no WindowOpenerService
//	it("invokes the exportWar() method when the 'export war' button is clicked", function() {
//		given("dash.model.appDetailScreen.exportWar.invocationCount = 0");
//			and("dash.model.appDetailScreen.exportWarButton.enabled = true");
//		when("dash.view.(#appDetailScreen .exportWarBtn button).clicked => true");
//		then("dash.model.appDetailScreen.exportWar.invocationCount = 1");
//	});
});
