caplinx.dashboard.app.model.dialog.DialogViewNode = function(sTemplateId)
{
	// call super constructor
	caplin.presenter.node.TemplateNode.call(this, "caplinx.dashboard.app." + sTemplateId);
	
	this.isClosable = new caplin.presenter.property.EditableProperty(true);
	this.hasBackground = new caplin.presenter.property.EditableProperty(true);
	
};
caplin.extend(caplinx.dashboard.app.model.dialog.DialogViewNode, caplin.presenter.node.TemplateNode);

caplinx.dashboard.app.model.dialog.DialogViewNode.prototype.initializeForm = function()
{
	caplin.core.Utility.interfaceMethod("DialogViewNode", "initializeForm");
};

