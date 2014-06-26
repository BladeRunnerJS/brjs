'use strict';

require('jquery');
require('es5-shim');

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * @name br.test.ViewFixture
 * @class
 * <p>The <code>ViewFixture</code> enables interacting with the rendered view via <code>ViewFixtureHandlers</code>. An 
 *  element in the view can be selected with jQuery selectors. In Given and When phases the selected element in the 
 *  view as well as its desired value will be passed as arguments to the <code>set()</code> method of a 
 *  <code>ViewFixtureHandler</code> which will update the element accordingly. In the Then phase the same arguments 
 *  will be passed to the <code>get()</code> method of a <code>ViewFixtureHandler</code>, which will then inspect the 
 *  selected view element and return a value of a particular property of this element to the <code>ViewFixture</code>. 
 *  The <code>ViewFixture</code> should mainly be used to check that the bindings between view elements in templates 
 *  and the corresponding presentation model properties have been specified correctly. A test might set a value on the 
 *  view element in the Given or When phases and then check in the Then phase that this value has been updated after 
 *  updating the relevant presentation model property.</p>
 *
 * <p>Assuming that the <code>ViewFixture</code> has been added with the identifier <code>view</code> as a subfixture
 * of the <code>ComponentFixture</code> which has the identifier <code>form</code>, then the <code>ViewFixture</code>
 * can be used in the following way in a test:
 * </p>
 * <p>
 * <code>then("form.view.(.orderSummary [identifier=\'orderStatus\']).text = 'complete'");</code>
 * </p>
 * <p>
 * In the above example the jQuery selector for the element in the view is
 *  <code>.spotGeneralSummary [identifier=\'dealSubmittedFor\']</code> and it must be specified within parentheses. The 
 *  following part of the statement, <code>.text = 'test phrase'</code>, specifies the ViewFixtureHandler
 * (<code>Text</code>) and the value (<code>'test phrase'</code>) which will be passed to it. The <code>Text</code>
 * <code>ViewFixtureHandler</code> will then get the text value of the selected view element and return this value to 
 * the <code>ViewFixture</code>. The test will pass if the text value of the selected view element is indeed equal to 
 *  <code>'test phrase'</code>.
 * </p>
 *
 * @implements br.test.Fixture
 *
 * @constructor
 * Constructs a <code>br.test.ViewFixture</code>.
 * @param {String} viewSelector (optional) CSS selector to identify the parent view element for this fixture
 */
br.test.ViewFixture = function(viewSelector) {
	this.m_sViewSelector = viewSelector || null;

	this.m_mViewHandlers = {
		blurred: new require('br/test/viewhandler/Blurred')(),
		checked: new require('br/test/viewhandler/Checked')(),
		childrenCount: new require('br/test/viewhandler/ChildrenCount')(),
		className: new require('br/test/viewhandler/ClassName')(),
		clicked: new require('br/test/viewhandler/Clicked')(),
		backgroundImage: new require('br/test/viewhandler/BackgroundImage')(),
		doesNotHaveClass: new require('br/test/viewhandler/DoesNotHaveClass')(),
		enabled: new require('br/test/viewhandler/Enabled')(),
		focusIn: new require('br/test/viewhandler/FocusIn')(),
		focusOut: new require('br/test/viewhandler/FocusOut')(),
		focused: new require('br/test/viewhandler/Focused')(),
		hasClass: new require('br/test/viewhandler/HasClass')(),
		height: new require('br/test/viewhandler/Height')(),
		isVisible: new require('br/test/viewhandler/IsVisible')(),
		mouseDown: new require('br/test/viewhandler/MouseDown')(),
		mouseMove: new require('br/test/viewhandler/MouseMove')(),
		mouseOut: new require('br/test/viewhandler/MouseOut')(),
		mouseOver: new require('br/test/viewhandler/MouseOver')(),
		mouseUp: new require('br/test/viewhandler/MouseUp')(),
		onKeyUp: new require('br/test/viewhandler/OnKeyUp')(),
		options: new require('br/test/viewhandler/Options')(),
		readonly: new require('br/test/viewhandler/Readonly')(),
		rightClicked: new require('br/test/viewhandler/RightClicked')(),
		scrolledHorizontal: new require('br/test/viewhandler/ScrolledHorizontal')(),
		scrolledVertical: new require('br/test/viewhandler/ScrolledVertical')(),
		selected: new require('br/test/viewhandler/Selected')(),
		text: new require('br/test/viewhandler/Text')(),
		typedValue: new require('br/test/viewhandler/TypedValue')(),
		value: new require('br/test/viewhandler/Value')(),
		width: new require('br/test/viewhandler/Width')(),
		borderWidth: new require('br/test/viewhandler/BorderWidth')(),
		borderColor: new require('br/test/viewhandler/BorderColor')(),
		topMarginWidth: new require('br/test/viewhandler/TopMarginWidth')(),
		bottomMarginWidth: new require('br/test/viewhandler/BottomMarginWidth')(),
		rightMarginWidth: new require('br/test/viewhandler/RightMarginWidth')(),
		leftMarginWidth: new require('br/test/viewhandler/LeftMarginWidth')(),
		color: new require('br/test/viewhandler/Color')(),
		onKeyDown: new require('br/test/viewhandler/OnKeyDown')(),
		top: new require('br/test/viewhandler/Top')()
	};
}
br.inherit(ViewFixture, Fixture);

ViewFixture.prototype.setUp = function() {
	var viewElements;

	if (this.m_sViewSelector) {
		viewElements = jQuery(this.m_sViewSelector);
		this._verifyOnlyOneElementSelected(viewElements, this.m_sViewSelector);
		this.setViewElement(viewElements[0]);
	}
};

ViewFixture.prototype.tearDown = function() {
	this.m_eViewElement = null;

	if (this.m_oBlurHandler) {
		this.m_oBlurHandler.destroy();
	}
};

/**
 * Allows custom view handlers to be added.
 * @param {Map} viewHandlersMap A map of handler name to handler class constructor reference.
 * @throws {br.Errors.InvalidParametersError} If an attempt is made to override an existing handler.
 */
ViewFixture.prototype.addViewHandlers = function(viewHandlersMap) {
	var keys = Object.keys(viewHandlersMap),
		existingHandlers = [];

	keys.forEach(function(key) {
		if (this.m_mViewHandlers.hasOwnProperty(key)) {
			existingHandlers.push('\'' + key + '\'');
			return;
		}
	}, this);

	if (existingHandlers.length > 0) {
		throw new Errors.InvalidParametersError(
			'The following view handlers were not added to the registry as they already exist: ' +
				existingHandlers.join(',')
		);
	}

	keys.forEach(function(key) {
		this.m_mViewHandlers[key] = new (viewHandlersMap[key])();
	}, this);
};

ViewFixture.prototype.setViewElement = function(viewElement) {
	this.m_eViewElement = viewElement;
	this.m_oBlurHandler = new require('br/test/viewhandler/BlurHandler')(viewElement);
};

ViewFixture.prototype.getViewElement = function() {
	return this.m_eViewElement;
};

ViewFixture.prototype.setViewElementWithoutAttachingBlurHandler = function(viewElement) {
	this.m_eViewElement = viewElement;
};

ViewFixture.prototype.setComponent = function(Component) {
	this.m_oComponent = Component;
};

ViewFixture.prototype.getComponent = function() {
	return this.m_oComponent;
};

ViewFixture.prototype.canHandleProperty = function(propertyName) {
	return true;
};

ViewFixture.prototype.canHandleExactMatch = function() {
	return false;
};

ViewFixture.prototype.doGivenAndDoWhen = function(propertyName, value) {
	var handler = this._getHandler(propertyName, value);

	if (handler.property === 'count') {
		throw new Errors.InvalidTestError('The "count" property can only be used in then statements.');
	} else {
		handler.viewFixtureHandler.set(handler.selectedElement, value);
	}
};

ViewFixture.prototype.doGiven = ViewFixture.prototype.doGivenAndDoWhen;
ViewFixture.prototype.doWhen = ViewFixture.prototype.doGivenAndDoWhen;

ViewFixture.prototype.doThen = function(propertyName, Value) {
	var handler = this._getHandler(propertyName, Value);

	if (handler.property === 'count') {
		assertEquals('"count" should be ' + Value, Value, handler.elements.length);
	} else {
		assertEquals(
			'"' + handler.property + '" should be ' + Value,
			Value,
			handler.viewFixtureHandler.get(handler.selectedElement, Value)
		);
	}
};

/** @private */
ViewFixture.prototype._getHandler = function(propertyName, value) {
	var handler = {};

	handler.property = this._getPropertyName(propertyName);
	handler.elements = this._getViewElements(propertyName);

	if (handler.property !== 'count') {
		handler.viewFixtureHandler = this._getViewHandler(handler.property);

		if (handler.elements.length === 1) {
			handler.selectedElement = handler.elements[0];
		} else {
			this._verifyOnlyOneElementSelected(handler.elements, handler.property);
		}
	}

	return handler;
};

/** @private */
ViewFixture.prototype._getPropertyName = function (propertyName) {
	return propertyName.match(/[^\.]*$/)[0];
};

/** @private */
ViewFixture.prototype._getViewElements = function(propertyName) {
	var selector = propertyName.match(/\((.*)\)\.[^.]+/)[1];
	return jQuery(this.m_eViewElement).find(selector);
};

/** @private */
ViewFixture.prototype._verifyOnlyOneElementSelected = function(elements, selector) {
	if (elements.length === 0) {
		throw 'No view element found for "' + selector + '"';
	} else if (elements.length > 1) {
		throw 'More than one view element found for "' + selector + '"';
	}
};

ViewFixture.prototype._getViewHandler = function(propertyName) {
	var handler = this.m_mViewHandlers[propertyName];

	if (!handler) {
		throw new Errors.InvalidTestError('Undefined view fixture handler "' + propertyName + '"');
	}

	return handler;
};

module.exports = ViewFixture;
