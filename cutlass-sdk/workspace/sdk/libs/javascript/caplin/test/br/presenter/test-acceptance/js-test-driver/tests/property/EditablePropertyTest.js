br.test.GwtTestRunner.initialize();

describe("behaviour of EditableProperty objects in a Presentation Model", function() {

	fixtures("PresenterFixtureFactory");

	it("has correct initial values", function()
	{
		given("demo.viewOpened = true");
		then("demo.view.(input#edProp-plain).value = 'a'");
			and("demo.view.(input#edProp-formatted).value = 'A'");
			and("demo.view.(input#edProp-parsed).value = 'a'");
			and("demo.view.(input#edProp-parsed-formatted).value = 'A'");
	});

	it("changes the view to match the view model", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.plainEditableProperty => 'b'");
		then("demo.view.(input#edProp-plain).value = 'b'");
	});

	it("changes the view to match the formatted view model", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.formattedEditableProperty => 'b'");
		then("demo.view.(input#edProp-formatted).value = 'B'");
	});

	it("changes the view to match the parsed view model", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.parsedEditableProperty => 'b'");
		then("demo.view.(input#edProp-parsed).value = 'c'");
	});

	it("changes the view to match the parsed formatted view model", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.parsedFormattedEditableProperty => 'b'");
		then("demo.view.(input#edProp-parsed-formatted).value = 'C'");
	});

	it("formats value entered via input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-formatted).value => 'b'");
		then("demo.view.(input#edProp-formatted).value = 'B'");
	});

	it("parses value entered via input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-parsed).value => 'b'");
		then("demo.view.(input#edProp-parsed).value = 'c'");
	});

	it("parses and formats value entered via input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-parsed-formatted).value => 'b'");
		then("demo.view.(input#edProp-parsed-formatted).value = 'C'");
	});


	/*
	 * Double entry tests
	 */

	it("formats a value that is entered twice via an input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-formatted).value => 'b'");
			and("demo.view.(input#edProp-formatted).value => 'b'");
		then("demo.view.(input#edProp-formatted).value = 'B'");
	});

	it("parses a value that is entered twice via an input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-parsed).value => 'b'");
			and("demo.view.(input#edProp-parsed).value => 'b'");
		then("demo.view.(input#edProp-parsed).value = 'c'");
	});

	it("parses and formats a value that is entered twice via an input box", function()
	{
		given("demo.viewOpened = true");
		when("demo.view.(input#edProp-parsed-formatted).value => 'b'");
			and("demo.view.(input#edProp-parsed-formatted).value => 'b'");
		then("demo.view.(input#edProp-parsed-formatted).value = 'C'");
	});
});
