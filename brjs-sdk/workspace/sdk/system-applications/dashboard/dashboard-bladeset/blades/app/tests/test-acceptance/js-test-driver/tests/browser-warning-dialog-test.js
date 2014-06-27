br.test.GwtTestRunner.initialize();

describe("browser warning dialog", function() 
{
	fixtures("brjs.dashboard.app.testing.DashboardFixtureFactory");
	
	it("displays the dialog for ie6", function() {
		given("browser.name = 'ie'");
			and("browser.version = '6'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'browserWarningDialog'");
	});
	
	it("displays the dialog for ie7", function() {
		given("browser.name = 'ie'");
			and("browser.version = '7'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'browserWarningDialog'");
	});
	
	it("displays the dialog for ie8", function() {
		given("browser.name = 'ie'");
			and("browser.version = '8'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'browserWarningDialog'");
	});

	it("doesnt display the dialog for ie9", function() {
		given("browser.name = 'ie'");
			and("browser.version = '9'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = false");
	});
	
	it("doesnt display dialog for chrome 15", function() {
		given("browser.name = 'chrome'");
			and("browser.version = '15'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = false");
	});
	
	it("doesnt display dialog for an unknown browser", function() {
		given("browser.name = 'someUnknownBrowser'");
			and("browser.version = '15'");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = false");
	});
	
	it("doesnt display dialog for an unknown version", function() {
		given("browser.name = 'chrome'");
			and("browser.version = ''");
			and("dash.loaded = true");
		then("dash.model.dialog.visible = false");
	});
	

});
