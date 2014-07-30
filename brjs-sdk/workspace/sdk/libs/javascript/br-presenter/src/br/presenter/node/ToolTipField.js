/**
 * Constructs a new instance of <code>ToolTipField</code>.
 *
 * @class
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model an input field on screen with a tool tip box that will be displayed when
 * a validation error has been produced.
 *
 * <p>The element you are modeling with this field should have as css property the tooltipClassName
 * in order to control wich is the filed on error. This css class will be set by the {@link br.presenter.util.ErrorMonitor}.
 * Also, see {@link br.presenter.control.tooltip.TooltipControl} on how to model the tool tip box.</p>
 *
 * @constructor
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link br.presenter.property.EditableProperty}.
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.node.ToolTipField = function(vValue)
{
	br.presenter.node.Field.call(this, vValue);
	this.tooltipClassName = new br.presenter.property.WritableProperty(false);

	this.hasToolTip = new br.presenter.property.WritableProperty(false);

	var tooltipClassNamePropertyListener = new br.presenter.property.PropertyListener();
	tooltipClassNamePropertyListener.onPropertyChanged = function() {
		this.hasToolTip.setValue(this.tooltipClassName.getValue() !== "")
	}.bind(this);
	this.tooltipClassName.addListener(tooltipClassNamePropertyListener, true);
};
br.Core.extend(br.presenter.node.ToolTipField, br.presenter.node.Field);
