'use strict';

var PresentationModelFixture = require('br/presenter/testing/PresentationModelFixture');
var ComponentFixture = require('br/component/testing/ComponentFixture');
var Errors = require('br/Errors');
var Core = require('br/Core');

/**
 * @module br/presenter/testing/PresenterComponentFixture
 */

var presenter_knockout = require('presenter-knockout');

/**
 * Constructs a <code>br.presenter.testing.PresenterComponentFixture</code>.
 * 
 * @class
 * @alias module:br/presenter/testing/PresenterComponentFixture
 * @extends module:br/component/testing/ComponentFixture
 * 
 * @classdesc
 * The <code>PresenterComponentFixture</code> serves to create presenter components in order to test the 
 * component behavior.
 * 
 * <p>Tests may use the <code>PresenterComponentFixture</code> to:</p>
 * 
 * <ul> 
 * 	<li>create a presenter component to test the model behavior:</br>
 * 		<code>
 *  	given("component.opened = true")<br>
 *  	and(component presentation model is in state A)<br>
 *  	when(component presentation model is modified)<br>
 *  	then(component presentation model is in new state B)<br>
 * 		</code>
 * 	</li>
 * 	<li>create a presenter component and bind it (using knockout) to the view template, to test the model and view behavior:</br>
 * 		<code>
 *  	given("component.viewOpened = true")<br>
 *  	and(component presentation model is in state A)<br>
 *  	and(component view is in state X)<br>
 *  	when(button clicked on component view)<br>
 *  	then(component presentation model is in new state B)<br>
 *  	and(component view is in new state Y)<br>
 * 		</code>
 * 	</li>
 * </ul>
 * 
 * @param {String} sTemplateId the HTML template id representing the view of the presenter component. Required, not-null.
 * @param {String} sPresentationModel the presentation model class name for the presenter component. Required, not-null.
 */
function PresenterComponentFixture(sTemplateId, sPresentationModel) {
	if (!sTemplateId) {
		throw new Errors.InvalidParametersError('PresenterComponentFixture must be provided with a view template id');
	}
	if (!sPresentationModel) {
		throw new Errors.InvalidParametersError('PresenterComponentFixture must be provided with a presentation model');
	}

	var sPresenterComponentXML = '<br.presenter.component.PresenterComponent templateId="' + sTemplateId + '" presentationModel="' + sPresentationModel + '"></br.presenter.component.PresenterComponent>';

	// call super constructor
	ComponentFixture.call(this, sPresenterComponentXML,
		new PresentationModelFixture());

	/**
	 * @private
	 */
	this.m_fBindPresentationModel = presenter_knockout.applyBindings;
}


Core.extend(PresenterComponentFixture, ComponentFixture);

/**
 * PresenterComponentFixture handles properties 'opened' and 'viewOpened'.
 * 
 * @param {String} sProperty The property to check.
 * 
 * @see br.test.Fixture#canHandleProperty
 */
PresenterComponentFixture.prototype.canHandleProperty = function(sProperty) {
	return (sProperty == 'opened') || (sProperty == 'viewOpened');
};

/**
 * This method creates the presenter component (if property = 'opened') and binds it to the view template (if 
 * property = 'viewOpened') using the references to the template Id and presentation model provided in the 
 * constructor.
 * 
 * @param {String} sProperty The property name
 * @param {Variant} vValue The value to check.
 * 
 * @see br.test.Fixture#doGiven
 */
PresenterComponentFixture.prototype.doGiven = function(sProperty, vValue) {
	if (!(sProperty == 'opened' || sProperty == 'viewOpened')) {
		throw new Errors.CustomError(Errors.INVALID_TEST, "PresenterComponentFixture only supports 'opened' or 'viewOpened' as property-value pairs.");
	}


	if (sProperty == 'opened') {
		presenter_knockout.applyBindings = function() {};
	}

	try {
		// call super method
		ComponentFixture.prototype.doGiven.call(this, 'opened', vValue);
	} finally {
		presenter_knockout.applyBindings = this.m_fBindPresentationModel;
	}
};


module.exports = PresenterComponentFixture;


