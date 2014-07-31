br.test.GwtTestRunner.initialize();

describe("View to model interactions for ExtJsComboBoxControlAdaptor", function() {
	fixtures("PresenterFixtureFactory");

	it("starts enabled and visible by default", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#dropDownComboxBox input).enabled = true");
			and("demo.view.(#dropDownComboxBox input).isVisible = true");
	});

	it("starts disabled and invisible if configured to do so", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#dropDownComboxBox3).enabled = false");
			and("demo.view.(#dropDownComboxBox3).isVisible = false");
	});

	it("has A selected when it is the default value", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#dropDownComboxBox input).value = 'a'");
	});

	it("has B selected when it is the default value", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#dropDownComboxBox2 input).value = 'b'");
	});

	it("becomes disabled and invisible when requested to do so", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.enabled => false");
			and("demo.model.selectionField.visible => false");
		then("demo.view.(#dropDownComboxBox input).enabled = false");
			and("demo.view.(#dropDownComboxBox input).isVisible = false");
	});

	it("opens combo list options when dropdown arrow is clicked", function(){
		given("demo.viewOpened = true");
		when("demo.view.(#dropDownSelectBox2 img).clicked => true");
		then("test.page.(.x-combo-list).isVisible = true");
	});

	it("displays an updated value when the selected value changes", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.value => 'b'");
		then("demo.view.(#dropDownComboxBox input).value = 'b'");
	});

	it("allows the options to be changed to completely new values", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.options => ['x', 'y']");
		then("demo.view.(#dropDownComboxBox input).value = 'x'");
	});

	it("displays updated label when selected value changes in model", function() {
		given("demo.viewOpened = true");
		when("demo.model.labelValueSelectionField.value => 'b'");
		then("demo.view.(#labelValueSelectBox input).value = 'B-Label'");
	});

});
