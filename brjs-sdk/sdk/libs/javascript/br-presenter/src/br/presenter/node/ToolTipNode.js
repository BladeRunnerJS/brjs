'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');

/**
 * @module br/presenter/node/ToolTipNode
 */

/**
 * @class
 * @alias module:br/presenter/node/ToolTipNode
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * <code>ToolTipNode</code> is used as the underlying model for
 * {@link module:br/presenter/control/tooltip/TooltipControl}
 * 
 * @param {String} tooltipClassname Css class name that the {@link module:br/presenter/control/tooltip/TooltipControl} will scan for rendering the tool tip box on.
 */
function ToolTipNode(tooltipClassname) {
	this.m_sTooltipClassName = tooltipClassname || 'has-tooltip';

	this.message = new EditableProperty('');

	this.hasMoved = new EditableProperty('');
}

Core.extend(ToolTipNode, PresentationNode);

ToolTipNode.prototype.setMessage = function(sMessage) {
	this.message.setValue(sMessage);
};

ToolTipNode.prototype.move = function(bMove) {
	this.hasMoved.setValue(bMove);
};

ToolTipNode.prototype.getTooltipClassName = function() {
	return this.m_sTooltipClassName;
};

module.exports = ToolTipNode;
