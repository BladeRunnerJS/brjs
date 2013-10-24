br.test.GwtTestRunner.initialize();

describe("View to model interactions for Field", function() {

	fixtures("PresenterFixtureFactory");
	
	it("contains default value when loaded", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#fieldText).value = 'a'");
			and("demo.view.(input[name='aField']).count = 1");
	});
	
	it("shows updated value in view when presentation model is changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.field.value => 'b'");
		then("demo.view.(#fieldText).value = 'b'");
	});
	
	it("updates the presentationmodel when the view is changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.view.(#fieldText).value => 'abc'");
		then("demo.model.field.value = 'abc'");
	});
	
	it("gains the display nones when view hidden", function() {
		given("demo.viewOpened = true");
		when("demo.model.field.visible => false");
		then("demo.view.(#fieldText).isVisible = false");
	});
	
	it("gains the disabled CSS class when disabled", function() {
		given("demo.viewOpened = true");
		when("demo.model.field.enabled => false");
		then("demo.view.(#fieldText).enabled = false");
	});
});
