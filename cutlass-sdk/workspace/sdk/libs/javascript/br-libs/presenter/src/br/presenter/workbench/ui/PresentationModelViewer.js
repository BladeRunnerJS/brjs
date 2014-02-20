/**
 * @implements br.workbench.ui.WorkbenchComponent
 */
br.presenter.workbench.ui.PresentationModelViewer = function(oPresentationModel)
{
	if (!oPresentationModel)
	{
		throw "PresentationModelViewer expects a presentation model";
	}
	this.m_PresentationModel = oPresentationModel;
	this.m_oTree = new br.presenter.workbench.ui.PresentationModelTree(oPresentationModel);
	
	this.m_eElement = document.createElement("div");
	br.util.ElementUtility.addClassName(this.m_eElement, "presentation-model-viewier");
	
	this.m_eElement.appendChild(this._getFormElement());
	this.m_eElement.appendChild(this.m_oTree.getElement());
};

br.Core.implement(br.presenter.workbench.ui.PresentationModelViewer, br.workbench.ui.WorkbenchComponent);

br.presenter.workbench.ui.PresentationModelViewer.prototype._getFormElement = function()
{
	this.m_PresentationModel = new br.presenter.workbench.model.TreeViewerPM(this);
	this.m_oComponent = new br.presenter.component.PresenterComponent("br.presenter.tree-viewer", this.m_PresentationModel);
	var eElement = this.m_oComponent.getElement();
	
	return eElement;	
};

br.presenter.workbench.ui.PresentationModelViewer.prototype.getElement = function()
{
	return this.m_eElement;
};

br.presenter.workbench.ui.PresentationModelViewer.prototype.search = function(sValue)
{
	this.m_oTree.search(sValue);
};

br.presenter.workbench.ui.PresentationModelViewer.prototype.close = function()
{
	this.m_eElement.parentNode.removeChild(this.m_eElement);
	this.m_oComponent.onClose();
};
