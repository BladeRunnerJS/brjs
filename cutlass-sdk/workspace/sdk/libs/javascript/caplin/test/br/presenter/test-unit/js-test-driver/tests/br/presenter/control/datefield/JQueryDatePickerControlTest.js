JQueryDatePickerControlTest = TestCase("JQueryDatePickerControlTest");

JQueryDatePickerControlTest.prototype.setUp = function()
{
	this.m_oControlAdaptor = new br.presenter.control.datefield.JQueryDatePickerControl();
	this.m_oControlAdaptor.m_oJQueryNode.datepicker = function(){
		// do nothing
	};
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
