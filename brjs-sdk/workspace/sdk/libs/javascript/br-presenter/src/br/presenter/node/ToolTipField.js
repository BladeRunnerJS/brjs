/**
 * @module br/presenter/node/ToolTipField
 */

/**
 * Constructs a new instance of <code>ToolTipField</code>.
 *
 * @description
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model an input field on screen with a tool tip box that will be displayed when
 * a validation error has been produced.
 *
 * <p>The element you are modeling with this field should have as css property the tooltipClassName
 * in order to control wich is the filed on error. This css class will be set by the {@link module:br/presenter/util/ErrorMonitor}.
 * Also, see {@link module:br/presenter/control/tooltip/TooltipControl} on how to model the tool tip box.</p>
 *
 * @class
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link module:br/presenter/property/EditableProperty}.
 * @extends module:br/presenter/node/PresentationNode
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
