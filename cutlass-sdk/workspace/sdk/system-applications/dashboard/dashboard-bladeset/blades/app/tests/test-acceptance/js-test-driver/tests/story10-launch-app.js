br.test.GwtTestRunner.initialize();

// Story 10 - LAUNCH APP
describe("story #10", function() 
{
	fixtures("brjs.dashboard.app.testing.DashboardFixtureFactory");
	
	
	it("can display single app details with no bladesets", function() {
		given("dash.loaded = true");
			and("page.url => '#apps/launchme'");
		when("dash.service.responseReceived => '200 {}'");
		then("dash.model.appDetailScreen.appName = 'launchme'")
	});
	
	it("opens window at the correct url when invoking the launch app button", function() {
		given("test.continuesFrom = 'can display single app details with no bladesets'");
		when("dash.model.appDetailScreen.launchApp.invoked => true");
		then("dash.windowOpened = '/test/baseurl/launchme'");
	});
	
});


