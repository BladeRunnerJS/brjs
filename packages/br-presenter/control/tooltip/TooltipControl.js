'use strict';

var ElementUtility = require('br-util/ElementUtility');
var AliasRegistry = require('br/AliasRegistry');
var ControlAdaptor = require('br-presenter/control/ControlAdaptor');
var Core = require('br/Core');
var PropertyHelper = require('br-presenter/property/PropertyHelper');

/**
 * @module br/presenter/control/tooltip/TooltipControl
 */

/**
 * @class
 * @alias module:br/presenter/control/tooltip/TooltipControl
 * @implements module:br/presenter/control/ControlAdaptor
 *
 * @classdesc
 * A presenter control that places a tooltip next to the field defined by the  {@link module:br/presenter/node/ToolTipNode}.
 * This class is constructed by presenter automatically on your behalf.
 *
 * <p>The TooltipControl must be used with a {@link module:br/presenter/node/ToolTipNode}.
 * This presentation node represents the view element which will be the container of the tooltip box.
 * This means, all the fields in which a tooltip will be displayed must be contained inside this
 * element. Unlike a normal tooltip this automatically displays beside the specified field without the user having to hover over it.</p>
 *
 * <p>The TooltipControl is aliased by <em>br.tooltip</em>, and can be used within templates
 * as follows:</p>
 *
 * <pre>
 *   &lt;div data-bind="control:'br.tooltip', controlNode:tooltipNode"&gt;
 *   	&lt;input class="tooltip-field1" /&gt;
 *   	&lt;input class="tooltip-field2" /&gt;
 *   &lt;/div&gt;
 * </pre>
 *
 * <p>Keep in mind that only one html element must have the tooltip css class at a time. This is why
 * we also provide a {@link module:br/presenter/util/ErrorMonitor} to control several {@link br/presenter/node/TooltipField}
 * an deal with this. We recommend the use of this class for {@link module:br/presenter/node/TooltipField}
 * monitoring.</p>
 */
function TooltipControl() {
	this.m_oPropertyHelper = new PropertyHelper();
	this.m_eNode = null;
	this.m_oTooltip = null;
	this.m_oPresentationNode = null;
}

Core.inherit(TooltipControl, ControlAdaptor);

TooltipControl.prototype.destroy = function() {
	this._removeTooltip();
	this.m_oPropertyHelper.removeAllListeners();

	this.m_oPresentationNode = null;
};

TooltipControl.prototype.setElement = function(eElement) {
	this.m_eNode = eElement;
};

TooltipControl.prototype.setPresentationNode = function(oPresentationNode) {
	this.m_oPresentationNode = oPresentationNode;

	this.m_oPropertyHelper.addChangeListener(oPresentationNode.hasMoved, this, 'onTooltipMoved');
};

TooltipControl.prototype.onTooltipMoved = function() {
	if (this.m_oPresentationNode.hasMoved.getValue()) {
		this._addTooltip(this.m_oPresentationNode.message.getValue());
	} else {
		this._removeTooltip();
	}
};

TooltipControl.prototype._addTooltip = function(sFailureMessage) {
	var TooltipHelper = require('alias!br.presenter.tooltip-helper');
	this._removeTooltip();

	var ePointTo = ElementUtility.getElementsByClassName(this.m_eNode, '*', this.m_oPresentationNode.getTooltipClassName());
	this.m_oTooltip = new TooltipHelper()
		.updateTooltip(sFailureMessage)
		.containWithin(this.m_eNode)
		.pointTo(ePointTo);
};

TooltipControl.prototype._removeTooltip = function() {
	if (this.m_oTooltip) {
		this.m_oTooltip.remove();
		delete this.m_oTooltip;
	}
};

TooltipControl.prototype.setOptions = function(mOptions) {};

TooltipControl.prototype.updateEnabled = function() {};

module.exports = TooltipControl;
