require("mock4js");
ApiProtector = function()
{
	this.m_pObjects = [];
	this.m_pItems = [];

	this.protectApis.apply(this, arguments);
};

ApiProtector.prototype.protectApis = function()
{
	for(var i = 0, l = arguments.length; i < l; ++i)
	{
		var sObject = arguments[i];
		try
		{
			var oObject = caplin.core.ClassUtility.getPackage(sObject);
			this.protectApi(oObject);

			if(oObject.prototype)
			{
				this.protectApi(oObject.prototype);
			}
		}
		catch(e)
		{
			// do nothing
		}
	}
};

ApiProtector.prototype.protectApi = function(oObject)
{
	var mItems = {};

	for(var sItem in oObject)
	{
		mItems[sItem] = oObject[sItem];
	}

	this.m_pObjects.push(oObject);
	this.m_pItems.push(mItems);
};

ApiProtector.prototype.restoreApis = function()
{
	for(var i = 0, l = this.m_pObjects.length; i < l; ++i)
	{
		var vObject = this.m_pObjects[i];
		var mItems = this.m_pItems[i];

		for(var sItem in mItems)
		{
			vObject[sItem] = mItems[sItem];
		}
	}
};

var jstestdriverextensions = {
	ApiProtector: ApiProtector
};



function CaplinTestCase(sTestName)
{
	var oTest = TestCase(sTestName).prototype;

	oTest.test_testCaseHasNotBeenInitialized = function()
	{
		fail("you must call Test.initialize() at the end of your test script");
	};

	oTest.initialize = function()
	{
		delete oTest["test_testCaseHasNotBeenInitialized"];
		delete oTest["initialize"];

		var oTempTest = {};
		for(var sMethod in oTest)
		{
			if((sMethod == "setUp") || (sMethod == "tearDown"))
			{
				//oTest["orig_" + sMethod] = oTest[sMethod];
				oTempTest["orig_" + sMethod] = oTest[sMethod];
				delete oTest[sMethod];
			}
			else if(sMethod.match(/^_/))
			{
				// private method -- leave where it is
				oTempTest[sMethod] = oTest[sMethod];
			}
			else
			{
				//oTest["test_" + sMethod] = oTest[sMethod];
				oTempTest["test_" + sMethod] = oTest[sMethod];
				delete oTest[sMethod];
			}
		}

		//re-add renamed methods to the test object in a separate
		//loop to avoid an infinite loop when running tests in IE8-
		for (var sMethod in oTempTest)
		{
			oTest[sMethod] = oTempTest[sMethod];
		}

		oTest.setUp = function()
		{
			Mock4JS.addMockSupport(window);
			Mock4JS.clearMocksToVerify();

			this.m_fAssertEquals = window.assertEquals;
			this.m_fAssertNotEquals = window.assertNotEquals;
			window.assertEquals = window.assertSame;
			window.assertNotEquals = window.assertNotSame;
			this.m_oApiProtector = new ApiProtector();

			if(this.orig_setUp)
			{
				return this.orig_setUp();
			}
		};

		oTest.tearDown = function()
		{
			this.m_oApiProtector.restoreApis();
			window.assertEquals = this.m_fAssertEquals;
			window.assertNotEquals = this.m_fAssertNotEquals;

			if(this.orig_tearDown)
			{
				return this.orig_tearDown();
			}

			Mock4JS.verifyAllMocks();
		};

		oTest.protectApis = function()
		{
			this.m_oApiProtector.protectApis.apply(this.m_oApiProtector, arguments);
		};
	};

	return oTest;
}

jstestdriverextensions.CaplinTestCase = CaplinTestCase;



function defineTestCase(name, setUp, tearDown) {

	var testCase = TestCase(name);
	testCase.prototype.setUp = setUp;
	testCase.prototype.tearDown = tearDown;

	var definitionErrors = [];

	var aliases = {};

	function addDefinitionError(message) {
		definitionErrors.push(message);
		if (testCase.prototype["test definition errors"] == null) {
			testCase.prototype["test definition errors"] = function() {
				fail(definitionErrors.join("\n"));
			}
		}
	}

	testCase.prototype.continuesFrom = function(testName) {
		var realTestName = aliases[testName] || testName;
		if ( ! testCase.prototype[realTestName]) {
			throw new Error("Test not found "+testName + " ("+realTestName+").");
		}
		try {
			testCase.prototype[realTestName].call(this);
		} catch (e) {
			if (e.name != "DependedTestFailed") {
				e = new Error("Unable to continue because depended on test "+testName+" failed.");
				e.name = "DependedTestFailed";
				e.stack = "";
			}
			throw e;
		}
		if (window["JsMockito"] && window["JsMockito"].clearAllMocks) {
			JsMockito.clearAllMocks();
		}
	};

	function testThat(mainTestName, testCode) {
		var realTestName = "test that "+mainTestName;
		if (testCase.prototype[realTestName] != null) {
			addDefinitionError("Can't name test '"+mainTestName+"' as a test with that name is already defined.");
		} else {
			testCase.prototype[realTestName] = testCode;
		}

		function aliasFunc(aliasName) {
			if (aliases[aliasName] != null) {
				addDefinitionError("Can't alias test '"+mainTestName+"' to '"+aliasName+"' as that alias is already defined.");
			} else {
				aliases[aliasName] = realTestName;
			}
			return aliasFunc;
		}

		return {
			alsoKnownAs: aliasFunc
		};
	}

	return {
		setup: function(setupCode) {
			testCase.prototype.setUp = setupCode;
		},
		tearDown: function(tearDownCode) {
			testCase.prototype.tearDown = tearDownCode;
		},
		set: function(name, value) {
			testCase.prototype[name] = value;
		},
		testThat: testThat
	};
}

jstestdriverextensions.defineTestCase = defineTestCase;

if (typeof module !== "undefined") module.exports = (Object.keys(module.exports).length || typeof module.exports === "function") ? module.exports : jstestdriverextensions;

window.jstestdriverextensions = (typeof module !== "undefined" && module.exports) || jstestdriverextensions;
