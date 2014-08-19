'use strict';

/**
 * @module br/test/ViewFixture
 */

/**
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
* @module br/test/ViewFixture
*/

require('jquery');
require('es5-shim');

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * Constructs a <code>br.test.ViewFixture</code>.
 * @implements module:br/test/Fixture
 * @alias module:br/test/ViewFixture
 * @class
 * @param {String} viewSelector (optional) CSS selector to identify the parent view element for this fixture
 */
function ViewFixture(viewSelector) {
	this.m_sViewSelector = viewSelector || null;

	var Blurred = require('br/test/viewhandler/Blurred');
	var Checked = require('br/test/viewhandler/Checked');
	var ChildrenCount = require('br/test/viewhandler/ChildrenCount');
	var ClassName = require('br/test/viewhandler/ClassName');
	var Clicked = require('br/test/viewhandler/Clicked');
	var BackgroundImage = require('br/test/viewhandler/BackgroundImage');
	var DoesNotHaveClass = require('br/test/viewhandler/DoesNotHaveClass');
	var Enabled = require('br/test/viewhandler/Enabled');
	var FocusIn = require('br/test/viewhandler/FocusIn');
	var FocusOut = require('br/test/viewhandler/FocusOut');
	var Focused = require('br/test/viewhandler/Focused');
	var HasClass = require('br/test/viewhandler/HasClass');
	var Height = require('br/test/viewhandler/Height');
	var IsVisible = require('br/test/viewhandler/IsVisible');
	var MouseDown = require('br/test/viewhandler/MouseDown');
	var MouseMove = require('br/test/viewhandler/MouseMove');
	var MouseOut = require('br/test/viewhandler/MouseOut');
	var MouseOver = require('br/test/viewhandler/MouseOver');
	var MouseUp = require('br/test/viewhandler/MouseUp');
	var OnKeyUp = require('br/test/viewhandler/OnKeyUp');
	var Options = require('br/test/viewhandler/Options');
	var Readonly = require('br/test/viewhandler/Readonly');
	var RightClicked = require('br/test/viewhandler/RightClicked');
	var ScrolledHorizontal = require('br/test/viewhandler/ScrolledHorizontal');
	var ScrolledVertical = require('br/test/viewhandler/ScrolledVertical');
	var Selected = require('br/test/viewhandler/Selected');
	var Text = require('br/test/viewhandler/Text');
	var TypedValue = require('br/test/viewhandler/TypedValue');
	var Value = require('br/test/viewhandler/Value');
	var Width = require('br/test/viewhandler/Width');
	var BorderWidth = require('br/test/viewhandler/BorderWidth');
	var BorderColor = require('br/test/viewhandler/BorderColor');
	var TopMarginWidth = require('br/test/viewhandler/TopMarginWidth');
	var BottomMarginWidth = require('br/test/viewhandler/BottomMarginWidth');
	var RightMarginWidth = require('br/test/viewhandler/RightMarginWidth');
	var LeftMarginWidth = require('br/test/viewhandler/LeftMarginWidth');
	var Color = require('br/test/viewhandler/Color');
	var OnKeyDown = require('br/test/viewhandler/OnKeyDown');
	var Top = require('br/test/viewhandler/Top');

	this.m_mViewHandlers = {
		blurred: new Blurred(),
		checked: new Checked(),
		childrenCount: new ChildrenCount(),
		className: new ClassName(),
		clicked: new Clicked(),
		backgroundImage: new BackgroundImage(),
		doesNotHaveClass: new DoesNotHaveClass(),
		enabled: new Enabled(),
		focusIn: new FocusIn(),
		focusOut: new FocusOut(),
		focused: new Focused(),
		hasClass: new HasClass(),
		height: new Height(),
		isVisible: new IsVisible(),
		mouseDown: new MouseDown(),
		mouseMove: new MouseMove(),
		mouseOut: new MouseOut(),
		mouseOver: new MouseOver(),
		mouseUp: new MouseUp(),
		onKeyUp: new OnKeyUp(),
		options: new Options(),
		readonly: new Readonly(),
		rightClicked: new RightClicked(),
		scrolledHorizontal: new ScrolledHorizontal(),
		scrolledVertical: new ScrolledVertical(),
		selected: new Selected(),
		text: new Text(),
		typedValue: new TypedValue(),
		value: new Value(),
		width: new Width(),
		borderWidth: new BorderWidth(),
		borderColor: new BorderColor(),
		topMarginWidth: new TopMarginWidth(),
		bottomMarginWidth: new BottomMarginWidth(),
		rightMarginWidth: new RightMarginWidth(),
		leftMarginWidth: new LeftMarginWidth(),
		color: new Color(),
		onKeyDown: new OnKeyDown(),
		top: new Top()
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
	var BlurHandler = require('br/test/viewhandler/BlurHandler');

	this.m_eViewElement = viewElement;
	this.m_oBlurHandler = new BlurHandler(viewElement);
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
