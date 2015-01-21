/**
 * @module br/component/testing/ComponentFixture
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');
var ComponentFrame, ComponentFrameFixture, ComponentModelFixture, Frame, AliasRegistry, ViewFixture, Utility;

/**
 * @class
 * @alias module:br/component/testing/ComponentFixture
 * @implements module:br/test/Fixture
 * 
 * @classdesc
 * Constructs a <code>ComponentFixture</code>.
 * 
 * The <code>ComponentFixture</code> serves to create components using the ComponentFactory when these are
 * required in the system under test.
 *
 * <p>In addition to creating and opening the component, the ComponentFixture defines several sub-fixtures
 * to be added to the test runner, enabling the testing and manipulation of the view and presentation model
 * of the component.</p>
 * 
 * @param {String} sXml the component XML required to create the component. Required.
 * @param {module:br/component/testing/ComponentModelFixture} oModelFixture the presentation model fixture. Required.
 * @param {module:br/test/ViewFixture} oViewFixture the view fixture. Optional.
 */
function ComponentFixture(sXml, oModelFixture, oViewFixture) {
	//TODO: This check should be an isA instead of a fulfills.
	if (!sXml || !oModelFixture || !(br.fulfills(oModelFixture, ComponentModelFixture))) {
		throw new Errors.InvalidParametersError("The ComponentFixture must be provided with the component XML " +
				"and with a valid presentation model fixture which is an instance of " +
				"br.component.testing.ComponentModelFixture.");
	}

	/**
	 * @private
	 */
	this.m_sXml = sXml;

	/**
	 * @private
	 */
	this.m_oModelFixture = oModelFixture;
	/**
	 * @private
	 */
	this.m_oViewFixture = oViewFixture || new ViewFixture();

	// TODO: find out why we need this as setUp() should be called before the test starts anyway
	this.setUp();

	this.m_fOnOpenCallback = null;
};

br.implement(ComponentFixture, Fixture);

/**
 * Upon set-up of the ComponentFixture, the ComponentFactory is configured not to create an ErrorComponent. If
 * no component can be created with the given XML configuration, an exception will be thrown instead.
 *
 * @see br.test.Fixture#setUp
 */
ComponentFixture.prototype.setUp = function() {
	/**
	 * @private
	 */
	this.m_oComponentFrame = new ComponentFrame(this);
	/**
	 * @private
	 */
	this.m_oComponentFrameFixture = new ComponentFrameFixture(this.m_oComponentFrame);
};

/**
 * Upon tear-down of the ComponentFixture, the component created is closed and the ComponentFactory is re-configured
 * to its original settings.
 *
 * @see br.test.Fixture#tearDown
 */
ComponentFixture.prototype.tearDown = function() {
	if (this.m_oComponent) {
		this.m_oComponent.onClose();
		this.m_oComponent = null;
	}
	this.m_oComponentFrame = null;
	this.m_oComponentFrameFixture = null;
};

/**
 * Allows custom view handlers to be added. The ViewFixture's addViewHandlers method throws an exception if an attempt is
 *  made to override an existing handler.
 *
 * @param {Map} viewHandlersMap A map of handler name to handler class constructor reference
 */
ComponentFixture.prototype.addViewFixtureHandlers = function(viewHandlersMap) {
	this.m_oViewFixture.addViewHandlers(viewHandlersMap);
};

/**
 * Set the selector mappings to use with the view fixture. This method proxies the call to the
 *  {@link module:br/test/ViewFixture#setSelectorMappings} method.
 * @param {Object} selectorMappings Map of selector mappings.
 */
ComponentFixture.prototype.setSelectorMappings = function(selectorMappings) {
	this.m_oViewFixture.setSelectorMappings(selectorMappings);
};

/**
 * ComponentFixture handles the 'opened' property.
 *
 * @param {String} sProperty name of the property
 * @see br.test.Fixture#canHandleProperty
 *
 * @type boolean
 */
ComponentFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty == "opened";
};

/**
 * This method creates and opens the component created, and sets it on the presentation model and
 * view sub-fixtures so that tests may manipulate model properties and the view elements.
 *
 * @param {String} sProperty name of the property
 * @param {String} vValue value of the property
 *
 * @see br.test.Fixture#doGiven
 */
ComponentFixture.prototype.doGiven = function(sProperty, vValue) {
	if (sProperty !== "opened") {
		throw new Errors.InvalidTestError("ComponentFixture only supports the 'opened' property.");
	} else {
		var sXml = this.m_sXml.replace("%TEST_COMPONENT_ID%", vValue);
		this._createComponent(sXml);
	}
};

/**
 * doWhen is not supported on the ComponentFixture.
 *
 * @param {String} sProperty name of the property
 * @param {String} vValue value of the property
 *
 * @see br.test.Fixture#doWhen
 */
