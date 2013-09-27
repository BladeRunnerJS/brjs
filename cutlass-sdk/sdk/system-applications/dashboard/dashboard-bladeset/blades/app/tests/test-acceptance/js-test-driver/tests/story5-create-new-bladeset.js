caplin.testing.GwtTestRunner.initialize();

// Story 5 - CREATE NEW BLADESET
describe("story #5", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");

	it("displays the new-bladeset dialog when the New Bladeset button is invoked", function() {
	   	given("test.continuesFrom = 'story #4::can display single app details with no bladesets'");
	   	when("dash.model.appDetailScreen.newBladeset.invoked => true");
		then("dash.model.appDetailScreen.visible = true");
			and("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'newBladesetDialog'");
			and("dash.model.dialog.viewNode.current.bladesetName.value = ''");
			and("dash.model.dialog.viewNode.current.bladesetName.hasFocus = true");
			and("dash.model.dialog.viewNode.current.createBladesetButton.enabled = false");
			and("dash.service.requestSent = 'GET /sdk/version'");
			and("dash.service.requestSent = 'GET /apps'");
			and("dash.service.requestSent = 'GET /apps/mynewtrader'");
			and("dash.service.noMoreRequests = true");
	});
	
	it("enables the Create Bladeset button when a valid bladesetName is specified", function() {
	   	given("test.continuesFrom = 'displays the new-bladeset dialog when the New Bladeset button is invoked'");
	   	when("dash.model.dialog.viewNode.current.bladesetName.value => 'fx'");
		then("dash.model.dialog.viewNode.current.createBladesetButton.enabled = true");
	});
	
	it ("re-disables when bladesetName is set to blank", function() {
		given("test.continuesFrom = 'enables the Create Bladeset button when a valid bladesetName is specified'");
	   	when("dash.model.dialog.viewNode.current.bladesetName.value => ''");
		then("dash.model.dialog.viewNode.current.createBladesetButton.enabled = false");
	});
	
	it("should issue a create-bladeset request when the CreateBladeset button is invoked", function() {
		given("test.continuesFrom = 'enables the Create Bladeset button when a valid bladesetName is specified'");
		when("dash.model.dialog.viewNode.current.createBladeset.invoked => true");		
		then("dash.service.requestSent = 'POST /apps/mynewtrader/fx {command:create-bladeset}'");
			and("dash.service.requestSent = 'GET /apps'");		// Triggered when dialog is closed
			and("dash.service.noMoreRequests = true");
			and("dash.model.dialog.visible = false");
	});
	
	it("sends a GET for the application after invoking the create-bladeset operation succesfully", function() {
		given("test.continuesFrom = 'should issue a create-bladeset request when the CreateBladeset button is invoked'");
		when("dash.service.responseReceived => '200'");			// This is for the POST /apps/mynewtrader/fx create-bladeset 
			and("dash.service.responseReceived => '200'");		// This is for the GET /apps
		then("dash.service.requestSent = 'GET /apps/mynewtrader'");
			and("dash.model.dialog.visible = false");
	});

	it("reloads the app details screen information after a create-bladeset operation has been succesfully performed", function() {
		given("test.continuesFrom = 'sends a GET for the application after invoking the create-bladeset operation succesfully'");
		when("dash.service.responseReceived => '200 {\"fx\":[]}'");
		then("dash.model.appDetailScreen.bladesets.length = 1");
			and("dash.model.appDetailScreen.bladesets[0].bladesetName = 'fx'");
			and("dash.model.appDetailScreen.bladesets[0].blades.length = 0");
			and("dash.model.appDetailScreen.resetDatabaseButton.enabled = true");
			and("dash.model.appDetailScreen.exportWarButton.enabled = true");
			and("dash.model.appDetailScreen.launchAppButton.enabled = true");
			and("dash.model.appDetailScreen.newBladesetButton.enabled = true");
			and("dash.model.appDetailScreen.importBladesFromAppButton.enabled = false");
	});
	
	it("attempting to create a bladeset that already exists disables the dialog Create button", function() {
		given("test.continuesFrom = 'reloads the app details screen information after a create-bladeset operation has been succesfully performed'");
		when("dash.model.dialog.viewNode.current.createBladeset.invoked => true");
			and("dash.model.dialog.viewNode.current.bladesetName.value => 'fx'");
		then("dash.model.dialog.viewNode.current.createBladesetButton.enabled = false");
			and("dash.model.dialog.viewNode.current.bladesetName.hasError = true");
			and("dash.model.dialog.viewNode.current.bladesetName.failureMessage = 'A bladeset called \'fx\' already exists.'");
	});
	
	it("displays the received error message if the create-bladeset operation fails", function() {
		given("test.continuesFrom = 'displays the new-bladeset dialog when the New Bladeset button is invoked'");
		when("dash.service.responseReceived => '500 {\"message\":\"There has been a technical fault.\"}'");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'notificationDialog'");
			and("dash.model.dialog.viewNode.current.message = 'There has been a technical fault.'");
	});
	
});


