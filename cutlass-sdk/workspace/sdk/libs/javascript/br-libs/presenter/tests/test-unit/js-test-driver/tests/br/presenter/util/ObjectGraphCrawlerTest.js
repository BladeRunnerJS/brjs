br.Core.thirdparty('mock4js');

ObjectGraphCrawlerTest = TestCase("ObjectGraphCrawler");

// --------------- TEST OBJECTS -----------------------
TestObjectLeg = function(sDealtCurrency){this.m_sDealtCurrency = sDealtCurrency;};
TestObjectLeg.prototype.getDealtCurrency = function(){return this.m_sDealtCurrency;};
TestObjectLeg.prototype.setDealtCurrency = function(sDealtCurrency){this.m_sDealtCurrency = sDealtCurrency;};
TestObjectLeg.prototype.getSomething = function(){return undefined;};

TestObject = function(sAccount, sDealtCurrency)
{
	//to be accessed as object property
	this.Account = sAccount;
	//to be accessed by setter/getter
	this.m_sDealtCurrency = sDealtCurrency;
	this.m_sTrType = "";
	
	this.legs = [new TestObjectLeg(this.m_sDealtCurrency), new TestObjectLeg(this.m_sDealtCurrency)];
};
TestObject.prototype.setTradingType = function(sTradeType){this.m_sTrType = sTradeType;};
TestObject.prototype.getTradingType = function(){return this.m_sTrType;};
TestObject.prototype.getFieldValue = function(sFieldName){};
TestObject.prototype.setFieldValue = function(sFieldName, sFieldValue){};
//----------------------------------------------------

ObjectGraphCrawlerTest.prototype.setUp = function() {
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
	
	this.m_oObjectCrawler = new br.presenter.util.ObjectGraphCrawler();

	this.m_oTestObject = new TestObject("acct1", "GBP");
	this.m_oMockTestObject = mock(TestObject);
};

ObjectGraphCrawlerTest.prototype.tearDown = function() {
	Mock4JS.verifyAllMocks();
};

ObjectGraphCrawlerTest.prototype.test_setValueOnModel = function() {
	this.m_oObjectCrawler.setValueForObject(this.m_oTestObject, "Account", "acct29");
	assertEquals("1a", "acct29",this.m_oTestObject.Account);
};

ObjectGraphCrawlerTest.prototype.test_getValueFromModel = function() {
	this.m_oTestObject.Account = "acct62";
	var result = this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, ["Account"]);
	assertEquals("1a", "acct62", result);
};

ObjectGraphCrawlerTest.prototype.test_setValueOnModelUsingSetter = function() {
	this.m_oObjectCrawler.setValueForObject(this.m_oTestObject, "tradingType", "SPOT");
	assertEquals("1a", "SPOT", this.m_oTestObject.getTradingType());
};

ObjectGraphCrawlerTest.prototype.test_getValueFromModelUsingGetter = function() {
	this.m_oTestObject.setTradingType("FWD");
	var result = this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, ["tradingType"]);
	assertEquals("1a", "FWD", result);
};

ObjectGraphCrawlerTest.prototype.test_setValueOnModelUsingSetFieldValue = function() {
	var oTestObject = this.m_oMockTestObject.proxy();
	this.m_oMockTestObject.stubs().getFieldValue("randomField").will(returnValue(true));
	this.m_oMockTestObject.expects(once()).setFieldValue("randomField", "test_value");
	this.m_oObjectCrawler.setValueForObject(oTestObject, "randomField", "test_value");
};

ObjectGraphCrawlerTest.prototype.test_getValueFromModelUsingGetFieldValue = function() {
	var oTestObject = this.m_oMockTestObject.proxy();
	this.m_oMockTestObject.stubs().getFieldValue("randomField").will(returnValue("a_value"));
	var result = this.m_oObjectCrawler.getValueFromObject(oTestObject, ["randomField"]);
	assertEquals("1a", "a_value", result);
};

ObjectGraphCrawlerTest.prototype.test_getNestedValueModel = function() {
	this.m_oTestObject.legs[0].setDealtCurrency("EUR");
	var sFirstLegCurrency = this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, ["legs","0","dealtCurrency"]);
	assertEquals("1a", "EUR", sFirstLegCurrency);
};	

ObjectGraphCrawlerTest.prototype.testGetNestedValueFromStringPath = function() {
	this.m_oTestObject.legs[0].setDealtCurrency("JPY");
	var sFirstLegCurrency = this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, "legs.0.dealtCurrency");
	assertEquals("1a", "JPY", sFirstLegCurrency);
};

ObjectGraphCrawlerTest.prototype.testSetNestedValueModel = function() {
	this.m_oObjectCrawler.setValueForObject(this.m_oTestObject, "legs.0.dealtCurrency", "USD");
	assertEquals("1a", "USD", this.m_oTestObject.legs[0].getDealtCurrency());
};


ObjectGraphCrawlerTest.prototype.testSetThrowsErrorIfSetMethodDoesntExist = function() 
{
	try
	{
		this.m_oObjectCrawler.setValueForObject(this.m_oTestObject, "legs.0.doesntExist", "value");
		fail();
	}
	catch(e)
	{
		assertEquals(e.message,'The method "setDoesntExist" does not exist');
	}
};


ObjectGraphCrawlerTest.prototype.testGetThrowsErrorIfGetMethodDoesntExistFromString = function() 
{
	try
	{
		this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, "legs.0.doesntExist");
		fail();
	}
	catch(e)
	{
		assertEquals(e.message,'The method "getDoesntExist" or object "doesntExist" could not be found when searching for property "legs.0.doesntExist"');
	}
};

ObjectGraphCrawlerTest.prototype.testGetThrowsErrorIfGetMethodDoesntExistFromArray = function() 
{
	try
	{
		this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject,  ["legs","0","doesntExist"]);
		fail();
	}
	catch(e)
	{
		assertEquals(e.message,'The method "getDoesntExist" or object "doesntExist" could not be found when searching for property "legs.0.doesntExist"');
	}
};

ObjectGraphCrawlerTest.prototype.testGetThrowsErrorIfPathIsntCorrect = function() 
{
	try
	{
		this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, "legs.2.dealtCurrency");
		fail();
	}
	catch(e)
	{
		
		assertEquals(e.message,'The method "get2" or object "2" could not be found when searching for property "legs.2.dealtCurrency"');
	}
};

ObjectGraphCrawlerTest.prototype.testGetThrowsErrorIfPathIsntCorrectDueToUndefinedReturn = function() 
{
	try
	{
		this.m_oObjectCrawler.getValueFromObject(this.m_oTestObject, "legs.1.Something.dealtCurrency");
		fail();
	}
	catch(e)
	{
		assertEquals(e.message,'The method "getSomething" returned undefined when searching for property "legs.1.Something.dealtCurrency"');
	}
};
