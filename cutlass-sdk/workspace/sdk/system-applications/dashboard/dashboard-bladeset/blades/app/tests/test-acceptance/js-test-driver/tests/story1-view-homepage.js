br.test.GwtTestRunner.initialize();

// Story 1 - DASHBOARD HOME PAGE
describe("story #1", function()
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	it("shouldn't display the crumbtrail when the homepage is loaded", function() {
		given("dash.loaded = true");
			and("page.url = ''");
		then("dash.model.crumbtrail.visible = false");
	});
	
	it("should request app info when the front page is requested", function() {
		given("dash.loaded = true");
			and("page.url = ''");
		then("dash.service.requestSent = 'GET /sdk/version'"); 	
			and("dash.service.requestSent = 'GET /apps'");				// BEFORE the page.url has been set
			and("dash.service.requestSent = 'GET /apps'"); 				// AFTER the page.url has been set
			and("dash.service.noMoreRequests = true");
			and("dash.model.appsScreen.apps.length = 0");
	});
	
	it("should display all available apps when the app info arrives", function() {
		given("test.continuesFrom = 'should request app info when the front page is requested'");
		when("dash.service.responseReceived => '<DEFAULT_APPS>'");
		then("dash.model.appsScreen.visible = true");
			and("dash.model.appDetailScreen.visible = false");
			and("dash.model.releaseNoteScreen.visible = false");
			and("dash.model.dialog.visible = false");
			and("dash.model.appsScreen.apps.length = 1");
			and("dash.model.appsScreen.apps[0].appName = 'Example App'");
	});
	
	it("can display multiple apps", function() {
		given("test.continuesFrom = 'should request app info when the front page is requested'");
		when("dash.service.responseReceived => '200 [\"fxtrader\", \"fitrader\", \"novotrader\"]'");
		then("dash.model.appsScreen.apps.length = 3");
			and("dash.model.appsScreen.apps[0].appName = 'fxtrader'");
			and("dash.model.appsScreen.apps[1].appName = 'fitrader'");
			and("dash.model.appsScreen.apps[2].appName = 'novotrader'");
			and("dash.model.appsScreen.visible = true");
			and("dash.model.appDetailScreen.visible = false");
			and("dash.model.releaseNoteScreen.visible = false");
			and("dash.model.dialog.visible = false");
	});
	
	it("displays the sdk version when the response is recieved", function() {
		given("test.continuesFrom = 'should request app info when the front page is requested'");
		when("dash.service.responseReceived => '200 {}'");
			and("dash.service.responseReceived => '200 {}'");
			and("dash.service.responseReceived => '200 {\"Version\":\"1.2.3\"}'");
		then("dash.model.sdkVersion = '1.2.3'");
	});
	
});
