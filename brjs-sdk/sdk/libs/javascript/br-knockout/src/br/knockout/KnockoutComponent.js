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
  this.m_eTemplate = this._getTemplate(sTemplateId);
  this.m_oPresentationModel = vPresentationModel;
  this.m_bViewBound = false;
  this.m_bViewAttached = false;
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

  frame.setContent(this.getElement());
};

KnockoutComponent.prototype.getElement = function() {
  if (!this.m_bViewBound) {
    this.m_bViewBound = true;
    ko.applyBindings(this.m_oPresentationModel, this.m_eTemplate);
  }

  return this.m_eTemplate;
};

/** @private */
KnockoutComponent.prototype._getTemplate = function(sTemplateId) {
	var eTemplateHolder;
	var eTemplateNode = require('service!br.html-service').getHTMLTemplate(sTemplateId);

	if (!eTemplateNode) {
	    throw new KnockoutComponent.TemplateNotFoundError("Template with ID "+sTemplateId+" couldn't be found");
	}

	eTemplateHolder = eTemplateNode.cloneNode(true);
	eTemplateHolder.removeAttribute('id');

	return eTemplateHolder;
};

module.exports = KnockoutComponent;
