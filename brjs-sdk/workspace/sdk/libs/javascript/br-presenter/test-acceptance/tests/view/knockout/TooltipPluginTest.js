br.test.GwtTestRunner.initialize();

describe("Tooltip Plugin Tests",function() {
	fixtures("TooltipPluginFixtureFactory");
	
	beforeEach(function() {
		//br.test.Utils.saveGlobalElements(br.presenter.view.knockout.TooltipPlugin.element[0]);
		//br.test.Utils.addSavedElements();
		
		
		document.body.appendChild(br.presenter.view.knockout.TooltipPlugin.element[0]);
		
	})
	
	it("test the tooltip presentation model works",function() {
		given("form.viewOpened = true");	
			and("form.model.theField.value = '1234'");
		when("form.model.theField.value => 'Hello World'");
		then("form.view.(#theField).value = 'Hello World'");
		
			and("test.page.(.ui-tooltip).isVisible = false");
		
	});
	
	it("test the tooltip appears when field has an error",function() {
		given("form.viewOpened = true");
		when("form.model.theField.hasError => true");
			and("form.model.theField.failureMessage => 'Uh Oh! Failure!!'");
			and("form.view.(#theField).focusIn => true");
		then("test.page.(.ui-tooltip).isVisible = true");
			and("test.page.(.ui-tooltip-content).text = 'Uh Oh! Failure!!'");
	
	});
	
	it("test the tooltip disappears when the field no longer has an error",function() {
		given("test.continuesFrom = 'test the tooltip appears when field has an error'");
		when("form.model.theField.hasError => false");
			and("form.view.(#theField).focusOut => true");
		then("test.page.(.ui-tooltip).isVisible = false");
	});
	
});
