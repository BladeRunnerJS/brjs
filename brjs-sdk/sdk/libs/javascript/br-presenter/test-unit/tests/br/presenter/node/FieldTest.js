(function() {
    var Errors = require("br/Errors");
    var WritableProperty = require("br/presenter/property/WritableProperty");
    var EditableProperty = require("br/presenter/property/EditableProperty");
    var Field = require("br/presenter/node/Field");
    FieldTest = TestCase("FieldTest");
    AbstractFieldTest.inheritMethods(FieldTest);

    FieldTest.prototype._$getFieldClass = function()
    {
        return Field;
    };

    FieldTest.prototype.test_defaultValuesCanBePassedInViaConstructor = function()
    {
        var oField = new Field();
        assertEquals("1a", "", oField.label.getValue());
        assertEquals("1b", undefined, oField.value.getValue());

        var oField = new Field("value");
        oField.label.setValue("label");
        assertEquals("2a", "label", oField.label.getValue());
        assertEquals("2b", "value", oField.value.getValue());
    };

    FieldTest.prototype.test_editablePropertyCanBePassedForValueInConstructor = function() {
        var oField = new Field(new EditableProperty("value"));
        assertEquals("Expect Field has label ''", "", oField.label.getValue());
        assertEquals("Expect Field has value 'value'", "value", oField.value.getValue());
    };

    FieldTest.prototype.test_cannotPassNonEditablePropertyInConstructor = function() {
        assertException("Can't pass non-editable properties into Field", function(){
            new Field(new WritableProperty("value"));
        }, Errors.INVALID_PARAMETERS);
    };

    FieldTest.prototype.test_hasErrorAndFailureMessageAreSetCorrectly = function()
    {
        var fField = this._$getFieldClass();
        var oField = new fField();
        
        assertFalse("1a", oField.hasError.getValue());
        assertEquals("1b", "", oField.failureMessage.getValue());
        
        oField.value.addValidator(this._getTestValidator()).setUserEnteredValue("fail");
        assertTrue("2a", oField.hasError.getValue());
        assertEquals("2b", "only 'pass' is valid", oField.failureMessage.getValue());
    };

    FieldTest.prototype.test_canSetControlNameIndependentlyOfOtherPropertiesOnAField = function()
    {
        var oField = new Field("value");
        oField.label.setValue("label");
        oField.controlName.setValue("controlName");
        assertEquals("1a", "value", oField.value.getValue());
        assertEquals("1b", "label", oField.label.getValue());
        assertEquals("1c", "controlName", oField.controlName.getValue());
    };
})();
