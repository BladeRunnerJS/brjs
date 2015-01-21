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
