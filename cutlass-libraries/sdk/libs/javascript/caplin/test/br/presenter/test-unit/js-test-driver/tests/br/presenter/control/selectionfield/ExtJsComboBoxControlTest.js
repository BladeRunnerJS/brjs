ExtJsComboBoxControlTest = TestCase("ExtJsComboBoxControlTest");

ExtJsComboBoxControlTest.prototype.setUp = function()
{
	this.m_oControlAdaptor = new br.presenter.control.selectionfield.ExtJsComboBoxControl();
	this.m_oControlAdaptor.m_oExtComboBox = {
		setValue:function() {
			// do nothing
		},
		setVisible:function() {
			// do nothing
		},
		setDisabled:function() {
			// do nothing
		}};

	this.m_oControlAdaptor.setElement({
		type: 'text',
		style: {
			display: ''
		}
	});
};

ExtJsComboBoxControlTest.prototype.test_comboBoxCanBeBoundToSelectionField = function()
{
	var oSelectionField = new br.presenter.node.SelectionField(["option #1", "option #2"]);
	this.m_oControlAdaptor.setPresentationNode(oSelectionField);
};

ExtJsComboBoxControlTest.prototype.test_comboBoxThrowsExceptionIfBoundToVanillaPresentationNode = function()
{
	var oPresentationNode = new br.presenter.node.PresentationNode();
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oPresentationNode);
	}, "InvalidControlModelError");
};

ExtJsComboBoxControlTest.prototype.test_comboBoxThrowsExceptionIfBoundToMultiSelectionField = function()
{
	var oMultiSelectionField = new br.presenter.node.MultiSelectionField(["option #1", "option #2"]);
	var oThis = this;
	
	assertException(function() {
		oThis.m_oControlAdaptor.setPresentationNode(oMultiSelectionField);
	}, "InvalidControlModelError");
};
