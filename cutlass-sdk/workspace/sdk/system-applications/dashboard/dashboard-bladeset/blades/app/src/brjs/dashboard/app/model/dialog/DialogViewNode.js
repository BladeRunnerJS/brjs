brjs.dashboard.app.model.dialog.DialogViewNode = function(sTemplateId)
{
	// call super constructor
	br.presenter.node.TemplateNode.call(this, "brjs.dashboard.app." + sTemplateId);
	
	this.isClosable = new br.presenter.property.EditableProperty(true);
	this.hasBackground = new br.presenter.property.EditableProperty(true);
	
};
br.Core.extend(brjs.dashboard.app.model.dialog.DialogViewNode, br.presenter.node.TemplateNode);

brjs.dashboard.app.model.dialog.DialogViewNode.prototype.initializeForm = function()
{
	throw new Error("DialogViewNode.initializeForm not implemented.");
};

