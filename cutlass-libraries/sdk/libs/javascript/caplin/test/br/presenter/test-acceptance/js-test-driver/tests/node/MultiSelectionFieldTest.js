br.test.GwtTestRunner.initialize();

describe("View to model interactions for MultiSelectBox", function() {

	fixtures("PresenterFixtureFactory");
	
	it("contains default value when loaded", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#multiSelectBox).value = ['a','c']");
			and("demo.view.(select[name='aMultiSelectionField']).count = 1");
			and("demo.view.(#multiSelectBoxMapped).value = ['a','c']");
			and("demo.view.(#multiSelectBoxMapped option:nth-child(1)).text = 'aLabel'");
			and("demo.view.(#multiSelectBoxMapped option:nth-child(3)).text = 'cLabel'");
	});

	it("displays the default labels when loaded with a map", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#multiSelectBoxMapped).options = ['aLabel','bLabel','cLabel', 'dLabel']");
	});

	it("shows updated value in view when presentation value is changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.multiSelectBox.value => ['b']");
		then("demo.view.(#multiSelectBox).value = ['b']");
	});

	it("shows updated options in view when presentation model is completely changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.multiSelectBox.options => ['e','d']");
		then("demo.view.(#multiSelectBox).options = ['e','d']");
	});

	it("chooses value of new options when presentation model options are completely changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.multiSelectBox.options => ['e','d']");
		then("demo.view.(#multiSelectBox).value = undefined");
	});
	
	it("sets updated value in PM when choice in view is changed", function() {
		given("demo.viewOpened = true");
		when("demo.view.(#multiSelectBox option:last).selected => true");
		then("demo.model.multiSelectBox.value = ['a','c','d']");
	});

	it("removes value from values Array when you unselect it", function() {
		given("demo.viewOpened = true");
			and("demo.model.multiSelectBox.value = ['a','c','d']");
		when("demo.view.(#multiSelectBox option:contains('c')).selected => false");
		then("demo.model.multiSelectBox.value = ['a','d']");
	});
	
	it("gains the display nones when view hidden", function() {
		given("demo.viewOpened = true");
		when("demo.model.multiSelectBox.visible => false");
		then("demo.view.(#multiSelectBox).isVisible = false");
	});
	
	it("gains the disabled CSS class when disabled", function() {
		given("demo.viewOpened = true");
		when("demo.model.multiSelectBox.enabled => false");
		then("demo.view.(#multiSelectBox).enabled = false");
	});
});
