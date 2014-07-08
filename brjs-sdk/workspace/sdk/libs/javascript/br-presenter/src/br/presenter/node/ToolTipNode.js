/**
 * ToolTipNode is used as the underlying model for
 * {@link br.presenter.control.tooltip.TooltipControl}
 *
 * @constructor
 * @param {String} tooltipClassname Css class name that the {@link br.presenter.control.tooltip.TooltipControl} will scan for rendering the tool tip box on.
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.node.ToolTipNode = function(tooltipClassname)
{
	this.m_sTooltipClassName = tooltipClassname || "has-tooltip";

	this.message = new br.presenter.property.EditableProperty("");

	this.hasMoved = new br.presenter.property.EditableProperty("");
};
br.Core.extend(br.presenter.node.ToolTipNode, br.presenter.node.PresentationNode);

br.presenter.node.ToolTipNode.prototype.setMessage = function(sMessage)
{
	this.message.setValue(sMessage);
}

br.presenter.node.ToolTipNode.prototype.move = function(bMove)
{
	this.hasMoved.setValue(bMove);
}

br.presenter.node.ToolTipNode.prototype.getTooltipClassName = function()
{
	return this.m_sTooltipClassName;
}
