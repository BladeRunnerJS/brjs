br.test.GwtTestRunner.initialize();

describe("behaviour of an Alias objects in a Presentation Model", function() {

	fixtures("PresenterFixtureFactory");

	it("has correct initial values set", function()
	{
		given("demo.viewOpened = true");
		then("demo.view.(#alias-plain).text = 'a'");
			and("demo.view.(#alias-formatted).text = 'A'");
	});

	it("changes the plain view to match the aliased plain view-model property", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.plainProperty => 'b'");
		then("demo.view.(#alias-plain).text = 'b'");
	});

	it("changes the formatted view to match the aliased formatted view-model data", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.formattedProperty => 'b'");
		then("demo.view.(#alias-formatted).text = 'B'");
	});
});
