(function() {
    var EditableProperty = require("br/presenter/property/EditableProperty");
    var WritableProperty = require("br/presenter/property/WritableProperty");
    var Validator = require("br/presenter/validator/Validator");
    var Core = require("br/Core");
    AbstractFieldTest = function()
    {
    };

    AbstractFieldTest.inheritMethods = function(fFieldTest)
    {
        for(var sMethod in AbstractFieldTest.prototype)
        {
            fFieldTest.prototype[sMethod] = AbstractFieldTest.prototype[sMethod];
        }
    };

    AbstractFieldTest.prototype._getTestValidator = function()
    {
        var fValidator = function(){};
        Core.implement(fValidator, Validator);

        fValidator.prototype.validate = function(sText, mConfig, oValidationResult)
        {
            var bIsValid = (sText == "fail") ? false : true;

            oValidationResult.setResult(bIsValid, "only 'pass' is valid");
        };

        return new fValidator();
    };

    AbstractFieldTest.prototype.test_fieldsContainANumberOfPredefinedProperties = function()
    {
        var fField = this._$getFieldClass();
        var oField = new fField();

        assertTrue("1a", oField.label instanceof WritableProperty);
        assertTrue("1b", oField.value instanceof EditableProperty);
        assertTrue("1c", oField.hasError instanceof WritableProperty);
        assertTrue("1d", oField.failureMessage instanceof WritableProperty);
    };
})();

