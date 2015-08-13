'use strict';

var DateRangeCrossPropertyValidator = require('br/presenter/validator/DateRangeCrossPropertyValidator');
var CrossValidationPropertyBinder = require('br/presenter/validator/CrossValidationPropertyBinder');
var Errors = require('br/Errors');
var ISODateProperty = require('br/presenter/property/ISODateProperty');
var ISODateValidator = require('br/presenter/validator/ISODateValidator');
var Field = require('br/presenter/node/Field');
var Core = require('br/Core');

/**
 * @module br/presenter/node/DateField
 */

var momentjs = require('momentjs');

/**
 * Constructs a new instance of <code>DateField</code>.
 *
 * @class
 * @alias module:br/presenter/node/DateField
 * @extends module:br/presenter/node/Field
 *
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to model a
 * date input field on screen.
 *
 * @param [vDate] The initial value of the date field, either using a
 * String or as a {@link module:br/presenter/property/EditableProperty}.
 * @param [vStartDate] Start of the date range for this field, either as
 * an ISO Date String, a native Date object, or a {@link module:br/presenter/property/ISODateProperty}
 * @param [vEndDate] End of the date range for this field, either as
 * an ISO Date String, a native Date object, or a {@link module:br/presenter/property/ISODateProperty}
 */
function DateField(vDate, vStartDate, vEndDate) {
	if (vDate instanceof Date) {
		vDate = moment(vDate).format('YYYY-MM-DD');
	}
	// call super constructor
	Field.call(this, vDate);
	this.value.addValidator(new ISODateValidator(), {});


	if (!(vStartDate instanceof ISODateProperty)) {
		vStartDate = new ISODateProperty(vStartDate);
	}

	if (!(vEndDate instanceof ISODateProperty)) {
		vEndDate = new ISODateProperty(vEndDate);
	}

	var oStart = vStartDate.getDateValue();
	var oEnd = vEndDate.getDateValue();
	if (oStart && oEnd && (oEnd < oStart)) {
		throw new Errors.InvalidParametersError('Start date was later than the end date');
	}

	/**
	 * Start of the date range for this field
	 * @type br.presenter.property.ISODateProperty
	 */
	this.startDate = vStartDate;

	/**
	 * End of the date range for this field
	 * @type br.presenter.property.ISODateProperty
	 */
	this.endDate = vEndDate;

	/** @private */
	this.m_nCrossValidatorId = CrossValidationPropertyBinder.bindValidator({
		selectedDate: this.value,
		startDate: this.startDate,
		endDate: this.endDate
	}, new DateRangeCrossPropertyValidator());
	this.value.forceValidation();
}

Core.extend(DateField, Field);

module.exports = DateField;
