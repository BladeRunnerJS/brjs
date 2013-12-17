SelectionFieldTest = TestCase("SelectionFieldTest");
AbstractFieldTest.inheritMethods(SelectionFieldTest);

SelectionFieldTest.prototype._$getFieldClass = function()
{
	return br.presenter.node.SelectionField;
};

SelectionFieldTest.prototype.getTestValidator = function()
{
	var fValidator = function()
	{
	};
	
	br.Core.extend(fValidator, br.presenter.validator.Validator);
	fValidator.prototype.validate = function(sText, mAttributes, oValidationResult)
	{
		var bIsValid = (sText == "goodvalue");
		oValidationResult.setResult(bIsValid, "must be 'goodvalue'");
	};
	
	return new fValidator();
};

SelectionFieldTest.prototype.test_selectionFieldCanBeConstructedWithoutArguments = function()
{
	var oSelectionField = new br.presenter.node.SelectionField();
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertNull("1b", oSelectionField.value.getValue());
	assertTrue("1c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_defaultValueCanBePassedInViaConstructor = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], "option1");
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "option1", oSelectionField.value.getValue());
	assertFalse("1c", oSelectionField.hasError.getValue());
	
	oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], "option2");
	assertEquals("2a", "", oSelectionField.label.getValue());
	assertEquals("2b", "option2", oSelectionField.value.getValue());
	assertFalse("2c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_defaultValueCanBePassedIntoConstructorAsEditableProperty = function()
{
	var oEditableProperty = new br.presenter.property.EditableProperty("option1");
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], oEditableProperty);
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "option1", oSelectionField.value.getValue());
	assertFalse("1c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_defaultValuesCannotBePassedIntoConstructorAsNonEditableProperty = function()
{
	var oProperty = new br.presenter.property.WritableProperty("option1");
	assertException("1a", function(){
		var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], oProperty);
	}, br.Errors.LEGACY);
};

SelectionFieldTest.prototype.test_theValueDefaultsToTheFirstOptionIfNotSpecified = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "option1", oSelectionField.value.getValue());
	assertFalse("1c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_theValueCanDefaultToAnEmptyString = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], "");
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "", oSelectionField.value.getValue());
	assertTrue("1c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_anErrorIsDisplayedIfTheDefaultValueIsNotOneOfTheAvailableOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], "no-such-option");
	assertEquals("1a", "", oSelectionField.label.getValue());
	
	assertEquals("1b", "no-such-option", oSelectionField.value.getValue());
	assertTrue("1c", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_editablePropertiesCanBePassedInViaConstructor = function() {
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"],
		new br.presenter.property.EditableProperty("option2"));
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "option2", oSelectionField.value.getValue());
};

SelectionFieldTest.prototype.test_nonEditablePropertiesCanNotBePassedInViaConstructor = function()
{
	assertException("1a", function(){
		new br.presenter.node.SelectionField(["option1", "option2"],
			new br.presenter.property.WritableProperty("option1"));
	}, br.Errors.LEGACY);
};

SelectionFieldTest.prototype.test_passingANonExistentOptionAsAnEditablePropertyStillCausesAnError = function() {
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"],
		new br.presenter.property.EditableProperty("no-such-option"));
	assertEquals("1a", "", oSelectionField.label.getValue());
	assertEquals("1b", "no-such-option", oSelectionField.value.getValue());
	assertTrue("1c", oSelectionField.hasError.getValue());
};

//SelectionFieldTest.prototype.test_optionsCanBePassedInAsAProperty = function() {
//	var oSelectionField = new br.presenter.node.SelectionField(new br.presenter.property.WritableProperty(["option1", "option2"]));
//	assertUndefined("1a", oSelectionField.label.getValue());
//	assertEquals("1b", "option1", oSelectionField.value.getValue());
//	assertEquals("1c", ["option1", "option2"], oSelectionField.options.getValue());
//	assertFalse("1c", oSelectionField.hasError.getValue());
//};

