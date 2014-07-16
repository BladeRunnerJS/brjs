br.test.GwtTestRunner.initialize();

describe("Dashboard App", function() {
	fixtures("brjs.dashboard.app.testing.DashboardFixtureFactory");
	
	it("displays the screens that are visible", function() {
		given("dash.loaded = true");
			and("dash.model.appsScreen.visible = true");
			and("dash.model.appDetailScreen.visible = true");
			and("dash.model.releaseNoteScreen.visible = true");
		then("dash.view.(#appsScreen).isVisible = true");
			and("dash.view.(#appDetailScreen).isVisible = true");
			and("dash.view.(#releaseNoteScreen).isVisible = true");
	});
	
	it("hides the screens that are not visible", function() {
		given("dash.loaded = true");
			and("dash.model.appsScreen.visible = false");
			and("dash.model.appDetailScreen.visible = false");
			and("dash.model.releaseNoteScreen.visible = false");
		then("dash.view.(#appsScreen).isVisible = false");
			and("dash.view.(#appDetailScreen).isVisible = false");
			and("dash.view.(#releaseNoteScreen).isVisible = false");
	});
});
