require('br-presenter/_resources-test-at/html/test-form.html');
(function () {

	var assertFails = require('jsunitextensions').assertFails;
	var Mock4JS = require('mock4js');

	var ComponentFixtureTest = TestCase("ComponentFixtureTest");
	var PresentationModelFixture = require('br-presenter/testing/PresentationModelFixture');
	var ViewFixture = require('br-test/ViewFixture');
	var FixtureRegistry = require('br-test/FixtureRegistry');
	var ComponentFixture = require('br-component/testing/ComponentFixture');
	var TestSerializableComponent = require('br-component/_test-src/TestSerializableComponent');

	ComponentFixtureTest.prototype.setUp = function()
	{
		var Mock4JS = require('mock4js');
		Mock4JS.addMockSupport(window);
		Mock4JS.clearMocksToVerify();

		this.m_oMockModelFixture = mock(PresentationModelFixture);
		this.m_oMockViewFixture = mock(ViewFixture);
		this.m_oMockFixtureRegistry = mock(FixtureRegistry);

		this.m_eElement = document.createElement("div");

		this.m_oMockComponent = mock(TestSerializableComponent);
	//	this.m_oMockComponent.stubs().getElement().will(returnValue(this.m_eElement));
	//	this.m_oMockComponent.stubs().onOpen(500, 498);
	//	this.m_oMockComponent.stubs().onActivate();
	//
	//	var oThis = this;
	//	br.component.ComponentFactory.registerComponent("component", function(sXml){
	//		oThis.m_oMockComponent.expects(once()).setFrame(ANYTHING);
	//		oThis.m_sComponentXml = sXml;
	//		return oThis.m_oMockComponentInstance;
	//	});
	};

	ComponentFixtureTest.prototype.tearDown = function()
	{
		Mock4JS.verifyAllMocks();
	};


	/* ********************************************************************
	*						 Instantiating Fixture
	**********************************************************************/

	ComponentFixtureTest.prototype.test_fixtureMustBeInstantiatedWithXMLAndModelFixture = function()
	{
		var self = this;
		assertFails("1a ComponentFixture cannot be constucted with no XML and model fixture",
				function() {
					new ComponentFixture();
				}
		);
		assertFails("1b ComponentFixture cannot be constucted without model fixture",
			function() {
				new ComponentFixture("<br.component.testSerializableComponent/>");
			}
		);
		assertFails("1c ComponentFixture cannot be constucted with null model fixture",
			function() {
				new ComponentFixture("<br.component.testSerializableComponent/>", null);
			}
		);
		assertFails("1d ComponentFixture cannot be constucted with invalid model fixture",
			function() {
				new ComponentFixture("<br.component.testSerializableComponent/>", "a string");
			}
		);
		assertFails("1e ComponentFixture cannot be constucted with null XML",
			function() {
				new ComponentFixture(null, self.m_oMockModelFixture.proxy());
			}
		);
		assertFails("1f ComponentFixture cannot be constucted with empty XML",
			function() {
				new ComponentFixture("", self.m_oMockModelFixture.proxy());
			}
		);
		assertNoException("1g ComponentFixture can be constucted with XML and model fixture parameters",
			function() {
				new ComponentFixture("<br.component.testSerializableComponent/>", self.m_oMockModelFixture.proxy());
			}
		);
	};

	/*
	 * Adding a view handler to the captive ViewFixture
	 */
	ComponentFixtureTest.prototype.test_aNewViewHandlerCanBeAdded = function() {
		var oComponentFixture = new ComponentFixture("<component/>",
			this.m_oMockModelFixture.proxy(), this.m_oMockViewFixture.proxy());

		var viewHandlerMap = {
			'new-value': function() {
				this.get = function() { return 'new (valid) handler get response'; };
				this.set = function() {};
			}
		};

		this.m_oMockViewFixture.expects(once()).addViewHandlers(viewHandlerMap);
		oComponentFixture.addViewFixtureHandlers(viewHandlerMap);
	};

	ComponentFixtureTest.prototype.test_SelectorMappingsCanBeAdded = function() {
		var oComponentFixture = new ComponentFixture("<component/>",
			this.m_oMockModelFixture.proxy(), this.m_oMockViewFixture.proxy());

		var mMappings = {
			'foo': 'bar',
			'baz': 'faz'
		};

		this.m_oMockViewFixture.expects(once()).setSelectorMappings(mMappings);
		oComponentFixture.setSelectorMappings(mMappings);
	};

	/* ********************************************************************
	*						 Handling 'opened'
	**********************************************************************/

	ComponentFixtureTest.prototype.test_canHandlePropertyOpened = function()
	{
		var oFixture = new ComponentFixture("<br.component.testSerializableComponent/>", this.m_oMockModelFixture.proxy());
		var bCanHandleProperty = oFixture.canHandleProperty('opened');
		assertTrue("2a ComponentFixture can handle property opened", bCanHandleProperty);
	};

	/* ********************************************************************
	*						 doWhen and doThen
	**********************************************************************/


	ComponentFixtureTest.prototype.test_doWhenAndDoThenThrowException = function()
	{
		var oComponentFixture = new ComponentFixture("<br.component.testSerializableComponent/>",
				this.m_oMockModelFixture.proxy());

		assertException("3a ComponentFixture cannot handle doWhen", function() {
			oComponentFixture.doWhen("opened", true);
		}, "IllegalTestClauseError");

		assertException("3b ComponentFixture cannot handle doThen", function() {
			oComponentFixture.doThen("opened", true);
		}, "IllegalTestClauseError");
	};

	/* ********************************************************************
	*							   doGiven
	**********************************************************************/

	ComponentFixtureTest.prototype.test_componentOpenedIsSetOnTheModelFixture = function()
	{
		var oComponentFixture = new ComponentFixture("<br.component.TestSerializableComponent/>",
				this.m_oMockModelFixture.proxy());

	//	this.m_oMockModelFixture.expects(once()).setComponent(new TestSerializableComponent());
		this.m_oMockModelFixture.expects(once()).setComponent(ANYTHING);
		oComponentFixture.doGiven("opened", true);
	};

	// The component fixture does not interact with the ViewFixture to call setViewElement
	ComponentFixtureTest.prototype.test_componentOpenedIsSetOnTheViewFixture = function()
	{
		var oComponentFixture = new ComponentFixture("<br.component.TestSerializableComponent/>",
				this.m_oMockModelFixture.proxy(), this.m_oMockViewFixture.proxy());

		this.m_oMockModelFixture.stubs().setComponent(ANYTHING);
	//	this.m_oMockViewFixture.expects(once()).setViewElement(this.m_eElement);
	//	this.m_oMockViewFixture.expects(once()).setComponent(new br.component.TestSerializableComponent());
		this.m_oMockViewFixture.expects(once()).setComponent(ANYTHING);
		oComponentFixture.doGiven("opened", true);
	};

	// TODO go through the rest of these tests and validate
	//ComponentFixtureTest.prototype.test_componentOpenedCallsOnOpenOnTheComponentCreated = function()
	//{
	//	var oComponentFixture = new ComponentFixture("<br.component.testSerializableComponent/>",
	//			this.m_oMockModelFixture.proxy());
	//
	//	this.m_oMockModelFixture.stubs().setComponent(ANYTHING);
	//	this.m_oMockComponent.expects(once()).onOpen(ANYTHING, ANYTHING);
	//	oComponentFixture.doGiven("opened", true);
	//};
	//
	//ComponentFixtureTest.prototype.test_componentOpenedInvokesOnOpenCallback = function()
	//{
	//	var oComponentFixture = new ComponentFixture("<component/>",
	//			this.m_oMockModelFixture.proxy());
	//
	//	var bCallbackInvoked = false;
	//	var fCallback = function(oComponent)
	//	{
	//		bCallbackInvoked = true;
	//		assertTrue("5a A component should be passed to the onOpen callback function",
	//				oComponent instanceof br.component.Component);
	//	};
	//
	//	this.m_oMockModelFixture.stubs().setComponent(ANYTHING);
	//	this.m_oMockComponent.stubs().onOpen(ANYTHING, ANYTHING);
	//	oComponentFixture.onOpen(fCallback);
	//	oComponentFixture.doGiven("opened", true);
	//	assertTrue("5b onOpen callback function sould have been invoked", bCallbackInvoked);
	//};
	//
	//ComponentFixtureTest.prototype.test_createdComponentIsClosedOnTeardown = function()
	//{
	//	var oComponentFixture = new ComponentFixture("<component/>",
	//			this.m_oMockModelFixture.proxy());
	//
	//	oComponentFixture.setUp();
	//	this.m_oMockModelFixture.stubs().setComponent(ANYTHING);
	//	oComponentFixture.doGiven("opened", true);
	//	this.m_oMockComponent.expects(once()).onClose();
	//	oComponentFixture.tearDown();
	//};
	//
	///* ********************************************************************
	// *							 sub-fixtures
	// **********************************************************************/
	//
	//ComponentFixtureTest.prototype.test_modelAndViewAndFrameFixturesAreAddedAsSubFixtures = function()
	//{
	//	var oModelFixture = this.m_oMockModelFixture.proxy();
	//	var oViewFixture = this.m_oMockViewFixture.proxy();
	//	var oFixtureRegistry = this.m_oMockFixtureRegistry.proxy();
	//
	//	var oComponentFixture = new ComponentFixture("<component/>",
	//			oModelFixture, oViewFixture);
	//
	//	this.m_oMockFixtureRegistry.expects(once()).addFixture("model", oModelFixture);
	//	this.m_oMockFixtureRegistry.expects(once()).addFixture("view", oViewFixture);
	//	this.m_oMockFixtureRegistry.expects(once()).addFixture("componentFrame", ANYTHING);
	//	oComponentFixture.addSubFixtures(oFixtureRegistry);
	//};
	//
	///* ********************************************************************
	// * 							public methods
	// **********************************************************************/
	//
	//ComponentFixtureTest.prototype.test_getComponent = function()
	//{
	//	var oComponentFixture = new ComponentFixture("<component/>", this.m_oMockModelFixture.proxy());
	//
	//	this.m_oMockModelFixture.expects(once()).setComponent(this.m_oMockComponentInstance);
	//	oComponentFixture.doGiven("opened", true);
	//
	//	assertEquals(this.m_oMockComponent.proxy(), oComponentFixture.getComponent());
	//};

})();
