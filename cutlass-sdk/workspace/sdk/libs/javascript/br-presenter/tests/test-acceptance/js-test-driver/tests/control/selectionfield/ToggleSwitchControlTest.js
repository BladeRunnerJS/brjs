br.test.GwtTestRunner.initialize();

describe("View to model interactions for toggle switch", function() {
	fixtures("PresenterFixtureFactory");
	
	it("starts enabled and visible by default", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#toggleSwitch1).doesNotHaveClass = 'disabled'");
			and("demo.view.(#toggleSwitch1).isVisible = true");
	});
	
	it("starts disabled and invisible if configured to do so", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#toggleSwitch3).hasClass = 'disabled'");
			and("demo.view.(#toggleSwitch3).isVisible = false");
	});
	
	it("has A selected when it is the default value", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
		and("demo.view.(#toggleSwitch1 .choiceA).text = 'a'");
	});
	
	it("has B selected when it is the default value", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#toggleSwitch2).hasClass = 'choiceBSelected'");
			and("demo.view.(#toggleSwitch1 .choiceB).text = 'b'");
	});
	
	it("becomes disabled when requested to do so", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.enabled => false");
		then("demo.view.(#toggleSwitch1).hasClass = 'disabled'");
			and("demo.view.(#toggleSwitch1).isVisible = true");
	});
	
	it("becomes invisible when requested to do so", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.visible => false");
		then("demo.view.(#toggleSwitch1).isVisible = false");
			and("demo.view.(#toggleSwitch1).doesNotHaveClass = 'disabled'");
	});
	
	it("becomes disabled and invisible when requested to do so", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.enabled => false");
			and("demo.model.selectionField.visible => false");
		then("demo.view.(#toggleSwitch1).hasClass = 'disabled'");
			and("demo.view.(#toggleSwitch1).isVisible = false");
	});
	
	it("displays an updated value when the selected value changes", function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.value => 'b'");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceBSelected'");
			and("demo.view.(#toggleSwitch1).doesNotHaveClass = 'choiceASelected'");
	});
	
	it("allows the value to be updated multiple times", function() {
		given("test.continuesFrom = 'displays an updated value when the selected value changes'");
		when("demo.model.selectionField.value => 'a'");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
			and("demo.view.(#toggleSwitch1).doesNotHaveClass = 'choiceBSelected'");
	});
	
	it("automatically selects a new option when the updated options no longer include the currently selected item", function() {
		given("demo.viewOpened = true");
			and("demo.model.selectionField.value = 'b'");
		when("demo.model.selectionField.options => ['x', 'y']");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
			and("demo.view.(#toggleSwitch1).doesNotHaveClass = 'choiceBSelected'");
			and("demo.view.(#toggleSwitch1 .choiceA).text = 'x'");
			and("demo.view.(#toggleSwitch1 .choiceB).text = 'y'");
	});
	
	it("allows options to be updated multiple times", function() {
		given("test.continuesFrom = 'automatically selects a new option when the updated options no longer include the currently selected item'");
			and("demo.model.selectionField.value = 'y'");
		when("demo.model.selectionField.options => ['one', 'two']");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
			and("demo.view.(#toggleSwitch1).doesNotHaveClass = 'choiceBSelected'");
			and("demo.view.(#toggleSwitch1 .choiceA).text = 'one'");
			and("demo.view.(#toggleSwitch1 .choiceB).text = 'two'");
			
	});
	
	// view interaction tests: these need to be performed because this is a caplin control, and is not tested elsewhere
	
	it("selects b when a is currently selected and b is clicked", function() {
		given("demo.viewOpened = true");
		when("demo.view.(#toggleSwitch1 .choiceB).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceBSelected'");
	});
	
	it("does not affect selection if you click on the same side as is currently selected (left side)", function() {
		given("demo.viewOpened = true");
		when("demo.view.(#toggleSwitch1 .choiceA).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
	});

	it("selects a when b is currently selected and a is clicked", function() {
		given("test.continuesFrom = 'selects b when a is currently selected and b is clicked'");
		when("demo.view.(#toggleSwitch1 .choiceA).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
	});
	
	it("does not affect selection if you click on the same side as is currently selected (right side)", function() {
		given("test.continuesFrom = 'selects b when a is currently selected and b is clicked'");
		when("demo.view.(#toggleSwitch1 .choiceB).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceBSelected'");
	});
	
	it("does nothing if clicked when disabled (currently on left side)", function() {
		given("demo.viewOpened = true");
			and("demo.model.selectionField.enabled = false");
		when("demo.view.(#toggleSwitch1 .choiceB).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceASelected'");
			and("demo.model.selectionField.value = 'a'");
	});
	
	it("does nothing if clicked when disabled (currently on right side)", function() {
		given("test.continuesFrom = 'selects b when a is currently selected and b is clicked'");
			and("demo.model.selectionField.enabled => false");
		when("demo.view.(#toggleSwitch1 .choiceA).clicked => true");
		then("demo.view.(#toggleSwitch1).hasClass = 'choiceBSelected'");
			and("demo.model.selectionField.value = 'b'");
	});
});
