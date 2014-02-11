br.Core.thirdparty("momentjs");

/**
 * Constructs a new instance of <code>DateField</code>.
 *
 * @class
 * A <code>PresentationNode</code> containing all of the attributes necessary to model a
 * date input field on screen.
 * 
 * @constructor
 * @param [vDate] The initial value of the date field, either using a
 * String or as a {@link br.presenter.property.EditableProperty}.
 * @param [vStartDate] Start of the date range for this field, either as
 * an ISO Date String, a native Date object, or a {@link br.presenter.property.ISODateProperty}
 * @param [vEndDate] End of the date range for this field, either as
 * an ISO Date String, a native Date object, or a {@link br.presenter.property.ISODateProperty}
 * @extends br.presenter.node.Field
 */
br.presenter.node.DateField = function(vDate, vStartDate, vEndDate)
{
	if (vDate instanceof Date)
	{
		vDate = moment(vDate).format("YYYY-MM-DD");
	}
	// call super constructor
	br.presenter.node.Field.call(this, vDate);
	this.value.addValidator(new br.presenter.validator.ISODateValidator(), {});


	if (!(vStartDate instanceof br.presenter.property.ISODateProperty))
	{
		vStartDate = new br.presenter.property.ISODateProperty(vStartDate);
	}

	if (!(vEndDate instanceof br.presenter.property.ISODateProperty))
	{
		vEndDate = new br.presenter.property.ISODateProperty(vEndDate);
	}

	var oStart = vStartDate.getDateValue();
	var oEnd = vEndDate.getDateValue();
	if (oStart && oEnd && (oEnd < oStart))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "Start date was later than the end date");
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
	this.m_nCrossValidatorId = br.presenter.validator.CrossValidationPropertyBinder.bindValidator({
		selectedDate: this.value,
		startDate: this.startDate,
		endDate: this.endDate
	}, new br.presenter.validator.DateRangeCrossPropertyValidator());
	this.value.forceValidation();
};
br.Core.extend(br.presenter.node.DateField, br.presenter.node.Field);
