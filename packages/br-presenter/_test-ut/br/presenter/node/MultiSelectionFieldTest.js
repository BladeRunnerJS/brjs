(function() {
    var OptionsNodeList = require('br-presenter/node/OptionsNodeList');
    var Errors = require("br/Errors");
    var WritableProperty = require('br-presenter/property/WritableProperty');
    var EditableProperty = require('br-presenter/property/EditableProperty');
    var Validator = require('br-presenter/validator/Validator');
    var Core = require("br/Core");
    var MultiSelectionField = require('br-presenter/node/MultiSelectionField');
    MultiSelectionFieldTest = TestCase("MultiSelectionFieldTest");
    AbstractFieldTest.inheritMethods(MultiSelectionFieldTest);

    MultiSelectionFieldTest.prototype._$getFieldClass = function()
    {
        return MultiSelectionField;
    };

    MultiSelectionFieldTest.prototype.getTestValidator = function()
    {
        var fValidator = function()
        {
        };
        
        Core.extend(fValidator, Validator);
        fValidator.prototype.validate = function(pOptions, mAttributes, oValidationResult)
        {
            var bIsValid = (pOptions.length == 2);
            oValidationResult.setResult(bIsValid, "exactly 2 options must be selected");
        };
        
        return new fValidator();
    };

    MultiSelectionFieldTest.prototype.test_selectionFieldCanBeConstructedWithoutArguments = function()
    {
        var oMultiSelectionField = new MultiSelectionField();
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", [], oMultiSelectionField.value.getValue());
        assertEquals("1c", [], oMultiSelectionField.options.getOptions());
        assertFalse("1d", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_defaultValuesCanBePassedInViaConstructor = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option1"]);
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["option1"], oMultiSelectionField.value.getValue());
        assertFalse("1c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option2"]);
        assertEquals("2a", "", oMultiSelectionField.label.getValue());
        assertEquals("2b", ["option2"], oMultiSelectionField.value.getValue());
        assertFalse("2c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option1", "option2"]);
        assertEquals("3a", "", oMultiSelectionField.label.getValue());
        assertEquals("3b", ["option1", "option2"], oMultiSelectionField.value.getValue());
        assertFalse("3c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(["option1", "option2"], []);
        assertEquals("4a", "", oMultiSelectionField.label.getValue());
        assertEquals("4b", [], oMultiSelectionField.value.getValue());
        assertFalse("4c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_defaultValuesCanBePassedInViaConstructorAsMap = function()
    {
        var mKeyLabels = {"option1":  "option1Label", "option2": "option2Label"};
        var oMultiSelectionField = new MultiSelectionField(mKeyLabels, ["option1"]);
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["option1"], oMultiSelectionField.value.getValue());
        assertFalse("1c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(mKeyLabels, ["option2"]);
        assertEquals("2a", "", oMultiSelectionField.label.getValue());
        assertEquals("2b", ["option2"], oMultiSelectionField.value.getValue());
        assertFalse("2c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(mKeyLabels, ["option1", "option2"]);
        assertEquals("3a", "", oMultiSelectionField.label.getValue());
        assertEquals("3b", ["option1", "option2"], oMultiSelectionField.value.getValue());
        assertFalse("3c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(mKeyLabels, []);
        assertEquals("4a", "", oMultiSelectionField.label.getValue());
        assertEquals("4b", [], oMultiSelectionField.value.getValue());
        assertFalse("4c", oMultiSelectionField.hasError.getValue());
    };


    MultiSelectionFieldTest.prototype.test_defaultValuesCanBePassedIntoConstructorAsEditableProperty = function()
    {
        var oEditableProperty = new EditableProperty(["option1"]);
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], oEditableProperty);
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["option1"], oMultiSelectionField.value.getValue());
        assertFalse("1c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_defaultValuesCannotBePassedIntoConstructorAsNonEditableProperty = function()
    {
        var oProperty = new WritableProperty(["option1"]);
        assertException("1a", function(){
            var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], oProperty);
        }, Errors.INVALID_PARAMETERS);
    };

    MultiSelectionFieldTest.prototype.test_theValueDefaultsToEmptyIfNotSpecified = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", [], oMultiSelectionField.value.getValue());
        assertFalse("1c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_anErrorIsDisplayedIfTheDefaultValueContainsANonOption = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["no-such-option"]);
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertTrue("1c", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option1", "no-such-option", "option2"]);
        assertEquals("2a", "", oMultiSelectionField.label.getValue());
        assertEquals("2b", ["option1", "no-such-option", "option2"], oMultiSelectionField.value.getValue());
        assertTrue("2c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_editablePropertiesCanBePassedInViaConstructor = function() {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"],
            new EditableProperty(["option2"]));
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["option2"], oMultiSelectionField.value.getValue());
    };

    MultiSelectionFieldTest.prototype.test_nonEditablePropertiesCanNotBePassedInViaConstructor = function()
    {
        assertException("1a", function(){
            new MultiSelectionField(["option1", "option2"],
                new WritableProperty(["option1"]));
        }, Errors.INVALID_PARAMETERS);
    };

    MultiSelectionFieldTest.prototype.test_passingANonExistentOptionAsAnEditablePropertyStillCausesAnError = function() {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"],
            new EditableProperty(["no-such-option"]));
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertTrue("1c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_optionsCanBePassedInAsAnOptionsNodeList = function() {
        var oMultiSelectionField = new MultiSelectionField(new OptionsNodeList(["option1", "option2"]));
        assertEquals("1a", "", oMultiSelectionField.label.getValue());
        assertEquals("1b", [], oMultiSelectionField.value.getValue());
        assertEquals("1c", ["option1", "option2"], oMultiSelectionField.options.getOptions());
        assertFalse("1c", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_theValueCanBeChangedToOneOfTheAvailableOptions = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.value.setUserEnteredValue(["option2"]);
        assertEquals("2a", ["option2"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_theValueCanBeChangedToBeAllTheAvailableOptions = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.value.setUserEnteredValue(["option1", "option2"]);
        assertEquals("2a", ["option1", "option2"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_theValueCanBeChangedBackToAnEmptyArray = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option1"]);
        assertEquals("1a", ["option1"], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.value.setUserEnteredValue([]);
        assertEquals("2a", [], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_anErrorIsDisplayedIfTheValueIsChangedToANonExistentOption = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.value.setUserEnteredValue(["no-such-option"]);
        assertEquals("2a", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertTrue("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_aNonExistentOptionCanBeSelectedWithoutErroringIfConfiguredToAllowThis = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        oMultiSelectionField.allowInvalidSelections(true);
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.value.setUserEnteredValue(["no-such-option"]);
        assertEquals("2a", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_theOptionsCanBeSuccesfullyChangedIfTheyStillIncludeTheCurrentValue = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["option1"]);
        assertEquals("1a", ["option1"], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.options.setOptions(["option0", "option1", "option2", "option3"]);
        assertEquals("2a", ["option1"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_theOptionsCanBeSuccesfullyAndCompletelyChangedWhenNoOptionsAreSelected = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        
        oMultiSelectionField.options.setOptions(["optionX", "optionY", "optionZ"]);
        assertEquals("2a", [], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_changingTheOptionsCanCauseAnErrorToBeFixedIfTheOptionsNowReIncludeTheCurrentValue = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"], ["no-such-option"]);
        assertEquals("1a", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertTrue("1b", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField.options.setOptions(["no-such-option"]);
        assertEquals("2a", ["no-such-option"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_optionsThatAreNoLongerAvailableAreRemovedFromTheSelection = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2", "option3"], ["option2", "option3"]);
        assertEquals("1a", ["option2", "option3"], oMultiSelectionField.value.getValue());
        assertFalse("1b", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField.options.setOptions(["option1", "option2"]);
        assertEquals("2a", ["option2"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField.options.setOptions(["option1", "option2", "option3"]);
        assertEquals("3a", ["option2"], oMultiSelectionField.value.getValue());
    };

    MultiSelectionFieldTest.prototype.test_whenConfiguredUpdatingTheOptionsDoesNotAutomaticallyUpdateTheValue = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2", "option3"], ["option2", "option3"]);
        oMultiSelectionField.automaticallyUpdateValueWhenOptionsChange(false);
        assertEquals("1a", ["option2", "option3"], oMultiSelectionField.value.getValue());
        assertFalse("1b", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField.options.setOptions(["option1", "option2"]);
        assertEquals("2a", ["option2", "option3"], oMultiSelectionField.value.getValue());
        assertTrue("2b", oMultiSelectionField.hasError.getValue());
        
        oMultiSelectionField.options.setOptions(["option1", "option2", "option3"]);
        assertEquals("3a", ["option2", "option3"], oMultiSelectionField.value.getValue());
        assertFalse("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_thatTheEntireDerivationChainPropagatesCorrectly = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["value1", "value2", "value3", "value4"], ["value1", "value3"]);
        oMultiSelectionField.value.addValidator(this.getTestValidator());
        
        assertEquals("1a", ["value1", "value3"], oMultiSelectionField.value.getValue());
        assertFalse("1b", oMultiSelectionField.hasError.getValue());
        assertEquals("1c", "", oMultiSelectionField.failureMessage.getValue());
        
        oMultiSelectionField.options.setOptions(["value1", "value2"]);
        
        assertEquals("2a", ["value1"], oMultiSelectionField.value.getValue());
        assertTrue("2b", oMultiSelectionField.hasError.getValue());
    };

    MultiSelectionFieldTest.prototype.test_canSetControlNameIndependentlyOfOtherPropertiesOnMultiSelectionField = function()
    {
        var oMultiSelectionField = new MultiSelectionField(["option1", "option2"]);
        oMultiSelectionField.label.setValue("label");
        oMultiSelectionField.controlName.setValue("controlName");
        assertEquals("1a", [], oMultiSelectionField.value.getValue());
        assertEquals("1b", "label", oMultiSelectionField.label.getValue());
        assertEquals("1c", "controlName", oMultiSelectionField.controlName.getValue());
    };
})();
