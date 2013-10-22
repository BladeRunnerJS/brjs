br.test.GwtTestRunner.initialize();

// Story 7 - IMPORT BLADES FROM ANOTHER APP
describe("story #7", function() 
{
	fixtures("caplinx.dashboard.app.testing.DashboardFixtureFactory");
	
	it("displays the import blades dialog", function() {
		given("test.continuesFrom = 'story #4::enables the Import Blades From App button when there are multiple applications'");
		when("dash.model.appDetailScreen.importBladesFromApp.invoked => true");
		then("dash.service.requestSent = 'GET /apps/fxtrader'")
			and("dash.model.dialog.visible = true");
			and("dash.model.dialog.type = 'importBladesFromAppDialog'");
			and("dash.model.dialog.viewNode.current.selectedApp.value = 'fxtrader'");
	});

	it("displays the bladesets and blades for the selected application in the dialog", function() {
		given("test.continuesFrom = 'displays the import blades dialog'");
		when("dash.service.responseReceived => '200 {\"ticket\":[\"market\", \"limit\"], \"grid\":[\"major\", \"minor\"]}'");
		then("dash.model.dialog.viewNode.current.bladesets.length = 2");
			and("dash.model.dialog.viewNode.current.bladesets[0].bladesetName = 'ticket'");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades.length = 2")
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].bladeName = 'market'");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[1].bladeName = 'limit'");
			and("dash.model.dialog.viewNode.current.bladesets[1].bladesetName = 'grid'");
			and("dash.model.dialog.viewNode.current.bladesets[1].blades.length = 2")
			and("dash.model.dialog.viewNode.current.bladesets[1].blades[0].bladeName = 'major'");
			and("dash.model.dialog.viewNode.current.bladesets[1].blades[1].bladeName = 'minor'");
			and("dash.model.dialog.viewNode.current.importBladesButton.enabled = false");
	});
	
//	TODO: Fix flaky test which does not even interact with the view
//	it("sets all the bladesets and blades as selected by default", function() {
//		given("test.continuesFrom = 'displays the bladesets and blades for the selected application in the dialog'");
//		then("dash.model.dialog.viewNode.current.bladesets[0].isSelected = true");
//			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].isSelected = true");
//			and("dash.model.dialog.viewNode.current.bladesets[0].blades[1].isSelected = true");
//			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected = true")
//			and("dash.model.dialog.viewNode.current.bladesets[1].blades[0].isSelected = true");
//			and("dash.model.dialog.viewNode.current.bladesets[1].blades[1].isSelected = true");
//	});
	
//	TODO: Comment back in the tests below once their dependency test above is reliable
//	it("de-selects all child blades if the parent bladeset toggle is not selected", function() {
//		given("test.continuesFrom = 'sets all the bladesets and blades as selected by default'");
//		when("dash.model.dialog.viewNode.current.bladesets[1].isSelected => false");
//		then("dash.model.dialog.viewNode.current.bladesets[1].blades[0].isSelected = false");
//			and("dash.model.dialog.viewNode.current.bladesets[1].blades[1].isSelected = false");
//	});
//	
//	it("only lets bladeset toggle affect child blades and not other bladeset blades", function() {
//		given("test.continuesFrom = 'de-selects all child blades if the parent bladeset toggle is not selected'")
//		then("dash.model.dialog.viewNode.current.bladesets[0].isSelected = true")
//			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].isSelected = true");
//			and("dash.model.dialog.viewNode.current.bladesets[0].blades[1].isSelected = true");
//	});
//	
//	it("should issue an import-blades request POST for only the selected blades when the Import button is invoked", function() {
//		given("test.continuesFrom = 'de-selects all child blades if the parent bladeset toggle is not selected'");
//		when("dash.model.dialog.viewNode.current.importBlades.invoked => true");
//		then("dash.service.requestSent = 'POST /apps/mynewtrader {command:import-blades,app:fxtrader,bladesets:{ticket:[market,limit]}}'");
//	});

	it("sets a blade and its parent bladeset as selected when the single blade is clicked", function() {
		given("test.continuesFrom = 'displays the bladesets and blades for the selected application in the dialog'");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[1].blades[1].isSelected => true");
		then("dash.model.dialog.viewNode.current.bladesets[0].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[1].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[1].isIndeterminate = true")
			and("dash.model.dialog.viewNode.current.bladesets[1].blades[0].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[1].blades[1].isSelected = true");
	});

