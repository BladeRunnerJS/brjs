"use strict";

/**
* @module br/knockout/KnockoutComponent
*/

var ko = require( 'ko' );
var br = require( 'br/Core' );
var Errors = require('br/Errors');
var Component = require( 'br/component/Component' );

/**
* @class
* @alias module:br/knockout/KnockoutComponent
* @implements module:br/component/Component
*
* @classdesc
* Constructs a new instance of <code>KnockoutComponent</code>.
*
* @param {String} sTemplateId The id of a template to render the presentation model with.
* @param {Object} oViewModel A Knockout View Model object instance.
*/
function KnockoutComponent(sTemplateId, vPresentationModel) {
  this.m_sTemplateId = sTemplateId;
  this.m_eTemplate = null;
  this.m_oPresentationModel = vPresentationModel;
}
br.implement( KnockoutComponent, Component );

/**
 * @private
 */
KnockoutComponent.TEMPLATE_NOT_FOUND = "TemplateNotFound";

/**
 * @private
 */
KnockoutComponent.TemplateNotFoundError = function(message, filename, lineNumber) {
  Errors.CustomError.call(this, KnockoutComponent.TEMPLATE_NOT_FOUND, message, filename, lineNumber);
};
br.extend(KnockoutComponent.TemplateNotFoundError, Errors.CustomError);

// *********************** Component Interface ***********************

KnockoutComponent.prototype.setDisplayFrame = function(frame) {
  this.m_oFrame = frame;

  frame.on('attach', function() {
    ko.applyBindings(this.m_oPresentationModel, this._getTemplate());
  }.bind(this));

  frame.setContent(this.getElement());
};

KnockoutComponent.prototype.getElement = function() {
  return this._getTemplate();
};

/** 
 * We lazilly get the template element so no elements are loaded if tests don't bind the model to the view 
 * @private
 */
KnockoutComponent.prototype._getTemplate = function() {
	if (!this.m_eTemplate) {
		var eTemplateNode = require('service!br.html-service').getTemplateElement(this.m_sTemplateId);

		if (!eTemplateNode) {
			throw new KnockoutComponent.TemplateNotFoundError("Template with ID '" + this.m_sTemplateId + "' couldn't be found");
		}
		this.m_eTemplate = eTemplateNode;
	}
	return this.m_eTemplate;
};

module.exports = KnockoutComponent;
