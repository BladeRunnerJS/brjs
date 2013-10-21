br.test.GwtTestRunner.initialize();

// Story 3 - IMPORT MOTIF ZIP
describe("story #3", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	
	it("displays the import motif dialog when the import motif button is clicked", function() {
		given("test.continuesFrom = 'story #1::should display all available apps when the app info arrives'");
		when("dash.model.appsScreen.importMotifFromZip.invoked => true");
		then("dash.model.appsScreen.visible = true");
			and("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'importMotifDialog'");
			and("dash.model.dialog.viewNode.current.appZip.enabled = true");
			and("dash.model.dialog.viewNode.current.appName.value = ''");
			and("dash.model.dialog.viewNode.current.appNamespace.value = ''");
			and("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("disables the import-motif dialog button when appName is not specified", function() {
		given("test.continuesFrom = 'displays the import motif dialog when the import motif button is clicked'");
		when("dash.model.dialog.viewNode.current.appName.value => ''");
			and("dash.model.dialog.viewNode.current.appNamespace.value => 'novox'");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("disables the import-motif dialog button when appNamespace is not specified", function() {
		given("test.continuesFrom = 'displays the import motif dialog when the import motif button is clicked'");
		when("dash.model.dialog.viewNode.current.appName.value => 'novotrader'");
			and("dash.model.dialog.viewNode.current.appNamespace.value => ''");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = false");
	});
	
	it("enables the import-motif dialog button when valid appName and appNamespace are specified", function() {
		given("test.continuesFrom = 'displays the import motif dialog when the import motif button is clicked'");
		when("dash.model.dialog.viewNode.current.appName.value => 'novotrader'");
			and("dash.model.dialog.viewNode.current.appNamespace.value => 'novox'");
		then("dash.model.dialog.viewNode.current.createAppButton.enabled = true");
	});
	
	it("imports the motif zip", function() {
		// TODO
	});
});