// TODO: fix this flaky test
//	it("imports a single blade and its parent when the single blade is clicked from a set of 2 childblades", function() {
//		given("test.continuesFrom = 'sets a blade and its parent bladeset as selected when the single blade is clicked'");
//		when("dash.model.dialog.viewNode.current.importBlades.invoked => true");
//		then("dash.service.requestSent = 'POST /apps/mynewtrader {command:import-blades,app:fxtrader,bladesets:{grid:{newBladesetName:grid,blades:[minor]}}}'");
//	});
	
	it("allows changing the name of the new bladeset", function() {
		given("test.continuesFrom = 'displays the bladesets and blades for the selected application in the dialog'");
			and("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.value = 'ticket'");
			and("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.hasError = false");
		when("dash.model.dialog.viewNode.current.bladesets[1].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[0].isSelected => true");
			and("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.value => 'newticket'");
			and("dash.model.dialog.viewNode.current.importBlades.invoked => true");
		then("dash.service.requestSent = 'POST /apps/mynewtrader {command:import-blades,app:fxtrader,bladesets:{ticket:{newBladesetName:newticket,blades:[market,limit]}}}'");
	});

	it("newBladesetName field has validation and prevents importing if validation fails", function() {
		given("test.continuesFrom = 'allows changing the name of the new bladeset'");
		when("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.value => 'newTicket'");
		then("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.hasError = true");
			and("dash.model.dialog.viewNode.current.importBladesButton.enabled = false");
	});
	
	it("newBladesetName validation is ignored if the bladeset is not longer selected", function() {
		given("test.continuesFrom = 'newBladesetName field has validation and prevents importing if validation fails'");
			and("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.hasError = true");
			and("dash.model.dialog.viewNode.current.bladesets[1].newBladesetName.hasError = false");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected => true");
		then("dash.model.dialog.viewNode.current.importBladesButton.enabled = true");
	});
	
	it("import button is disabled if an invalid bladeset is re-selected", function() {
		given("test.continuesFrom = 'newBladesetName validation is ignored if the bladeset is not longer selected'");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => true");
		then("dash.model.dialog.viewNode.current.importBladesButton.enabled = false");
	});
	
	it("newBladesetName validation is used if a single blade is selected", function() {
		given("test.continuesFrom = 'displays the bladesets and blades for the selected application in the dialog'");
			and("dash.model.dialog.viewNode.current.bladesets[0].isSelected = false");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected = true");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].isSelected => true");
			and("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.value => 'a b c'");
		then("dash.model.dialog.viewNode.current.bladesets[0].newBladesetName.hasError = true");
			and("dash.model.dialog.viewNode.current.importBladesButton.enabled = false");
	});
	
	it("newBladesetName field is only visible if bladeset is selected", function() {
		given("test.continuesFrom = 'story #7::displays the bladesets and blades for the selected application in the dialog'");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => true");
			and("dash.model.dialog.viewNode.current.bladesets[1].isSelected => false");
		then("dash.model.dialog.viewNode.current.bladesets[0].displayNewBladesetField = true");
			and("dash.model.dialog.viewNode.current.bladesets[1].displayNewBladesetField = false");
	});

	it("newBladesetName field is visible if a single blade is selected", function() {
		given("test.continuesFrom = 'story #7::displays the bladesets and blades for the selected application in the dialog'");
		when("dash.model.dialog.viewNode.current.bladesets[0].isSelected => false");
			and("dash.model.dialog.viewNode.current.bladesets[0].blades[0].isSelected => true");
		then("dash.model.dialog.viewNode.current.bladesets[0].displayNewBladesetField = true");
	});

});


