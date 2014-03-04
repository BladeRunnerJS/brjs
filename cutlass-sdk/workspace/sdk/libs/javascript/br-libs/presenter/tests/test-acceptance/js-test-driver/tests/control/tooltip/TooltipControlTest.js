br.test.GwtTestRunner.initialize();

describe("Floating Tooltip Control tests", function() {
	fixtures("TooltipControlFixtureFactory");
	
	/* input1 validation incorrect: any value != 1 (tooltip error msg=ERR)*/
	/* input2 validation incorrect: any value != 0 (tooltip error msg=ANOTHERERR)*/
	
	it("Does not display tooltip when form is opened insitially", function() {
		given("form.viewOpened = true");
		then("form.view.(.input1).doesNotHaveClass = 'has-tooltip'");
		then("form.view.(.input2).doesNotHaveClass = 'has-tooltip'");
	});

	
	it("Shows tooltip on first input when validation incorrect", function() {
		given("test.continuesFrom = 'Does not display tooltip when form is opened insitially'");
		when("form.view.(.input1).value => '0'");
		then("form.view.(.input1).hasClass = 'has-tooltip'");
			and("form.view.(.tooltip-content).isVisible = true");
			and("form.view.(.tooltip-content).text = 'ERR'");
	});
	
	it("Shows tooltip on last input on error (input 2 is last errror)", function() {
		given("test.continuesFrom = 'Shows tooltip on first input when validation incorrect'");
		when("form.view.(.input2).value => '1'");
		then("form.view.(.input1).doesNotHaveClass = 'has-tooltip'");
			and("form.view.(.input2).hasClass = 'has-tooltip'");
			and("form.view.(.tooltip-content).isVisible = true");
			and("form.view.(.tooltip-content).text = 'ANOTHERERR'");
	});

	it("Shows tooltip on last input on error (input 1 is last errror)", function() {
		given("test.continuesFrom = 'Shows tooltip on last input on error (input 2 is last errror)'");
		when("form.view.(.input1).value => '0'");
		then("form.view.(.input2).doesNotHaveClass = 'has-tooltip'");
		and("form.view.(.input1).hasClass = 'has-tooltip'");
		and("form.view.(.tooltip-content).isVisible = true");
		and("form.view.(.tooltip-content).text = 'ERR'");
	});
	
	it("Shows tooltip on first input on error if second's input validation is correct", function() {
		given("test.continuesFrom = 'Shows tooltip on last input on error (input 2 is last errror)'");
		when("form.view.(.input2).value => '0'");
		then("form.view.(.input2).doesNotHaveClass = 'has-tooltip'");
			and("form.view.(.input1).hasClass = 'has-tooltip'");
			and("form.view.(.tooltip-content).isVisible = true");
			and("form.view.(.tooltip-content).text = 'ERR'");
	});
	
	it("Removes tooltip when any field is on error", function() {
		given("test.continuesFrom = 'Shows tooltip on first input on error if second's input validation is correct'");
		when("form.view.(.input1).value => '1'");
		then("form.view.(.input1).doesNotHaveClass = 'has-tooltip'");
			and("form.view.(.input2).doesNotHaveClass = 'has-tooltip'");
	});
	
});