SelectionFieldTest.prototype.test_theValueCanBeChangedToOneOfTheAvailableOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	
	oSelectionField.value.setUserEnteredValue("option2");
	assertEquals("2a", "option2", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_anErrorIsDisplayedIfTheValueIsChangedToANonExistentOption = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	
	oSelectionField.value.setUserEnteredValue("no-such-option");
	assertEquals("2a", "no-such-option", oSelectionField.value.getValue());
	assertTrue("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_aNonExistentOptionCanBeSelectedWithoutErroringIfConfiguredToAllowThis = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	oSelectionField.allowInvalidSelections(true);
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	
	oSelectionField.value.setUserEnteredValue("no-such-option");
	assertEquals("2a", "no-such-option", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_theOptionsCanBeSuccesfullyChangedIfTheyStillIncludeTheCurrentValue = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions(["option0", "option1", "option2", "option3"]);
	assertEquals("2a", "option1", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_changingTheOptionsCausesAnErrorIfTheOptionsNoLongerIncludeTheCurrentValue = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions(["optionX", "optionY", "optionZ"]);
	assertEquals("2a", "option1", oSelectionField.value.getValue());
	assertTrue("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_changingTheOptionsCanCauseAnErrorToBeFixedIfTheOptionsNowReIncludeTheCurrentValue = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"], "no-such-option");
	assertEquals("1a", "no-such-option", oSelectionField.value.getValue());
	assertTrue("1b", oSelectionField.hasError.getValue());
	
	oSelectionField.options.setOptions(["no-such-option"]);
	assertEquals("2a", "no-such-option", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
};

SelectionFieldTest.prototype.test_whenConfiguredTheValueRevertsToDefaultIfTheCurrentValueIsNoLongerAnOption = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2", "option3"]);
	oSelectionField.automaticallyUpdateValueWhenOptionsChange(true);
	
	oSelectionField.value.setUserEnteredValue("option3");
	assertEquals("1a", "option3", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2"]);
	assertEquals("2a", "option1", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2", "option3"]);
	assertEquals("3a", "option1", oSelectionField.value.getValue());
};

SelectionFieldTest.prototype.test_whenConfiguredTheValueRevertsToASpecifiedDefaultIfTheCurrentValueIsNoLongerAnOption = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2", "option3"], "option2");
	oSelectionField.automaticallyUpdateValueWhenOptionsChange(true);
	
	oSelectionField.value.setUserEnteredValue("option3");
	assertEquals("1a", "option3", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2"]);
	assertEquals("2a", "option2", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2", "option3"]);
	assertEquals("3a", "option2", oSelectionField.value.getValue());
};

SelectionFieldTest.prototype.test_whenConfiguredTheValueRevertsToTheFirstOptionIfTheDefaultValueIsAlsoNoLongerAnOption = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2", "option3", "option4"], "option3");
	oSelectionField.automaticallyUpdateValueWhenOptionsChange(true);
	
	oSelectionField.value.setUserEnteredValue("option4");
	assertEquals("1a", "option4", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2"]);
	assertEquals("2a", "option1", oSelectionField.value.getValue());
	assertFalse("2b", oSelectionField.hasError.getValue());
	
	oSelectionField.options.setOptions(["option1", "option2", "option3", "option4"]);
	assertEquals("3a", "option1", oSelectionField.value.getValue());
};

SelectionFieldTest.prototype.test_evenWhenConfiguredTheValueDoesntChangeIfTheUpdatedOptionsIsAnEmptyArray = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	oSelectionField.automaticallyUpdateValueWhenOptionsChange(true);
	
	oSelectionField.value.setUserEnteredValue("option2");
	assertEquals("1a", "option2", oSelectionField.value.getValue());
	
	oSelectionField.options.setOptions([]);
	assertEquals("2a", "option2", oSelectionField.value.getValue());
};

SelectionFieldTest.prototype.test_thatTheEntireDerivationChainPropagatesCorrectly = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["goodvalue", "badvalue"]);
	oSelectionField.automaticallyUpdateValueWhenOptionsChange(true);
	oSelectionField.value.addValidator(this.getTestValidator());
	
	assertEquals("1a", "goodvalue", oSelectionField.value.getValue());
	assertFalse("1b", oSelectionField.hasError.getValue());
	assertEquals("1c", "", oSelectionField.failureMessage.getValue());
	
	oSelectionField.options.setOptions(["badvalue"]);
	
	assertEquals("2a", "badvalue", oSelectionField.value.getValue());
	assertTrue("2b", oSelectionField.hasError.getValue());
	assertEquals("2c", "must be 'goodvalue'", oSelectionField.failureMessage.getValue());
};

SelectionFieldTest.prototype.test_canSetControlNameIndependentlyOfOtherPropertiesOnASelectionField = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option1", "option2"]);
	oSelectionField.label.setValue("label");
	oSelectionField.controlName.setValue("controlName");
	assertEquals("1a", "option1", oSelectionField.value.getValue());
	assertEquals("1b", "label", oSelectionField.label.getValue());
	assertEquals("1c", "controlName", oSelectionField.controlName.getValue());
};
