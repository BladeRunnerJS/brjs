br.test.GwtTestRunner.initialize();

describe("behaviour of Property objects in a Presentation Model", function() {

	fixtures("PresenterFixtureFactory");

	it("has correct initial values set", function()
	{
		given("demo.viewOpened = true");
		then("demo.view.(span#prop-plain).text = 'a'");
			and("demo.view.(span#prop-formatted).text = 'A'");
	});

	it("changes the plain view to match the plain view model data when property value changes", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.plainProperty => 'b'");
		then("demo.view.(span#prop-plain).text = 'b'");
	});

	it("changes the formatted view to match the formatted view model data when property value changes", function()
	{
		given("demo.viewOpened = true");
		when("demo.model.formattedProperty => 'b'");
		then("demo.view.(span#prop-formatted).text = 'B'");
	});
});
