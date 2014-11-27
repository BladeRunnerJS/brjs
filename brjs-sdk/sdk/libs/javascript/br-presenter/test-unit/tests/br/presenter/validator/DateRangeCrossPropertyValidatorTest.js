DateRangeCrossPropertyValidatorTest = TestCase("DateRangeCrossPropertyValidatorTest");

DateRangeCrossPropertyValidatorTest.prototype.setUp = function()
{
	this.oCrossValidator = new br.presenter.validator.DateRangeCrossPropertyValidator();
	this.oDate = new br.presenter.property.EditableProperty();
	this.oStartDate = new br.presenter.property.ISODateProperty();
	this.oEndDate = new br.presenter.property.ISODateProperty();

	this.mProperties = {
		selectedDate: this.oDate,
		startDate: this.oStartDate,
		endDate: this.oEndDate
	};
	
	this.oValidationResult = new br.presenter.validator.ValidationResult();
};

DateRangeCrossPropertyValidatorTest.prototype.test_withNoDateAndNoStartAndEndValidationPasses = function()
{
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_withNoStartAndEndValidationPasses = function()
{
	this.oDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_passesWhenDateIsEqualToOrGreaterThanStartDateWithNoEndDate = function()
{
	this.oDate.setValue("2000-01-01");
	this.oStartDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());

	this.oDate.setValue("2000-01-02");
	this.oStartDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_failsWhenDateIsLessThanStartDateWithNoEndDate = function()
{
	this.oDate.setValue("2000-01-01");
	this.oStartDate.setValue("2000-01-02");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_passesWhenDateIsEqualToOrLessThanEndDateWithNoStartDate = function()
{
	this.oDate.setValue("2000-01-01");
	this.oEndDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());

	this.oDate.setValue("2000-01-01");
	this.oEndDate.setValue("2000-01-02");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_failsWhenDateIsGreaterThanEndDateWithNoStartDate = function()
{
	this.oDate.setValue("2000-01-02");
	this.oEndDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_passesWhenAllDatesAreEqual = function()
{
	this.oDate.setValue("2000-01-01");
	this.oStartDate.setValue("2000-01-01");
	this.oEndDate.setValue("2000-01-01");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_passesWhenAllDateIsInRangeOfStartAndEndDates = function()
{
	this.oStartDate.setValue("2000-01-01");
	this.oDate.setValue("2000-01-02");
	this.oEndDate.setValue("2000-01-03");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());

	this.oStartDate.setValue("2000-01-02");
	this.oDate.setValue("2000-01-02");
	this.oEndDate.setValue("2000-01-03");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());

	this.oStartDate.setValue("2000-01-01");
	this.oDate.setValue("2000-01-02");
	this.oEndDate.setValue("2000-01-02");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertTrue(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_failsWhenAllDateIsOutOfRangeOfStartAndEndDates = function()
{
	this.oStartDate.setValue("2000-01-02");
	this.oDate.setValue("2000-01-01");
	this.oEndDate.setValue("2000-01-03");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());

	this.oStartDate.setValue("2000-01-02");
	this.oDate.setValue("2000-01-04");
	this.oEndDate.setValue("2000-01-03");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());
};

DateRangeCrossPropertyValidatorTest.prototype.test_failsWhenAllStartAndEndDatesAreWrongWayAround = function()
{
	this.oStartDate.setValue("2000-01-03");
	this.oDate.setValue("2000-01-01");
	this.oEndDate.setValue("2000-01-02");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());

	this.oStartDate.setValue("2000-01-03");
	this.oDate.setValue("2000-01-04");
	this.oEndDate.setValue("2000-01-02");
	this.oCrossValidator.validate(this.mProperties, this.oValidationResult);
	assertFalse(this.oValidationResult.isValid());
};
