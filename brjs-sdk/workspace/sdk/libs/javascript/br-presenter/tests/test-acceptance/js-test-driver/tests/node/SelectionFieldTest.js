br.test.GwtTestRunner.initialize();

describe("View to model interactions for SelectBox", function() {

	fixtures("PresenterFixtureFactory");
	
	it("contains default value when loaded", function() {
		given("demo.viewOpened = true");
		then("demo.view.(#selectBox).value = 'a'");
			and("demo.view.(select[name='aSelectionField']).count = 1");
	});
	
	it("shows updated options in view when presentation model options are changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.selectionField.options => ['b','c']");
		then("demo.view.(#selectBox).options = ['b','c']");
	});
	
	it("chooses default option when presentation model options are changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.selectionField.options => ['b','c']");
		then("demo.view.(#selectBox).value = 'b'");
	});
	
	it("allows completely new options to be set", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.selectionField.options => ['x','y']");
		then("demo.view.(#selectBox).options = ['x','y']");
			and("demo.view.(#selectBox).value = 'x'");
	});
	
	it("allows completely new options containing spaces to be set", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.selectionField.options => ['WithoutSpaces','With Spaces']");
		then("demo.view.(#selectBox).options = ['WithoutSpaces','With Spaces']");
			and("demo.view.(#selectBox).value = 'WithoutSpaces'");
	});
	
	it("shows updated value in view when presentation model is changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.model.selectionField.value => 'b'");
		then("demo.view.(#selectBox).value = 'b'");
	});
	
	it("sets updated value in PM when choice in view is changed", function() {
		given("test.continuesFrom = 'contains default value when loaded'");
		when("demo.view.(#selectBox option:last).selected => true");
		then("demo.model.selectionField.value = 'b'");
	});

	it('gains the display "none" when view hidden', function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.visible => false");
		then("demo.view.(#selectBox).isVisible = false");
	});
	
	it('gains the "disabled" CSS class when disabled', function() {
		given("demo.viewOpened = true");
		when("demo.model.selectionField.enabled => false");
		then("demo.view.(#selectBox).enabled = false");
	});
});