ComponentFixture.prototype.doWhen = function(sProperty, vValue) {
	throw new Errors.IllegalTestClauseError("'when' clauses are not allowed for the ComponentFixture");
};

/**
 * doThen is not supported on the ComponentFixture.
 *
 * @param {String} sProperty name of the property
 * @param {String} vValue value of the property
 *
 * @see br.test.Fixture#doThen
 */
ComponentFixture.prototype.doThen = function(sProperty, vValue) {
	throw new Errors.IllegalTestClauseError("'then' clauses are not allowed for the ComponentFixture");
};

/**
 * The ComponentFixture adds the following sub-fixtures:
 * <ul>
 * <li><code>model</code>: the presentation model fixture, for manipulating and verifying properties in the presentation model</li>
 * <li><code>view</code>: the view fixture, the view fixture, for manipulating and verifying the state of elements on the component's view</li>
 * <li><code>componentFrame</code>: the component frame fixture, for verifying the state of the {@link module:br/component/Frame} housing the component</li>
 * </ul>
 * @see br.test.Fixture#addSubFixtures
 */
ComponentFixture.prototype.addSubFixtures = function(oFixtureRegistry) {
	oFixtureRegistry.addFixture("model", this.m_oModelFixture);
	oFixtureRegistry.addFixture("view", this.m_oViewFixture);
	oFixtureRegistry.addFixture("componentFrame", this.m_oComponentFrameFixture);
};

/**
 * @see br.test.Fixture#canHandleExactMatch
 */
ComponentFixture.prototype.canHandleExactMatch = function() {
	return false;
};

/**
 * This method can be called to set a single function on the fixture which will be executed whenever the
 * component is created and opened.
 *
 * @param {function} fCallback the function to execute on opening the component
 */
ComponentFixture.prototype.onOpen = function(fCallback) {
	this.m_fOnOpenCallback = fCallback;
};

/**
 * Tear down this fixture and and recreate the component with the passed in xml string.
 *
 * @param {String} sXml the xml string for the component.
 */
ComponentFixture.prototype.tearDownOldComponentAndRecreateWithNewXML = function(sXml) {
	this.tearDown();
	this.m_oViewFixture.tearDown();

	this._createComponent(sXml);
};

/**
 * Returns the Component under test.
 */
ComponentFixture.prototype.getComponent = function() {
	return this.m_oComponent;
};

/* -----------------------------------------------------------------------------
 *						  Private Methods
 * ----------------------------------------------------------------------------*/
/**
 * @private
 */
ComponentFixture.prototype._createComponent = function(sXml) {

	var sXMLRootMatch = /^\s*<([-A-Za-z0-9_.]+)/;
	var sType = ((sXml)?sXml.match(sXMLRootMatch)[1]:null);

	var Component = Utility.locate(sType, window);
	if(!Component){
		var Component = AliasRegistry.getClass( sType );
	}

	var oComponent = null;
	if (Component.createFromXml) {
		oComponent = Component.createFromXml(sXml);
	}
	else if (Component.deserialize) {
		oComponent = Component.deserialize(sXml);
	}

	this._setComponent(oComponent);

	if (this.m_fOnOpenCallback !== null) {
		this.m_fOnOpenCallback(oComponent);
	}

	oComponent.setDisplayFrame(this.m_oComponentFrame);
};

/**
 * @private
 */
ComponentFixture.prototype._setComponent = function(oComponent) {
	this.m_oComponent = oComponent;
	this.m_oModelFixture.setComponent(oComponent);
	this.m_oViewFixture.setComponent(oComponent);
};

ComponentFixture.prototype._setElement = function(eElement) {
	var nComponentWidth = 500;
	var nComponentHeight = 498;
	var eComponentContainer = document.createElement('div');
	eComponentContainer.style.width = nComponentWidth + "px"; //We need a container for the component due to failing scrolling tests in
	eComponentContainer.style.height = nComponentHeight + "px"; //grid CTs, the last row was being hidden by the horizontal scroll bar.

	eComponentContainer.appendChild(eElement);

	this.m_oViewFixture.setViewElement(eElement);

	document.body.appendChild(eComponentContainer);

	this.m_oComponentFrame.width = nComponentWidth;
	this.m_oComponentFrame.height = nComponentHeight;
	this.m_oComponentFrame.state = Frame.NORMAL;
	this.m_oComponentFrame.trigger('attach');

	this.m_oComponentFrame.isContentVisible = true;
	this.m_oComponentFrame.trigger('show');

	this.m_oComponentFrame.isFocussed = true;
	this.m_oComponentFrame.trigger('focus');
};

module.exports = ComponentFixture;

ComponentFrame = require('./ComponentFrame');
ComponentFrameFixture = require('./ComponentFrameFixture');
ComponentModelFixture = require('./ComponentModelFixture');
Frame = require('br/component/Frame');
AliasRegistry = require('br/AliasRegistry');
ViewFixture = require('br/test/ViewFixture');
Utility = require('br/core/Utility');
