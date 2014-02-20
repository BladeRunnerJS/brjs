"use strict";

// caplin.thirdparty("momentjs");

var i18n = require('br/I18n');

/**
 * @private
 */
function LocalisedDate(date) {
	this.date = date;
}

LocalisedDate.prototype.format = function(dateFormat) {
	var formattedDate = (dateFormat === "U") ? moment(this.date).unix() : moment(this.date).format(dateFormat);
	if (this._containsAmPm(dateFormat)) {
		// format a date containing only the am or pm string
		var amPmString = moment(this.date).format("a");
		var localeAmPmString = i18n("br.i18n.date." + amPmString);
		formattedDate = formattedDate.replace(amPmString, localeAmPmString);
	}

	if (this._containsMonthName(dateFormat)) {
		var monthName = this._getMonthName();
		var monthToken = "br.i18n.date.month." + monthName.toLowerCase();
		if (this._containsAbbreviatedMonthName(dateFormat)) {
			monthToken = "br.i18n.date.month.short." + monthName.toLowerCase();
			monthName = this._getAbbreviatedMonthName();
		}
		formattedDate = formattedDate.replace(monthName, i18n(monthToken));
	}

	if (this._containsDayName(dateFormat)) {
		var dayName = this._getDayName();
		var dayToken = "br.i18n.date.day." + dayName.toLowerCase();
		if (this._containsAbbreviatedDayName(dateFormat)) {
			dayToken = "br.i18n.date.day.short." + dayName.toLowerCase();
			dayName = this._getAbbreviatedDayName();
		}
		formattedDate = formattedDate.replace(dayName, i18n(dayToken));
	}

	return formattedDate;
};

LocalisedDate.prototype.format = function(dateFormat) {
	var formattedDate = (dateFormat === "U") ? moment(this.date).unix() : moment(this.date).format(dateFormat);
	if (this._containsAmPm(dateFormat)) {
		// format a date containing only the am or pm string
		var amPmString = moment(this.date).format("a");
		var localeAmPmString = i18n("br.i18n.date." + amPmString);
		formattedDate = formattedDate.replace(amPmString, localeAmPmString);
	}

	if (this._containsMonthName(dateFormat)) {
		var monthName = this._getMonthName();
		var monthToken = "br.i18n.date.month." + monthName.toLowerCase();
		if (this._containsAbbreviatedMonthName(dateFormat)) {
			monthToken = "br.i18n.date.month.short." + monthName.toLowerCase();
			monthName = this._getAbbreviatedMonthName();
		}
		formattedDate = formattedDate.replace(monthName, i18n(monthToken));
	}

	if (this._containsDayName(dateFormat)) {
		var dayName = this._getDayName();
		var dayToken = "br.i18n.date.day." + dayName.toLowerCase();
		if (this._containsAbbreviatedDayName(dateFormat)) {
			dayToken = "br.i18n.date.day.short." + dayName.toLowerCase();
			dayName = this._getAbbreviatedDayName();
		}
		formattedDate = formattedDate.replace(dayName, i18n(dayToken));
	}

	return formattedDate;
};

LocalisedDate.prototype._containsMonthName = function(dateFormat) {
	return (dateFormat.indexOf('MMM') !== -1) ? true : false;
};

LocalisedDate.prototype._containsAbbreviatedMonthName = function(dateFormat) {
	return this._containsMonthName(dateFormat) && !this._containsLongMonthName(dateFormat);
};

LocalisedDate.prototype._containsLongMonthName = function(dateFormat) {
	return (dateFormat.indexOf('MMMM') !== -1) ? true : false;
};

LocalisedDate.prototype._getAbbreviatedMonthName = function() {
	return moment(this.date).format("MMM");
};

LocalisedDate.prototype._getMonthName = function() {
	return moment(this.date).format("MMMM");
};

LocalisedDate.prototype._containsDayName = function(dateFormat) {
	return (dateFormat.indexOf('ddd') !== -1) ? true : false;
};

LocalisedDate.prototype._containsAbbreviatedDayName = function(dateFormat) {
	return this._containsDayName(dateFormat) && !this._containsLongDayName(dateFormat);
};

LocalisedDate.prototype._containsLongDayName = function(dateFormat) {
	return (dateFormat.indexOf('dddd') !== -1) ? true : false;
};

LocalisedDate.prototype._getAbbreviatedDayName = function() {
	return moment(this.date).format("ddd");
};

LocalisedDate.prototype._getDayName = function() {
	return moment(this.date).format("dddd");
};

LocalisedDate.prototype._containsAmPm = function(dateFormat) {
	return (dateFormat.indexOf('a') !== -1) ? true : false;
};

module.exports = LocalisedDate;
