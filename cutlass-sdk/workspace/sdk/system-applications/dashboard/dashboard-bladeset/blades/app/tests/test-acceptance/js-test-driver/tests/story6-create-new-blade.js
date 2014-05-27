br.test.GwtTestRunner.initialize();

// Story 6 - CREATE NEW BLADE
describe("story #6", function() 
{
	fixtures("brjs.dashboard.app.testing.DashboardFixtureFactory");
	
	
	it("displays the new-blade dialog when the New Blade context menu button is invoked", function() {
	   	given("test.continuesFrom = 'story #4::can display single app details with multiple bladesets and blades'");
	   	when("dash.model.appDetailScreen.bladesets[0].newBlade.invoked => true");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'newBladeDialog'");
			and("dash.model.dialog.viewNode.current.bladeName.value = ''");
			and("dash.model.dialog.viewNode.current.bladeName.hasFocus = true");
			and("dash.model.dialog.viewNode.current.createBladeButton.enabled = false");
	});
	
	it("enables the Create Blade button when a valid blade name is provided", function() {
	   	given("test.continuesFrom = 'displays the new-blade dialog when the New Blade context menu button is invoked'");
	   	when("dash.model.dialog.viewNode.current.bladeName.value => 'cobalt'");
		then("dash.model.dialog.viewNode.current.createBladeButton.enabled = true");
	});
	
	// TODO when validator is added
	it("disables the Create Blade button when an invalid blade name is provided - UPPERCASE", function() {
	   	given("test.continuesFrom = 'enables the Create Blade button when a valid blade name is provided'");
	   	when("dash.model.dialog.viewNode.current.bladeName.value => 'GRID'");
		then("dash.model.dialog.viewNode.current.createBladeButton.enabled = false");
			and("dash.model.dialog.viewNode.current.bladeName.hasError = true")
			and("dash.model.dialog.viewNode.current.bladeName.failureMessage = 'Package names can only contain lower-case alphanumeric characters, where the first character is non-numeric.'")
	});
	
	it("re-disables the Create Blade button when a valid blade name is set back to blank", function() {
	   	given("test.continuesFrom = 'enables the Create Blade button when a valid blade name is provided'");
	   	when("dash.model.dialog.viewNode.current.bladeName.value => ''");
		then("dash.model.dialog.viewNode.current.createBladeButton.enabled = false");
			and("dash.model.dialog.viewNode.current.bladeName.hasError = false")
	});

	it("should issue a create-blade request when the Create Blade button is invoked", function() {
		given("test.continuesFrom = 'enables the Create Blade button when a valid blade name is provided'");
		when("dash.model.dialog.viewNode.current.createBlade.invoked => true");
		then("dash.service.requestSent = 'POST /apps/mynewtrader/blue/cobalt {command:create-blade}'");
			and("dash.service.requestSent = 'GET /apps'");
			and("dash.model.appDetailScreen.bladesets.0.blades.length = 2");
			and("dash.model.dialog.visible = false");
			and("dash.service.noMoreRequests = true");
	});
	
	it("sends a new GET request after invoking the create-blade operation succesfully", function() {
		given("test.continuesFrom = 'should issue a create-blade request when the Create Blade button is invoked'");
		when("dash.service.responseReceived => '200'");
			and("dash.service.responseReceived => '200'");
		then("dash.service.requestSent = 'GET /apps/mynewtrader'");
			and("dash.model.dialog.visible = false");
	});
	
	it("reloads the app details screen information after a create-blade operation has been succesfully performed", function() {
		given("test.continuesFrom = 'sends a new GET request after invoking the create-blade operation succesfully'");
		when("dash.service.responseReceived => '200 {\"blue\":[\"cyan\", \"sky\", \"bright\"], \"red\":[\"crimson\", \"scarlet\"]}'");
		then("dash.model.appDetailScreen.bladesets.length = 2");
			and("dash.model.appDetailScreen.bladesets[0].bladesetName = 'blue'");
			and("dash.model.appDetailScreen.bladesets[0].blades.length = 3");
			and("dash.model.appDetailScreen.bladesets[0].blades[0].bladeName = 'cyan'");
			and("dash.model.appDetailScreen.bladesets[0].blades[1].bladeName = 'sky'");
			and("dash.model.appDetailScreen.bladesets[0].blades[2].bladeName = 'bright'");
	});
	
	it("attempting to create a blade that already exists disables the dialog Create button", function() {
		given("test.continuesFrom = 'reloads the app details screen information after a create-blade operation has been succesfully performed'");
		when("dash.model.dialog.viewNode.current.createBlade.invoked => true");
			and("dash.model.dialog.viewNode.current.bladeName.value => 'cyan'");
		then("dash.model.dialog.viewNode.current.createBladeButton.enabled = false");
			and("dash.model.dialog.viewNode.current.bladeName.hasError = true");
			and("dash.model.dialog.viewNode.current.bladeName.failureMessage = 'A blade called \'cyan\' already exists.'");
	});
	
	it("displays the received error message if the create-blade operation fails", function() {
		given("test.continuesFrom = 'displays the new-blade dialog when the New Blade context menu button is invoked'");
		when("dash.service.responseReceived => '500 {\"message\":\"There has been a technical fault.\"}'");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'notificationDialog'");
			and("dash.model.dialog.viewNode.current.message = 'There has been a technical fault.'");
	});
	
});


