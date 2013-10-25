br.test.GwtTestRunner.initialize();

// Story 11 - LAUNCH WORKBENCH
describe("story #11", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	
	it("can display app details with bladesets", function() {
		given("dash.loaded = true");
			and("page.url = '#apps/myapp'");
		when("dash.service.responseReceived => '200 {\"chart\":[\"curve\", \"line\"], \"grid\":[\"fxmajor\", \"fxminor\"]}'");
		then("dash.service.requestSent = 'GET /sdk/version'");
			and("dash.service.requestSent = 'GET /apps'");
			and("dash.service.requestSent = 'GET /apps/myapp'");
			and("dash.service.noMoreRequests = true");
	});
	
	// TODO: update the workbench tests once this functionality has been refactored
//	it("opens window at the correct url when invoking the lauch workbench button", function() {
//		given("test.continuesFrom = 'can display app details with bladesets'");
//		when("dash.model.appDetailScreen.bladesets[0].blades[0].viewInWorkbench.invoked => true");
//		then("dash.windowOpened = '/test/baseurl/myapp/chart-bladeset/blades/curve/workbench'");
//	});
	
});


