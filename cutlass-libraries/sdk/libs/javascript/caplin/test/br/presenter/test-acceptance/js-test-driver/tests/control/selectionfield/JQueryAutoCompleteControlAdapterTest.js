br.test.GwtTestRunner.initialize();

describe("View to model interactions for JQueryAutoCompleteControlAdapter", function() {
	fixtures("PresenterFixtureFactory");
	
	it("starts enabled and visible by default", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#jqueryAutoCompleteBox).enabled = true");
			and("demo.view.(#jqueryAutoCompleteBox).isVisible = true");
	});
	
	it("has the correct initial value", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#jqueryAutoCompleteBox).value = 'BB'");
	});
	
	it("correctly auto completes a valid input option", function() {
		given("demo.viewOpened = true");
		when("demo.model.jquerySelectionField.value => ''");
			and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'A'");
		then("demo.view.(#autocomplete-container li:eq(0)).text = 'AA'");
	});
	
	it("shows no options for invalid text", function() {
		given("demo.viewOpened = true");
		when("demo.model.jquerySelectionField.value => ''");
			and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'D'");
		then("demo.view.(#autocomplete-container li).count = '0'");
	});
	
	it("allows clicking on option to set the value", function() {
		given("demo.viewOpened = true");
		when("demo.model.jquerySelectionField.value => ''");
			and("demo.view.(#jqueryAutoCompleteBox).typedValue => 'A'");
			and("demo.view.(#autocomplete-container li:eq(0) a).clicked => true");
		then("demo.model.jquerySelectionField.value = 'AA'");
			and("demo.view.(#jqueryAutoCompleteBox).value = 'AA'");
	});
	
	it("does not display any options if minCharAmount is set to 2", function() {
		given("demo.viewOpened = true");
		when("demo.model.jquerySelectionField.value => ''");
		and("demo.view.(#jqueryAutoCompleteBox2).typedValue => 'A'");
		then("demo.view.(#autocomplete-container2 li).count = '0'");
	});
	
	it("does display options if minCharAmount is set to 2 and typed text is at least 2 chars long", function() {
		given("demo.viewOpened = true");
		when("demo.model.jquerySelectionField.value => ''");
		and("demo.view.(#jqueryAutoCompleteBox2).typedValue => 'AA'");
		then("demo.view.(#autocomplete-container2 li:eq(0)).text = 'AA'");
	});
});
