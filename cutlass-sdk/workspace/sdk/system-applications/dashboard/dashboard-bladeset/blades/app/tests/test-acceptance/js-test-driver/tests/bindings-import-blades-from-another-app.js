br.test.GwtTestRunner.initialize();

describe("Import Blades Dialog", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	it("newBladesetName field is correctly bound", function() {
		given("test.continuesFrom = 'story #7::displays the bladesets and blades for the selected application in the dialog'");
			and("dash.model.dialog.viewNode.current.bladesets[0].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[0].displayNewBladesetField = false");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected = true");		
			and("dash.model.dialog.viewNode.current.bladesets[1].displayNewBladesetField = true");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => true");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected => false");
		then("dash.view.(#modalDialog .importBladesets .importBladeset:eq(0) .new-bladeset-name .field).isVisible = true");
			and("dash.view.(#modalDialog .importBladesets .importBladeset:eq(1) .new-bladeset-name .field).isVisible = false");
	});
	
});
