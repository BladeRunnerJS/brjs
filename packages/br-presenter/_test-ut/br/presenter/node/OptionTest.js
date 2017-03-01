(function() {
    var Option = require('br-presenter/node/Option');
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    OptionTest = TestCase("OptionTest");

    OptionTest.prototype.setUp = function()
    {
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();
    };

    OptionTest.prototype.tearDown = function()
    {
        Mock4JS.verifyAllMocks();
    };

    OptionTest.prototype.test_Construction = function()
    {
        var option = new Option("key","label");
        
        assertEquals("a1", "key", option.value.getValue());
        assertEquals("a2", "label", option.label.getValue());
        assertEquals("a3", "label", option.toString());
        assertEquals("a4", true, option.enabled.getValue());
    };
})();
