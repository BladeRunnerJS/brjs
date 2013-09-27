caplin.testing.GwtTestRunner.initialize();

describe("splash screen tests", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	it("splash screen displayed by default", function() {
		given("dash.loaded = true");
		and("storage.dashboard_permanentlyHideSplashScreen = undefined");
		then("dash.model.appsScreen.splashScreen.isVisible = true");
	});

	it("sets local storage value to true if hide permanently is checked", function() {
		given("test.continuesFrom = 'splash screen displayed by default'");
		when("dash.model.appsScreen.splashScreen.permanentlyHideSplashScreen => true");
		then("storage.dashboard_permanentlyHideSplashScreen = true");
	});

	it("sets local storage value to false if hide permanently is unchecked", function() {
		given("test.continuesFrom = 'sets local storage value to true if hide permanently is checked'");
		when("dash.model.appsScreen.splashScreen.permanentlyHideSplashScreen => false");
		then("storage.dashboard_permanentlyHideSplashScreen = false");
	});

	it("splash screen is not displayed if local storage set", function() {
		given("test.continuesFrom = 'sets local storage value to true if hide permanently is checked'");
			and("storage.dashboard_permanentlyHideSplashScreen = true");
			and("dash.loaded = true");
		then("dash.model.appsScreen.splashScreen.isVisible = false");
	});
	
	it("splash screen is displayed if local storage set to false", function() {
		given("test.continuesFrom = 'sets local storage value to true if hide permanently is checked'");
			and("storage.dashboard_permanentlyHideSplashScreen = false");
			and("dash.loaded = true");
		then("dash.model.appsScreen.splashScreen.isVisible = true");
	});

	/* duplicate tests of the two above but using strings not booleans - some browsers return strings from local storage and not boolean */
	it("splash screen is not displayed if local storage set - using strings", function() {
		given("test.continuesFrom = 'sets local storage value to true if hide permanently is checked'");
			and("storage.dashboard_permanentlyHideSplashScreen = 'true'");
			and("dash.loaded = true");
		then("dash.model.appsScreen.splashScreen.isVisible = false");
	});
	it("splash screen is displayed if local storage set to false  - using strings", function() {
		given("test.continuesFrom = 'sets local storage value to true if hide permanently is checked'");
			and("storage.dashboard_permanentlyHideSplashScreen = 'false'");
			and("dash.loaded = true");
		then("dash.model.appsScreen.splashScreen.isVisible = true");
	});

	it("splash screen is displayed if local storage not available", function() {
		given("dash.disableLocalStorage = true");
			and("dash.loaded = true");
		then("dash.model.appsScreen.splashScreen.isVisible = true");
	});
	
});
