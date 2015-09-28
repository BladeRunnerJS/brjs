(function() {
    var Property = require("br/presenter/property/Property");
    var WritableProperty = require("br/presenter/property/WritableProperty");
    var Button = require("br/presenter/node/Button");
    ButtonTest = TestCase("ButtonTest");

    ButtonTest.prototype.test_canConstructAButtonWithAStringAsLabel = function()
    {
        var oButton = new Button("button");
        assertEquals("button", oButton.label.getValue());
    };

    ButtonTest.prototype.test_canConstructAButtonWithAPropertyAsLabel = function()
    {
        var oLabel = new WritableProperty("button");
        var oButton = new Button(oLabel);
        assertEquals("button", oButton.label.getValue());
    };

    ButtonTest.prototype.test_canConstructAButtonWithAReadOnlyPropertyAsLabel = function()
    {
        var oLabel = new Property("button");
        var oButton = new Button(oLabel);
        assertEquals("button", oButton.label.getValue());
    };


    ButtonTest.prototype.test_canConstructAButtonWithAReadOnlyPropertyAsLabel = function()
    {
        var oLabel = new Property("button");
        var oButton = new Button(oLabel);
        assertEquals("button", oButton.label.getValue());
    };
})();
