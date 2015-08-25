(function() {
    var Errors = require("br/Errors");
    var Alias = require("br/presenter/property/Alias");
    var WritableProperty = require("br/presenter/property/WritableProperty");
    var Formatter = require("br/presenter/formatter/Formatter");
    var Core = require("br/Core");
    AliasTest = TestCase("AliasTest");

    AliasTest.prototype.setUp = function()
    {
        // nothing
    };

    AliasTest.prototype.tearDown = function()
    {
        // nothing
    };

    AliasTest.prototype._getFormatter = function()
    {
        var fFormatter = function(){};
        Core.implement(fFormatter, Formatter);
        fFormatter.prototype.format = function(vValue, mAttributes)
        {
            return vValue.toUpperCase();
        }
        return new fFormatter();
    }

    AliasTest.prototype.test_canConstructAnAliasWithAPropertyInstance = function()
    {
        var oProperty = new WritableProperty("test");
        var oAlias = new Alias(oProperty);
        assertEquals("test", oAlias.getValue());
    };

    AliasTest.prototype.test_cannotConstructAnAliasWithANonProperty = function()
    {
        assertException("Non-property throws exception", function(){
            var oAlias = new Alias("test");
        }, Errors.INVALID_PARAMETERS);
    };

    AliasTest.prototype.test_changesInValueOfAliasedPropertyAreReflectedByTheAlias = function()
    {
        var oProperty = new WritableProperty("test1");
        var oAlias = new Alias(oProperty);
        assertEquals("test1", oAlias.getValue());

        oProperty.setValue("test2");
        assertEquals("test2", oAlias.getValue());
    };

    AliasTest.prototype.test_anAliasRespectsTheFormattersOnItsWrappedProperty = function()
    {
        var oProperty = new WritableProperty("test");
        oProperty.addFormatter(this._getFormatter(), {});
        var oAlias = new Alias(oProperty);

        assertEquals("TEST", oAlias.getFormattedValue());
    };
})();
