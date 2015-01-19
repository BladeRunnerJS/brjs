CalendarDateFieldTest = TestCase("CalendarDateFieldTest");

CalendarDateFieldTest.prototype.test_defaultValuesWhenConstructingACalendarDateFieldWithoutParams = function()
{
	var oNow = new Date();
	var oCDField = new br.presenter.node.CalendarDateField();
	assertUndefined("1a", oCDField.value.getValue());
	assertUndefined("1b", oCDField.startDate.getValue());
	assertUndefined("1c", oCDField.startDate.getValue());

	assertEquals("2a", oNow.getMonth(), oCDField.currentMonth.getValue());
	assertEquals("2b", oNow.getFullYear(), oCDField.currentYear.getValue());
	var pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("2c", (pValidDates.length >= 28) && (pValidDates.length < 32));
};

CalendarDateFieldTest.prototype.test_aStartDateInThePastResultsInCurrentMonthAndDateBeingNow = function()
{
	var oNow = new Date();
	var oPast = new Date();
	oPast.setFullYear(oPast.getFullYear() - 1);
	oPast.setMonth(oPast.getMonth() - 1);

	var oCDField = new br.presenter.node.CalendarDateField(null, oPast, null);
	assertEquals("1a", oNow.getMonth(), oCDField.currentMonth.getValue());
	assertEquals("1b", oNow.getFullYear(), oCDField.currentYear.getValue());
};

CalendarDateFieldTest.prototype.test_aFutureStartDateResultsInCurrentMonthAndDateBeingInTheFuture = function()
{
	var oFuture = new Date();
	oFuture.setFullYear(oFuture.getFullYear() + 1);
	oFuture.setMonth(oFuture.getMonth() + 2);

	var oCDField = new br.presenter.node.CalendarDateField(null, oFuture, null);
	assertEquals("1a", oFuture.getMonth(), oCDField.currentMonth.getValue());
	assertEquals("1b", oFuture.getFullYear(), oCDField.currentYear.getValue());
};

CalendarDateFieldTest.prototype.test_anStartAndEndDateInThePastResultsInCurrentMonthAndDateBeingSetToTheStartDate = function()
{
	var oPastStart = new Date();
	oPastStart.setFullYear(oPastStart.getFullYear() - 2);
	oPastStart.setMonth(oPastStart.getMonth() - 2);

	var oPastEnd = new Date();
	oPastEnd.setFullYear(oPastEnd.getFullYear() - 1);
	oPastEnd.setMonth(oPastEnd.getMonth() - 1);

	var oCDField = new br.presenter.node.CalendarDateField(null, oPastStart, oPastEnd);
	assertEquals("1a", oPastStart.getMonth(), oCDField.currentMonth.getValue());
	assertEquals("1b", oPastStart.getFullYear(), oCDField.currentYear.getValue());
};

CalendarDateFieldTest.prototype.test_anEndDateInThePastWithNoStartDateResultsInCurrentMonthAndDateBeingSetToEndDate = function()
{
	var oPastEnd = new Date();
	oPastEnd.setFullYear(oPastEnd.getFullYear() - 1);
	oPastEnd.setMonth(oPastEnd.getMonth() - 1);

	var oCDField = new br.presenter.node.CalendarDateField(null, null, oPastEnd);
	assertEquals("1a", oPastEnd.getMonth(), oCDField.currentMonth.getValue());
	assertEquals("1b", oPastEnd.getFullYear(), oCDField.currentYear.getValue());
};

CalendarDateFieldTest.prototype.test_changingTheCurrentMonthChangesTheValidDates = function()
{
	var oTestDate = new Date();
	oTestDate.setFullYear(oTestDate.getFullYear() + 1);
	oTestDate.setMonth(0); // January

	var oCDField = new br.presenter.node.CalendarDateField(null, oTestDate, null);
	oCDField.setAvailableDateStrategy(br.presenter.node.CalendarDateField.ALL_DAYS);
	var pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1a", pValidDates.length === 31);

	oCDField.currentMonth.setValue(1); // February
	pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1b", pValidDates.length <= 29);
};

CalendarDateFieldTest.prototype.test_changingTheCurrentYearChangesTheValidDates = function()
{
	var oTestDate = new Date();
	oTestDate.setDate(15); // 15th
	oTestDate.setMonth(0); // January

	var oCDField = new br.presenter.node.CalendarDateField(null, oTestDate, null);
	oCDField.setAvailableDateStrategy(br.presenter.node.CalendarDateField.ALL_DAYS);
	
	var pValidDates = oCDField.getValidDatesForMonth();
	var iDaysInThisMonth = new Date(new Date().getYear(), new Date().getMonth()+1, 0).getDate();
	assertTrue("1a", pValidDates.length === iDaysInThisMonth);

	oCDField.currentYear.setValue(2011); // non-leap year
	oCDField.currentMonth.setValue(1); // February
	pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1b", pValidDates.length === 28);

	oCDField.currentYear.setValue(2012); // go to leap year
	pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1b", pValidDates.length === 29);
};

CalendarDateFieldTest.prototype.test_changingAvailabilityStrategyToNODAYSChangesValidDatesForMonth = function()
{
	var oNow = new Date();
	var oCDField = new br.presenter.node.CalendarDateField();

	var pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1a", (pValidDates.length >= 28) && (pValidDates.length < 32));

	oCDField.setAvailableDateStrategy(br.presenter.node.CalendarDateField.NO_DAYS);
	pValidDates = oCDField.getValidDatesForMonth();
	assertTrue("1a", pValidDates.length === 0);
};
