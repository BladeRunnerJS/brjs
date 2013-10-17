caplin.testing.GwtTestRunner.initialize();

// Story 4 - VIEW APP DETAILS
describe("story #4", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	it("should display a correctly formatted crumbtrail when the app details page is loaded", function() {
		given("dash.loaded = true");
			and("page.url = '#apps/mynewtrader'");
		then("dash.model.crumbtrail.visible = true");
			and("dash.model.crumbtrail.crumbs.length = 2");
			and("dash.model.crumbtrail.crumbs[0].className = 'home-breadcrumb'");
			and("dash.model.crumbtrail.crumbs[1].className = 'active-breadcrumb'");
			and("dash.model.crumbtrail.crumbs[1].name = 'mynewtrader'");
	});
	
	it("can display single app details with no bladesets", function() {
		given("dash.loaded = true");
			and("page.url => '#apps/mynewtrader'");
		when("dash.service.responseReceived => '200 {}'");
		then("dash.model.appDetailScreen.appName = 'mynewtrader'")
			and("dash.model.appDetailScreen.bladesets.length = 0");
			and("dash.model.appDetailScreen.exportWarButton.enabled = true");
			and("dash.model.appDetailScreen.launchAppButton.enabled = true");
			and("dash.model.appDetailScreen.newBladesetButton.enabled = true");
			and("dash.model.appDetailScreen.importBladesFromAppButton.enabled = false");
	});
	
	it("makes a request for all apps after receiving a response for current application", function() {
		given("dash.loaded = true");
			and("page.url = '#apps/mynewtrader'");
		when("dash.service.responseReceived => '200 {\"blue\":[\"cyan\", \"sky\"], \"red\":[\"crimson\", \"scarlet\"]}'");
		then("dash.service.requestSent = 'GET /sdk/version'");
			and("dash.service.requestSent = 'GET /apps'");
			and("dash.service.requestSent = 'GET /apps/mynewtrader'");
			and("dash.service.noMoreRequests = true");
	});
	
	it("enables the Import Blades From App button when there are multiple applications", function() {
		given("test.continuesFrom = 'makes a request for all apps after receiving a response for current application'");
		when("dash.service.responseReceived => '200 [\"fxtrader\", \"mynewtrader\"]'");
		then("dash.model.appDetailScreen.importBladesFromAppButton.enabled = true");
			and("dash.service.noMoreRequests = true");
	});
	
	it("can display single app details with multiple bladesets and blades", function() {
		given("test.continuesFrom = 'makes a request for all apps after receiving a response for current application'");
		then("dash.model.appDetailScreen.appName = 'mynewtrader'");
			and("dash.model.appDetailScreen.bladesets.length = 2");
			and("dash.model.appDetailScreen.bladesets[0].bladesetName = 'blue'");
			and("dash.model.appDetailScreen.bladesets[0].blades[0].bladeName = 'cyan'");
			and("dash.model.appDetailScreen.bladesets[0].blades[1].bladeName = 'sky'");
			and("dash.model.appDetailScreen.bladesets[1].bladesetName = 'red'");
			and("dash.model.appDetailScreen.bladesets[1].blades[0].bladeName = 'crimson'");
			and("dash.model.appDetailScreen.bladesets[1].blades[1].bladeName = 'scarlet'");
			and("dash.model.appDetailScreen.exportWarButton.enabled = true");
			and("dash.model.appDetailScreen.launchAppButton.enabled = true");
			and("dash.model.appDetailScreen.newBladesetButton.enabled = true");
			and("dash.model.appDetailScreen.importBladesFromAppButton.enabled = false");
	});
	
});


