/**
 * @module br/presenter/control/tooltip/DefaultTooltipHelper
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/control/tooltip/DefaultTooltipHelper
 */
br.presenter.control.tooltip.DefaultTooltipHelper = function()
{
	this.m_eContainer = document.createElement('div');
	this.m_eContainer.className = "tooltip-container";
	this.m_eMessage = document.createElement('div');
	this.m_eMessage.className = "tooltip-content";
	this.m_eContainer.appendChild(this.m_eMessage);

	this.m_vParent = document.body;
};
br.Core.implement(br.presenter.control.tooltip.DefaultTooltipHelper, br.presenter.control.tooltip.TooltipHelper);

/**
 * @private
 */
br.presenter.control.tooltip.DefaultTooltipHelper.prototype.updateTooltip = function(sMessage)
{
	this.m_eMessage.textContent = sMessage;
	return this;
};

/**
 * @private
 */
br.presenter.control.tooltip.DefaultTooltipHelper.prototype.containWithin = function(oContainer)
{
	this.m_vParent = oContainer;
	return this;
};

/**
 * @private
 */
br.presenter.control.tooltip.DefaultTooltipHelper.prototype.pointTo = function(eErrorElement)
{
	if(eErrorElement.length > 1) {
		return this;
	}
	var eField = eErrorElement[0];

	var oTooltipDimensions = this._getTooltipDimensions();
	var oTooltipProperties = {};

	oTooltipProperties.left = eField.offsetLeft + eField.offsetWidth / 2 - oTooltipDimensions.width / 2;
	oTooltipProperties.top  = eField.offsetTop + eField.offsetHeight * 1.5;

	br.util.ElementUtility.addClassName(this.m_eContainer, 'tooltip-visible');
	this.m_eContainer.style.left = oTooltipProperties.left + "px";
	this.m_eContainer.style.top = oTooltipProperties.top + "px";
	this.m_vParent.appendChild(this.m_eContainer);

	return this;
};

/**
 * @private
 */
br.presenter.control.tooltip.DefaultTooltipHelper.prototype.remove = function()
{
	this.m_vParent.removeChild(this.m_eContainer);
};

/**
 * @private
 */
/*
 * Gets the dimensions of the tooltip, assuming the current content will not be changed. Do not call this function
 * in quick succession as it requires some heavy DOM lifting by the browser.
 */
br.presenter.control.tooltip.DefaultTooltipHelper.prototype._getTooltipDimensions = function()
{
	var eClone = this.m_eContainer.cloneNode(true);
	eClone.style.visibility = "hidden";
	eClone.style.position = "absolute";
	eClone.style.display = "block";
	this.m_vParent.appendChild(eClone);

	var oDimensions = {
		height: eClone.offsetHeight,
		width: eClone.offsetWidth
	};

	this.m_vParent.removeChild(eClone);
	return oDimensions;
};
