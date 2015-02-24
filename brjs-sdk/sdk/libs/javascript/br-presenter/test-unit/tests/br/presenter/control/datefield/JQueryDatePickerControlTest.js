JQueryDatePickerControlTest = TestCase("JQueryDatePickerControlTest");

JQueryDatePickerControlTest.prototype.setUp = function()
{
	this.m_oControlAdaptor = new br.presenter.control.datefield.JQueryDatePickerControl();
	var eElement = document.createElement('div');
	document.body.appendChild(eElement);

	this.m_oControlAdaptor.setElement(eElement);
};

JQueryDatePickerControlTest.prototype.test_datePickerCanBeBoundToDateField = function()
{
	var oDateField = new br.presenter.node.DateField();
	this.m_oControlAdaptor.setPresentationNode(oDateField);
};

JQueryDatePickerControlTest.prototype.test_datePickerThrowsExceptionIfBoundToVanillaPresentationNode = function()
{
	var oPresentationNode = new br.presenter.node.PresentationNode();
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oPresentationNode);
	}, "InvalidControlModelError");
};

JQueryDatePickerControlTest.prototype.test_datePickerThrowsExceptionIfBoundToVanillaField = function()
{
	var oField = new br.presenter.node.Field();
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oField);
	}, "InvalidControlModelError");
};

JQueryDatePickerControlTest.prototype.test_datePickerInitialisesWithCorrectDateFormat = function()
{
	var oDateField = new br.presenter.node.DateField();

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.onViewReady();

	assertEquals('yy-mm-dd', this.m_oControlAdaptor.m_oJQueryNode.datepicker('option', 'dateFormat'));
};

JQueryDatePickerControlTest.prototype.test_datePickerCanHaveOptionsOverridden = function()
{
	var oDateField = new br.presenter.node.DateField();

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.setOptions({
		dateFormat: 'YYYYMMDD'
	});

	this.m_oControlAdaptor.onViewReady();

	assertEquals('YYYYMMDD', this.m_oControlAdaptor.m_oJQueryNode.datepicker('option', 'dateFormat'));
};

JQueryDatePickerControlTest.prototype.test_datePickerInitialisesWithCorrectDate = function()
{
	var oDateField = new br.presenter.node.DateField();
	// 11th Feb 2015 in yy-mm-dd, the control's default format
	oDateField.value.setValue('15-02-11');

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.onViewReady();

	assertEquals(2015, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getFullYear());
	assertEquals(1, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getMonth()); // January is 0
	assertEquals(11, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getDate());
};

JQueryDatePickerControlTest.prototype.test_datePickerDateUpdatesWhenPropertyChanges = function()
{
	var oDateField = new br.presenter.node.DateField();
	// 11th Feb 2015 in yy-mm-dd, the control's default format
	oDateField.value.setValue('15-02-11');

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.onViewReady();

	// 23rd March 2016 in yy-mm-dd, the control's default format
	oDateField.value.setValue('16-03-23');

	assertEquals(2016, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getFullYear());
	assertEquals(2, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getMonth()); // January is 0
	assertEquals(23, this.m_oControlAdaptor.m_oJQueryNode.datepicker('getDate').getDate());
};

JQueryDatePickerControlTest.prototype.test_datePickerStartsDisabledIfFieldIs = function()
{
	var oDateField = new br.presenter.node.DateField();
	oDateField.enabled.setValue(false);

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.onViewReady();

	assertTrue(this.m_oControlAdaptor.m_oJQueryNode.datepicker('isDisabled'));
};

JQueryDatePickerControlTest.prototype.test_datePickerIsDisabledWhenEnabledPropertyChanges = function()
{
	var oDateField = new br.presenter.node.DateField();

	this.m_oControlAdaptor.setPresentationNode(oDateField);
	this.m_oControlAdaptor.onViewReady();

	oDateField.enabled.setValue(false);
	assertTrue(this.m_oControlAdaptor.m_oJQueryNode.datepicker('isDisabled'));

	oDateField.enabled.setValue(true);
	assertFalse(this.m_oControlAdaptor.m_oJQueryNode.datepicker('isDisabled'));
};
