/**
 * @module br/presenter/testing/NodeListLengthProperty
 */

/**
 * @class
 * @alias module:br/presenter/testing/NodeListLengthProperty
 * @extends module:br/presenter/property/WritableProperty
 */
br.presenter.testing.NodeListLengthProperty = function(oNodeList)
{
	// call super constructor
	br.presenter.property.WritableProperty.call(this, oNodeList.getPresentationNodesArray().length);
	
	this.m_oNodeList = oNodeList;
	this.addChangeListener(this, "_onPropertyChanged");
};
br.Core.extend(br.presenter.testing.NodeListLengthProperty, br.presenter.property.WritableProperty);

br.presenter.testing.NodeListLengthProperty.prototype._onPropertyChanged = function()
{
	var nNewLength = this.getValue();
	var pNodes = this.m_oNodeList.getPresentationNodesArray().slice(0, nNewLength);
	
	for(var i = pNodes.length; i < nNewLength; ++i)
	{
		var fNodeClass = this.m_oNodeList.m_fPermittedClass;
		pNodes.push(new fNodeClass());
	}
	
	this.m_oNodeList.updateList(pNodes);
};
