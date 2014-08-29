/**
 * @module br/presenter/workbench/model/TreeViewerPM
 */


/**
 * @private
 * @class
 * @alias module:br/presenter/workbench/model/TreeViewerPM
 */
br.presenter.workbench.model.TreeViewerPM = function(oSearchTarget)
{
	this.searchText = new br.presenter.property.EditableProperty();
	this.m_oSearchTarget = oSearchTarget
};

br.Core.extend(br.presenter.workbench.model.TreeViewerPM, br.presenter.PresentationModel);

br.presenter.workbench.model.TreeViewerPM.prototype.close = function()
{
	this.m_oSearchTarget.close();
}
	
br.presenter.workbench.model.TreeViewerPM.prototype.search = function()
{
	var vValue = this.searchText.getValue();
	this.m_oSearchTarget.search(vValue);
};
