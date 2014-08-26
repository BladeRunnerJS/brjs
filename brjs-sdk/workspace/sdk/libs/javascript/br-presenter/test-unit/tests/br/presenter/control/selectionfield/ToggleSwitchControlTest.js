ToggleSwitchControlTest = TestCase("ToggleSwitchControlTest");

ToggleSwitchControlTest.prototype.setUp = function()
{
	this.m_oControlAdaptor = new br.presenter.control.selectionfield.ToggleSwitchControl();
};

ToggleSwitchControlTest.prototype.test_toggleSwitchCanBeBoundToSelectionField = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2"]);
	this.m_oControlAdaptor.setPresentationNode(oSelectionField);
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundToVanillaPresentationNode = function()
{
	var oPresentationNode = new br.presenter.node.PresentationNode();
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oPresentationNode);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundToMultiSelectionField = function()
{
	var oMultiSelectionField = new br.presenter.node.MultiSelectionField(["option #1", "option #2"]);
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oMultiSelectionField);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundToSelectionFieldWithZeroOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField([]);
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oSelectionField);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundToSelectionFieldWithOneOption = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1"]);
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oSelectionField);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundToSelectionFieldWithMoreThanTwoOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2", "option #3"]);
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oSelectionField);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundSelectionFieldIsChangedToHaveLessThanTwoOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2"]);

	this.m_oControlAdaptor.setPresentationNode(oSelectionField);

	assertException(function() {
		oSelectionField.options.setOptions(["option #1"]);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_toggleSwitchThrowsExceptionIfBoundSelectionFieldIsChangedToHaveMoreThanTwoOptions = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2"]);
	oSelectionField._$setPath("a", {});
	
	this.m_oControlAdaptor.setPresentationNode(oSelectionField);

	assertException(function() {
		oSelectionField.options.setOptions(["option #1", "option #2", "option #3"]);
	}, "InvalidControlModelError");
};

ToggleSwitchControlTest.prototype.test_callingDestroyMethodWorksAndreferenceToElementIsRemoved = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2"]);
	oSelectionField._$setPath("a", {});
	
	this.m_oControlAdaptor.setPresentationNode(oSelectionField);
	
	this.m_oControlAdaptor.destroy();
	
	assertNull("Element reference still present", this.m_oControlAdaptor.m_eElement);
}
