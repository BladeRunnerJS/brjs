'use strict';

var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var DateField = require('br/presenter/node/DateField');

/**
 * @module br/presenter/node/CalendarDateField
 */

/**
 * @class
 * @alias module:br/presenter/node/CalendarDateField
 * @extends module:br/presenter/node/DateField
 * 
 * @classdesc
 * Represents a DateField that can be updated with a calendar.
 * 
 * @param vDate The initial date
 * @param vStartDate The first selectable date
 * @param vEndDate The last selectable date.
 */
function CalendarDateField(vDate, vStartDate, vEndDate) {
	DateField.call(this, vDate, vStartDate, vEndDate);

	/**
	 * The current month displayed.
	 * @type br.presenter.property.WritableProperty
	 */
	this.currentMonth = new WritableProperty();

	/**
	 * The current year displayed
	 * @type br.presenter.property.WritableProperty
	 */
	this.currentYear = new WritableProperty();

	var oStart = this.startDate.getDateValue();
	var oEnd = this.endDate.getDateValue();
	var oNow = new Date();

	if ((!oStart || (oStart < oNow)) && (!oEnd || (oEnd > oNow))) {
		this.currentMonth.setValue(oNow.getMonth());
		this.currentYear.setValue(oNow.getFullYear());
	} else if (oStart) {
		this.currentMonth.setValue(oStart.getMonth());
		this.currentYear.setValue(oStart.getFullYear());
	} else if (oEnd) {
		this.currentMonth.setValue(oEnd.getMonth());
		this.currentYear.setValue(oEnd.getFullYear());
	}

	/** @private */
	this.m_nValidityStrategy = CalendarDateField.ALL_DAYS;

	/**
	 * The current valid dates is the displayed month.
	 * @type br.presenter.property.WritableProperty
	 */
	this.validDatesForMonth = new WritableProperty();
	this._updateValidDates();

	// Listeners
	this.currentMonth.addChangeListener(this, '_updateValidDates');
	this.currentYear.addChangeListener(this, '_updateValidDates');
}

Core.extend(CalendarDateField, DateField);

/**
 * Represents the ALL days strategy
 * 
 * @type int
 * @see #setAvailableDateStrategy
 */
CalendarDateField.ALL_DAYS = 0;

/**
 * Represents the NO days strategy
 * 
 * @type int
 * @see #setAvailableDateStrategy
 */
CalendarDateField.NO_DAYS = 1;

/**
 * Sets the strategy of available dates. 
 * 
 * @param {int} nStrategy one of the static constants (e.g. ALL_DAYS, or NO_DAYS) on this class.
 */
CalendarDateField.prototype.setAvailableDateStrategy = function(nStrategy) {
	this.m_nValidityStrategy = nStrategy;
	this._updateValidDates();
};

/**
 * Returns an array of integers, which are the valid dates for the current month.
 * @type Array
 */
CalendarDateField.prototype.getValidDatesForMonth = function() {
	return this.validDatesForMonth.getValue();
};

// *********************** Private Methods ***********************

/**
 * @private
 */
CalendarDateField.prototype._updateValidDates = function() {
	if (this.m_nValidityStrategy === CalendarDateField.ALL_DAYS) {
		var pDates = this._getAllDatesForMonth(this.currentYear.getValue(), this.currentMonth.getValue());
		this.validDatesForMonth.setValue(pDates);
	} else if (this.m_nValidityStrategy === CalendarDateField.NO_DAYS) {
		this.validDatesForMonth.setValue([]);
	}
};

/**
 * @private
 * @param nYear
 * @param nMonth
 */
CalendarDateField.prototype._getAllDatesForMonth = function(nYear, nMonth) {
	var pDates = [
		1, 2, 3, 4, 5, 6, 7,
		8, 9, 10, 11, 12, 13, 14,
		15, 16, 17, 18, 19, 20, 21,
		22, 23, 24, 25, 26, 27, 28
	];
	var oDate = new Date(nYear, nMonth, 28);
	for (var nDay = 29, limit = 32; nDay < limit; nDay++) {
		oDate.setDate(nDay);
		if (oDate.getMonth() !== nMonth) {
			break;
		} else {
			pDates.push(nDay);
		}
	}

	return pDates;
};

module.exports = CalendarDateField;
