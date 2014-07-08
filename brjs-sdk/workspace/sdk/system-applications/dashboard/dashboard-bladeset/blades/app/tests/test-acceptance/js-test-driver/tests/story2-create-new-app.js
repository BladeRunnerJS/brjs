br.test.GwtTestRunner.initialize();

// Story 2 - CREATE NEW APP
describe("story #2", function() 
{
	fixtures("brjs.dashboard.app.testing.DashboardFixtureFactory");
	
	
	it("displays the new-app dialog when the create-app button is invoked", function() {
		given("test.continuesFrom = 'story #1::should display all available apps when the app info arrives'");
		when("dash.model.appsScreen.newApp.invoked => true");
		then("dash.model.appsScreen.visible = true");
			and("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'newAppDialog'");
			and("dash.model.dialog.viewNode.current.appName.value = ''");
			and("dash.model.dialog.viewNode.current.appName.hasFocus = true");
			and("dash.model.dialog.viewNode.current.appNamespace.value = ''");
			and("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("disables the 'create' button if the app name hasn't been entered", function() {
		given("test.continuesFrom = 'displays the new-app dialog when the create-app button is invoked'");
		when("dash.model.dialog.viewNode.current.appName.value => ''");
			and("dash.model.dialog.viewNode.current.appNamespace.value => 'novox'");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("disables the 'create' button if the app namespace hasn't been entered", function() {
		given("test.continuesFrom = 'displays the new-app dialog when the create-app button is invoked'");
		when("dash.model.dialog.viewNode.current.appName.value => 'my-new-app'");
			and("dash.model.dialog.viewNode.current.appNamespace.value => ''");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("enables the create app button when all fields have been entered with valid values", function() {
		given("test.continuesFrom = 'displays the new-app dialog when the create-app button is invoked'");
		when("dash.model.dialog.viewNode.current.appName.value => 'novotrader'");
			and("dash.model.dialog.viewNode.current.appNamespace.value => 'novox'");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = true");
			and("dash.model.dialog.viewNode.current.createAppButton.visible = true");
	});
	
	it("disables the 'create' button if the app namespace is not all lower case", function() {
		given("test.continuesFrom = 'enables the create app button when all fields have been entered with valid values'");
		when("dash.model.dialog.viewNode.current.appNamespace.value => 'novoX'");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("disables the 'create' button if the app namespace is a reserved name", function() {
		given("test.continuesFrom = 'enables the create app button when all fields have been entered with valid values'");
		when("dash.model.dialog.viewNode.current.appNamespace.value => 'brjs'");
		then("dash.model.dialog.viewNode.current.appNamespace.hasError = true");
			and("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
			and("dash.model.dialog.viewNode.current.appNamespace.failureMessage = '\'brjs\' is a reserved namespace.'");		
	});
	
	it("re-disables the create app button when valid appName turns into invalid field value", function() {
		given("test.continuesFrom = 'enables the create app button when all fields have been entered with valid values'");
		when("dash.model.dialog.viewNode.current.appName.value => ''");
		then("dash.model.dialog.viewNode.current.appName.hasError = false");
			and("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
			and("dash.model.dialog.viewNode.current.appName.failureMessage = ''");   
	});
	
	it("re-disables the create app button when valid appNamespace turns into invalid field value", function() {
		given("test.continuesFrom = 'enables the create app button when all fields have been entered with valid values'");
		when("dash.model.dialog.viewNode.current.appNamespace.value => ''");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
			and("dash.model.dialog.viewNode.current.appNamespace.hasError = false");
			and("dash.model.dialog.viewNode.current.appNamespace.failureMessage = ''");   
	});
	 
	it("should issue a create-app request when the create-app button is clicked", function() {
		given("test.continuesFrom = 'displays the new-app dialog when the create-app button is invoked'");
		when("dash.model.dialog.viewNode.current.appName.value => 'my-new-app'");
			and("dash.model.dialog.viewNode.current.appNamespace.value => 'novox'");
			and("dash.model.dialog.viewNode.current.createApp.invoked => true");
		then("dash.service.requestSent = 'POST /apps/my-new-app {command:create-app,namespace:novox}'");
			and("dash.model.dialog.visible = false");
	});
	 
	it("reloads the app information after a create-app operation has been succesfully performed", function() {
		given("test.continuesFrom = 'should issue a create-app request when the create-app button is clicked'");
		when("dash.service.responseReceived => '200'");
		then("dash.service.requestSent = 'GET /apps'");
			and("dash.model.dialog.visible = false");
	});
	
	it("displays the received error message if the create-app operation fails", function() {
		given("test.continuesFrom = 'displays the new-app dialog when the create-app button is invoked'");
		when("dash.service.responseReceived => '500 {\"message\":\"There has been a technical fault.\"}'");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'notificationDialog'");
			and("dash.model.dialog.viewNode.current.message = 'There has been a technical fault.'");
	});
	
	it("remembers the namespace value from the previous create app dialog entry", function() {
		given("test.continuesFrom = 'reloads the app information after a create-app operation has been succesfully performed'");
		when("dash.model.appsScreen.newApp.invoked => true");
		then("dash.model.appsScreen.visible = true");
			and("dash.model.dialog.visible = true");
			and("dash.model.dialog.viewNode.current.appName.value = ''");
			and("dash.model.dialog.viewNode.current.appNamespace.value = 'novox'");
	});
	
	it("should disable the import from zip button if the browser is ie9", function() {
		given("browser.name = 'ie'");
			and("browser.version = '9'");
			and("dash.loaded = true");
		then("dash.model.appsScreen.importMotifFromZipButton.enabled = false");
	});
	
	// TODO VIEW/MODEL BINDING TESTS:
	// Check dialog DOM
	// Check dialog DOM with invalid values
});
