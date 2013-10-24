/**
 * @class
 * Represents a DateField that can be updated with a calendar.
 * @constructor
 * @param vDate The initial date
 * @param vStartDate The first selectable date
 * @param vEndDate The last selectable date.
 * @extends br.presenter.node.DateField
 */
br.presenter.node.CalendarDateField = function(vDate, vStartDate, vEndDate)
{
	br.presenter.node.DateField.call(this, vDate, vStartDate, vEndDate);

	/**
	 * The current month displayed.
	 * @type br.presenter.property.WritableProperty
	 */
	this.currentMonth = new br.presenter.property.WritableProperty();

	/**
	 * The current year displayed
	 * @type br.presenter.property.WritableProperty
	 */
	this.currentYear = new br.presenter.property.WritableProperty();

	var oStart = this.startDate.getDateValue();
	var oEnd = this.endDate.getDateValue();
	var oNow = new Date();

	if ((!oStart || (oStart < oNow)) && (!oEnd || (oEnd > oNow)))
	{
		this.currentMonth.setValue(oNow.getMonth());
		this.currentYear.setValue(oNow.getFullYear());
	}
	else if (oStart)
	{
		this.currentMonth.setValue(oStart.getMonth());
		this.currentYear.setValue(oStart.getFullYear());
	}
	else if (oEnd)
	{
		this.currentMonth.setValue(oEnd.getMonth());
		this.currentYear.setValue(oEnd.getFullYear());
	}

	/** @private */
	this.m_nValidityStrategy = br.presenter.node.CalendarDateField.ALL_DAYS;

	/**
	 * The current valid dates is the displayed month.
	 * @type br.presenter.property.WritableProperty
	 */
	this.validDatesForMonth = new br.presenter.property.WritableProperty();
	this._updateValidDates();

	// Listeners
	this.currentMonth.addChangeListener(this,"_updateValidDates");
	this.currentYear.addChangeListener(this,"_updateValidDates");
};

br.extend(br.presenter.node.CalendarDateField, br.presenter.node.DateField);

/**
 * Represents the ALL days strategy
 * 
 * @type int
 * @see #setAvailableDateStrategy
 */
br.presenter.node.CalendarDateField.ALL_DAYS = 0;

/**
 * Represents the NO days strategy
 * 
 * @type int
 * @see #setAvailableDateStrategy
 */
br.presenter.node.CalendarDateField.NO_DAYS = 1;


/**
 * Sets the strategy of available dates. 
 * 
 * @param {int} nStrategy one of the static constants (e.g. ALL_DAYS, or NO_DAYS) on this class.
 */
br.presenter.node.CalendarDateField.prototype.setAvailableDateStrategy = function(nStrategy)
{
	this.m_nValidityStrategy = nStrategy;
	this._updateValidDates();
};

/**
 * Returns an array of integers, which are the valid dates for the current month.
 * @type Array
 */
br.presenter.node.CalendarDateField.prototype.getValidDatesForMonth = function()
{
	return this.validDatesForMonth.getValue();
};

// *********************** Private Methods ***********************

/**
 * @private
 */
br.presenter.node.CalendarDateField.prototype._updateValidDates = function()
{
	if (this.m_nValidityStrategy === br.presenter.node.CalendarDateField.ALL_DAYS)
	{
		var pDates = this._getAllDatesForMonth(this.currentYear.getValue(), this.currentMonth.getValue());
		this.validDatesForMonth.setValue(pDates);
	}
	else if (this.m_nValidityStrategy === br.presenter.node.CalendarDateField.NO_DAYS)
	{
		this.validDatesForMonth.setValue([]);
	}
};

/**
 * @private
 * @param nYear
 * @param nMonth
 */
br.presenter.node.CalendarDateField.prototype._getAllDatesForMonth = function(nYear, nMonth)
{
	var pDates = [
		 1, 2, 3, 4, 5, 6, 7,
		 8, 9,10,11,12,13,14,
		15,16,17,18,19,20,21,
		22,23,24,25,26,27,28
	];
	var oDate = new Date(nYear, nMonth, 28);
	for (var nDay = 29, limit = 32; nDay < limit; nDay++)
	{
		oDate.setDate(nDay);
		if (oDate.getMonth() !== nMonth)
		{
			break;
		}
		else
		{
			pDates.push(nDay);
		}
	}
	return pDates;
};
