'use strict';

/**
 * @module br/presenter/control/datefield/JQueryDatePickerLocaleUtil
 */

var i18n = require('br/I18n');

/**
 * @class
 * @alias module:br/presenter/control/datefield/JQueryDatePickerLocaleUtil
 *
 * @classdesc utility class that sets the jQuery datepicker locale-specific properties to i18n properties.
 *
 */
var JQueryDatePickerLocaleUtil = {};

/**
 * @description Instead of using different jQuery datepicker locales, this method allows the default locale to be used,
 * instead setting locale properties to i18n keys so that the user can internationalize their app using BRJS i18n keys.
 */
JQueryDatePickerLocaleUtil.getDefaultLocales = function() {
	var defaults = {
		closeText: i18n('br.presenter.datepicker.closeText'),
		prevText: i18n('br.presenter.datepicker.prevText'),
		nextText: i18n('br.presenter.datepicker.nextText'),
		currentText: i18n('br.presenter.datepicker.currentText'),
		monthNames: [
			i18n('br.presenter.datepicker.months.january'),
			i18n('br.presenter.datepicker.months.feburary'),
			i18n('br.presenter.datepicker.months.march'),
			i18n('br.presenter.datepicker.months.april'),
			i18n('br.presenter.datepicker.months.may'),
			i18n('br.presenter.datepicker.months.june'),
			i18n('br.presenter.datepicker.months.july'),
			i18n('br.presenter.datepicker.months.august'),
			i18n('br.presenter.datepicker.months.september'),
			i18n('br.presenter.datepicker.months.october'),
			i18n('br.presenter.datepicker.months.november'),
			i18n('br.presenter.datepicker.months.december')
		],
		monthNamesShort: [
			i18n('br.presenter.datepicker.months.short.january'),
			i18n('br.presenter.datepicker.months.short.feburary'),
			i18n('br.presenter.datepicker.months.short.march'),
			i18n('br.presenter.datepicker.months.short.april'),
			i18n('br.presenter.datepicker.months.short.may'),
			i18n('br.presenter.datepicker.months.short.june'),
			i18n('br.presenter.datepicker.months.short.july'),
			i18n('br.presenter.datepicker.months.short.august'),
			i18n('br.presenter.datepicker.months.short.september'),
			i18n('br.presenter.datepicker.months.short.october'),
			i18n('br.presenter.datepicker.months.short.november'),
			i18n('br.presenter.datepicker.months.short.december')
		],
		dayNames: [
			i18n('br.presenter.datepicker.days.sunday'),
			i18n('br.presenter.datepicker.days.monday'),
			i18n('br.presenter.datepicker.days.tuesday'),
			i18n('br.presenter.datepicker.days.wednesday'),
			i18n('br.presenter.datepicker.days.thursday'),
			i18n('br.presenter.datepicker.days.friday'),
			i18n('br.presenter.datepicker.days.saturday')
		],
		dayNamesShort: [
			i18n('br.presenter.datepicker.days.short.sunday'),
			i18n('br.presenter.datepicker.days.short.monday'),
			i18n('br.presenter.datepicker.days.short.tuesday'),
			i18n('br.presenter.datepicker.days.short.wednesday'),
			i18n('br.presenter.datepicker.days.short.thursday'),
			i18n('br.presenter.datepicker.days.short.friday'),
			i18n('br.presenter.datepicker.days.short.saturday')
		],
		dayNamesMin: [
			i18n('br.presenter.datepicker.days.min.sunday'),
			i18n('br.presenter.datepicker.days.min.monday'),
			i18n('br.presenter.datepicker.days.min.tuesday'),
			i18n('br.presenter.datepicker.days.min.wednesday'),
			i18n('br.presenter.datepicker.days.min.thursday'),
			i18n('br.presenter.datepicker.days.min.friday'),
			i18n('br.presenter.datepicker.days.min.saturday')
		],
		weekHeader: i18n('br.presenter.datepicker.weekHeader'),
		dateFormat: i18n('br.presenter.datepicker.dateFormat'),
		firstDay: 0,
		isRTL: i18n('br.presenter.datepicker.isRTL') === 'true',
		showMonthAfterYear: i18n('br.presenter.datepicker.showMonthAfterYear') === 'true',
		yearSuffix: ''
	};

	var i18nFirstDay = i18n('br.presenter.datepicker.firstDay');
	if (!isNaN(parseInt(i18nFirstDay, 10))) {
		defaults.firstDay = parseInt(i18nFirstDay, 10);
	}

	var i18nYearSuffix = i18n('br.presenter.datepicker.yearSuffix');
	if (i18nYearSuffix !== 'none') {
		defaults.yearSuffix = i18nYearSuffix;
	}

	return defaults;
};

br.presenter.control.datefield.JQueryDatePickerLocaleUtil = JQueryDatePickerLocaleUtil;
