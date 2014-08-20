'use strict';

/**
 * @module br/test/viewhandler/BlurHandler
 */

require('jquery');

/**
 * @private
 * @class
 * @alias module:br/test/viewhandler/BlurHandler
 */
function BlurHandler(eViewElement) {
	this.m_fOnBlur = this._onBlur.bind(this);
	this.m_eViewElement = eViewElement;
	this.m_sOnBlurListenerId = jQuery(this.m_eViewElement).on("blur", this.m_fOnBlur);
};

BlurHandler.prototype.destroy = function() {
	jQuery(this.m_eViewElement).off("blur", this.m_fOnBlur);
};

BlurHandler.prototype._onBlur = function(oEvent) {
	var eElement = oEvent.target || oEvent.srcElement;
	
	if (eElement.fireOnChange) {
		delete eElement.fireOnChange;
		jQuery(eElement).trigger("change");
	}
};

module.exports = BlurHandler;
