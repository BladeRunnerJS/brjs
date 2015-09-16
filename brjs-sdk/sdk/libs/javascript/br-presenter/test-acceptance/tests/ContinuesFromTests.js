(function() {
    var GwtTestRunner = require("br/test/GwtTestRunner");
    GwtTestRunner.initialize();

	// test for https://github.com/BladeRunnerJS/brjs/issues/1523 to make sure IE8 doesnt get a stackoverflow if there are multiple 'continuesFrom'
    describe("multiple continue from using a PresentaionModel with an EditableProperty dont cause a stackoverflow", function() {

        fixtures( require("br/presenter/PresenterFixtureFactory") );

        it("new test", function()
        {
            given("demo.viewOpened = true");
            then("demo.model.plainProperty = 'a'");
        });

        it("new test 2", function()
        {
            given("test.continuesFrom = 'new test'");
            then("demo.model.plainProperty = 'a'");
        });

        it("new test 3", function()
        {
            given("test.continuesFrom = 'new test 2'");
            then("demo.model.plainProperty = 'a'");
        });

        it("new test 4", function()
        {
            given("test.continuesFrom = 'new test 3'");
            then("demo.model.plainProperty = 'a'");
        });

        it("new test 5", function()
        {
            given("test.continuesFrom = 'new test 4'");
            then("demo.model.plainProperty = 'a'");
        });        

        it("new test 6", function()
        {
            given("test.continuesFrom = 'new test 5'");
            then("demo.model.plainProperty = 'a'");
        });   

        it("new test 7", function()
        {
            given("test.continuesFrom = 'new test 6'");
            then("demo.model.plainProperty = 'a'");
        });   

        it("new test 8", function()
        {
            given("test.continuesFrom = 'new test 7'");
            then("demo.model.plainProperty = 'a'");
        });              

        it("new test 9", function()
        {
            given("test.continuesFrom = 'new test 8'");
            then("demo.model.plainProperty = 'a'");
        });  

        it("new test 10", function()
        {
            given("test.continuesFrom = 'new test 9'");
            then("demo.model.plainProperty = 'a'");
        });

		// fresh test X exists so tests continue to run after the stackoverflow alert
        it("fresh test", function()
        {
            given("demo.viewOpened = true");
            then("demo.model.plainProperty = 'a'");
        });  

        it("fresh test 2", function()
        {
            given("demo.viewOpened = true");
            then("demo.model.plainProperty = 'a'");
        });  

        it("fresh test 3", function()
        {
            given("demo.viewOpened = true");
            then("demo.model.plainProperty = 'a'");
        });  
    });
})();