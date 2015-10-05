'use strict';

var jQuery = require('jquery');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/TooltipPlugin
 */

// TODO: TAT - this class needs testing

var jQuery = require('jquery');
var presenter_knockout = require('presenter-knockout');

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/TooltipPlugin
 */
function TooltipPlugin() {
	var sTooltipHtml = '<div class="ui-tooltip cpl-tooltip qtip ui-helper-reset ui-tooltip-default ui-tooltip-red ui-tooltip-validation ui-tooltip-focus ui-tooltip-pos-bc"><div class="ui-tooltip-tip" ></div><div class="ui-tooltip-content"></div></div>';
	TooltipPlugin.element = jQuery(sTooltipHtml);
	jQuery('body').append(TooltipPlugin.element);
	TooltipPlugin._removeTooltip(); // hide tooltip
	jQuery(document).click(TooltipPlugin._removeTooltip);
}

/** @private */
TooltipPlugin.prototype.init = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel) {
	eElement = jQuery(eElement);
	var oField = fAllBindingsAccessor().tooltip;

	eElement.bind('focusin', {
		eElement: eElement,
		oField: oField
	}, TooltipPlugin._onFocusIn);
	eElement.bind('focusout', TooltipPlugin._onFocusOut);

	eElement.click(TooltipPlugin.m_fClickFunction);
};

TooltipPlugin.prototype.update = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel) {};

TooltipPlugin._onFocusIn = function(oEvent) {
	if (oEvent.data.oField && oEvent.data.oField.hasError
		&& oEvent.data.oField.hasError.getValue() === true) {
		var vMessage = oEvent.data.oField.failureMessage.getValue();
		TooltipPlugin._showToolTip(oEvent.data.eElement, oEvent.target, vMessage);
	}
};

TooltipPlugin._onFocusOut = function(oEvent) {
	TooltipPlugin._removeTooltip();
};

TooltipPlugin._showToolTip = function(eElement, eContainingElement, vMessage) {
	TooltipPlugin.element.show().find('.ui-tooltip-content').html(vMessage);

	var containingElementPosition = jQuery(eContainingElement).offset();

	var bodyMarginTop = parseInt(jQuery('body').css('margin-top'));
	var bodyMarginLeft = parseInt(jQuery('body').css('margin-left'));
	bodyMarginTop = isNaN(bodyMarginTop) ? 0 : bodyMarginTop;
	bodyMarginLeft = isNaN(bodyMarginLeft) ? 0 : bodyMarginLeft;

	var top = containingElementPosition.top - bodyMarginTop - (8 + TooltipPlugin.element.outerHeight());
	var left = containingElementPosition.left - bodyMarginLeft + jQuery(eContainingElement).outerWidth() / 2 - TooltipPlugin.element.outerWidth() / 2;

	TooltipPlugin.element.css({
		display: 'block',
		position: 'absolute',
		top: top,
		left: left < 10 ? 10 : left
	});

	TooltipPlugin.element[0].style.opacity = 1;
};

TooltipPlugin._removeTooltip = function(event) {
	TooltipPlugin.element.hide();
	TooltipPlugin.element[0].style.opacity = 0;
};

TooltipPlugin.m_fClickFunction = function(e) {
	e.stopPropagation(); return false;
};

module.exports = TooltipPlugin;
