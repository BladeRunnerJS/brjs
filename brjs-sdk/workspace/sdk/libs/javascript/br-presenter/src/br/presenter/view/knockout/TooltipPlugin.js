/**
 * @module br/presenter/view/knockout/TooltipPlugin
 */

// TODO: TAT - this class needs testing

br.Core.thirdparty("jquery");
br.Core.thirdparty("presenter-knockout");

/**
 * @private
 * @class
 */
br.presenter.view.knockout.TooltipPlugin = function()
{
	var sTooltipHtml = '<div class="ui-tooltip cpl-tooltip qtip ui-helper-reset ui-tooltip-default ui-tooltip-red ui-tooltip-validation ui-tooltip-focus ui-tooltip-pos-bc"><div class="ui-tooltip-tip" ></div><div class="ui-tooltip-content"></div></div>';
	br.presenter.view.knockout.TooltipPlugin.element = jQuery( sTooltipHtml );
	jQuery("body").append( br.presenter.view.knockout.TooltipPlugin.element );
	br.presenter.view.knockout.TooltipPlugin._removeTooltip(); // hide tooltip
	jQuery(document).click(br.presenter.view.knockout.TooltipPlugin._removeTooltip);
};

/** @private */
br.presenter.view.knockout.TooltipPlugin.prototype.init = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel)
{
	eElement = jQuery(eElement);
	var oField = fAllBindingsAccessor().tooltip;
	
	eElement.bind("focusin", {eElement: eElement, oField: oField}, br.presenter.view.knockout.TooltipPlugin._onFocusIn );
	eElement.bind("focusout", br.presenter.view.knockout.TooltipPlugin._onFocusOut );

	eElement.click(br.presenter.view.knockout.TooltipPlugin.m_fClickFunction);
};

br.presenter.view.knockout.TooltipPlugin.prototype.update = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel){};

br.presenter.view.knockout.TooltipPlugin._onFocusIn = function(oEvent) {
	if( oEvent.data.oField &&  oEvent.data.oField.hasError 
			&& oEvent.data.oField.hasError.getValue() === true )
	{	
		var vMessage = oEvent.data.oField.failureMessage.getValue();
		br.presenter.view.knockout.TooltipPlugin._showToolTip( oEvent.data.eElement, oEvent.target, vMessage );
	}
};

br.presenter.view.knockout.TooltipPlugin._onFocusOut = function(oEvent) {
	br.presenter.view.knockout.TooltipPlugin._removeTooltip();
};

br.presenter.view.knockout.TooltipPlugin._showToolTip = function(eElement, eContainingElement, vMessage)
{
	br.presenter.view.knockout.TooltipPlugin.element.show().find(".ui-tooltip-content").html(vMessage);

	var containingElementPosition = jQuery(eContainingElement).offset();

	var bodyMarginTop = parseInt(bodyMargin = jQuery("body").css("margin-top"));
	var bodyMarginLeft = parseInt(bodyMargin = jQuery("body").css("margin-left"));
	bodyMarginTop = isNaN(bodyMarginTop) ? 0 : bodyMarginTop;
	bodyMarginLeft = isNaN(bodyMarginLeft) ? 0 : bodyMarginLeft;
	
	var top = containingElementPosition.top - bodyMarginTop - (8 + br.presenter.view.knockout.TooltipPlugin.element.outerHeight());
	var left = containingElementPosition.left - bodyMarginLeft +  jQuery(eContainingElement).outerWidth() / 2 - br.presenter.view.knockout.TooltipPlugin.element.outerWidth() / 2;

	br.presenter.view.knockout.TooltipPlugin.element.css({
		display:"block",
		position: "absolute",
		top: top,
		left: left < 10 ? 10 : left
	});

	br.presenter.view.knockout.TooltipPlugin.element[0].style.opacity = 1;
};

br.presenter.view.knockout.TooltipPlugin._removeTooltip = function(event)
{
	br.presenter.view.knockout.TooltipPlugin.element.hide();
	br.presenter.view.knockout.TooltipPlugin.element[0].style.opacity = 0;
};

br.presenter.view.knockout.TooltipPlugin.m_fClickFunction = function(e)
{
	e.stopPropagation(); return false;
};
