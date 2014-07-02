br.Core.thirdparty('jsunitextensions');
br.Core.thirdparty('mock4js');

ObservableTest = TestCase("ObservableTest");

ObservableTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	
	this.m_oObservable = new br.util.Observable();
	this.m_oObserver1 = mock(SimpleObserver);
	this.m_oObserver2 = mock(SimpleObserver);
};

ObservableTest.prototype.tearDown = function()
{
	Mock4JS.verifyAndClearAllMocks();
};

function SimpleObserver()
{
}
SimpleObserver.prototype.method1 = function() {
};
SimpleObserver.prototype.method2 = function(sOne) {
};
SimpleObserver.prototype.method3 = function(sOne, nTwo, bThree) {
};

SimpleObserver.prototype.method1_ = function() {
};
SimpleObserver.prototype.method2_ = function(sOne) {
};
SimpleObserver.prototype.method3_ = function(sOne, nTwo, bThree) {
};

function BadObserver()
{
}

BadObserver.prototype.method2 = function(sOne) {
	throw "Failed!!!";
};

ObservableTest.prototype.test_observerReceivesNullArgumentNotification = function()
{
	this.m_oObserver1.expects(once()).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method1");
};

ObservableTest.prototype.test_observerReceivesNoArgumentNotification = function()
{
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver1.expects(once()).method1_();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method1", []);
	this.m_oObservable.notify("method1_");
};

ObservableTest.prototype.test_observerReceivesOneArgumentNotification = function()
{
	this.m_oObserver1.expects(once()).method2(eq("test"));
	this.m_oObserver1.expects(once()).method2_(eq("test"));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method2", [ "test" ]);
	this.m_oObservable.notify("method2_", "test");
};

ObservableTest.prototype.test_observerReceivesThreeArgumentNotification = function()
{
	this.m_oObserver1.expects(once()).method3(eq("one"), eq(10), eq(false));
	this.m_oObserver1.expects(once()).method3_(eq("one"), eq(10), eq(false));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method3", [ "one", 10, false ]);
	this.m_oObservable.notify("method3_", "one", 10, false);
};

ObservableTest.prototype.test_observerReceivesMultiplesNotifications = function()
{
	this.m_oObserver1.expects(once()).method3(eq("one"), eq(10), eq(false));
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver1.expects(once()).method3(eq("next"), eq(-1), eq(true));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method3", [ "one", 10, false ]);
	this.m_oObservable.notifyObservers("method1", []);
	this.m_oObservable.notifyObservers("method3", [ "next", -1, true ]);
};

ObservableTest.prototype.test_multipleObserversReceiveNotifications = function()
{
	this.m_oObserver1.expects(once()).method3(eq("one"), eq(10), eq(false));
	this.m_oObserver1.expects(once()).method2(eq("something"));
	this.m_oObserver2.expects(once()).method3(eq("one"), eq(10), eq(false));
	this.m_oObserver2.expects(once()).method2(eq("something"));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());
	
	this.m_oObservable.notifyObservers("method3", [ "one", 10, false ]);
	this.m_oObservable.notifyObservers("method2", [ "something" ]);
};

ObservableTest.prototype.test_addNullObserverFails = function()
{
	var oThis = this;
	assertException(function() { 
		oThis.m_oObservable.addObserver(null);
	}, "InvalidParametersError");
};

ObservableTest.prototype.test_addUndefinedObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(undefined); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addUnspecifiedObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNumericPrimativeObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(10); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNumericObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(new Number(10)); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addStringObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver("test"); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNewStringObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(new String("test")); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addFunctionObserverFails = function()
{
	var oThis = this;
	var fTest = ObservableTest.prototype.test_addFunctionObserverFails;
	assertException(function() { oThis.m_oObservable.addObserver(fTest); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addBooleanPrimativeObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(false); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addBooleanObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addObserver(new Boolean(false)); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addDuplicateObserverSucceeds = function()
{
	// observer will be notified of events twice
	this.m_oObserver1.expects(exactly(2)).method1();
	this.m_oObserver1.expects(exactly(2)).method2(eq("duplicates"));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObservers("method1", [ ]);
	this.m_oObservable.notifyObservers("method2", [ "duplicates" ]);
};

ObservableTest.prototype.test_addNullUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(null); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addUndefinedUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(undefined); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addUnspecifiedUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNumericPrimativeUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(10); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNumericUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(new Number(10)); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addStringUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver("test"); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addNewStringUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(new String("test")); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addFunctionUniqueObserverFails = function()
{
	var oThis = this;
	var fTest = ObservableTest.prototype.test_addFunctionUniqueObserverFails;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(fTest); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addBooleanPrimativeUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(false); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addBooleanUniqueObserverFails = function()
{
	var oThis = this;
	assertException(function() { oThis.m_oObservable.addUniqueObserver(new Boolean(false)); }, "InvalidParametersError");
};

ObservableTest.prototype.test_addDuplicateUniqueObserverFails = function()
{
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver1.expects(once()).method2(eq("no duplicates"));
	
	assertTrue(this.m_oObservable.addUniqueObserver(this.m_oObserver1.proxy()));
	// second attempt to add the observer should fail
	assertFalse(this.m_oObservable.addUniqueObserver(this.m_oObserver1.proxy()));
	
	this.m_oObservable.notifyObservers("method1", [ ]);
	this.m_oObservable.notifyObservers("method2", [ "no duplicates" ]);
};

ObservableTest.prototype.test_removeObserverWhereObserverIsRegistered = function()
{
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver2.expects(exactly(2)).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());
	
	this.m_oObservable.notifyObservers("method1", [ ]);
	
	// remove the first observer - it shouldn't be notified of events now
	assertTrue(this.m_oObservable.removeObserver(this.m_oObserver1.proxy()));
	
	this.m_oObservable.notifyObservers("method1", [ ]);
};

ObservableTest.prototype.test_removeObserverWhereObserverIsUnregistered = function()
{
	assertFalse(this.m_oObservable.removeObserver(this.m_oObserver1.proxy()));
	assertFalse(this.m_oObservable.removeObserver(""));
	assertFalse(this.m_oObservable.removeObserver(10));
	assertFalse(this.m_oObservable.removeObserver(null));
	assertFalse(this.m_oObservable.removeObserver(undefined));
	assertFalse(this.m_oObservable.removeObserver());
};

ObservableTest.prototype.test_allRemoveObservers = function()
{
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver2.expects(once()).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());
	
	this.m_oObservable.notifyObservers("method1", [ ]);
	
	this.m_oObservable.removeAllObservers();
	
	this.m_oObservable.notifyObservers("method1", [ ]);
};

ObservableTest.prototype.test_observerCanSafelyRemoveItselfMidCallback = function()
{
	var oThis = this;
	var bDynamicObserverInvoked;
	var oDynamicObserver = {method1:function(){
		bDynamicObserverInvoked = true;
		oThis.m_oObservable.removeObserver(this);
	}};
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(oDynamicObserver);
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());
	
	this.m_oObserver1.expects(once()).method1();
	bDynamicObserverInvoked = false;
	this.m_oObserver2.expects(once()).method1();
	this.m_oObservable.notifyObservers("method1", [ ]);
	assertTrue("1a", bDynamicObserverInvoked);
	Mock4JS.verifyAllMocks();
	
	this.m_oObserver1.expects(once()).method1();
	bDynamicObserverInvoked = false;
	this.m_oObserver2.expects(once()).method1();
	this.m_oObservable.notifyObservers("method1", [ ]);
	assertFalse("2a", bDynamicObserverInvoked);
	Mock4JS.verifyAllMocks();
};

ObservableTest.prototype.test_notifyObserversWithTryCatchCallsAllObservablesAndReturnsExceptionsArrayForNonExistentMethod  = function()
{
	this.m_oObserver1.expects(once()).method1();
	this.m_oObserver2.expects(once()).method1();
	
	var oBadObserver = new BadObserver();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(oBadObserver);
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());

	var errors = this.m_oObservable.notifyObserversWithTryCatch("method1", [ ]);
	
	assertEquals(errors.length,1);
	assertEquals(errors[0].getMethodName(),"method1");
	assertEquals(errors[0].getObserver(),oBadObserver);
};

ObservableTest.prototype.test_notifyObserversWithTryCatchAndExceptionFlagCallsAllObservablesAndThrowsExceptionsArray = function()
{
	this.m_oObserver1.expects(once()).method2(eq("good"));
	this.m_oObserver2.expects(once()).method2(eq("good"));
	
	var oBadObserver = new BadObserver();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(oBadObserver);
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());

	try {
		this.m_oObservable.notifyObserversWithTryCatch("method2", [ "good" ], true);
		fail();
	} catch (e) {
		assertEquals(e.length,1);
		assertEquals(e[0].getException(),"Failed!!!");
	}		
};

ObservableTest.prototype.test_notifyObserversWithTryCatchPassesThroughArgumentsToObservers = function()
{
	this.m_oObserver1.expects(once()).method2(eq("good"));
	this.m_oObserver2.expects(once()).method2(eq("good"));
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());

	this.m_oObservable.notifyObserversWithTryCatch("method2", [ "good" ]);
};


ObservableTest.prototype.test_getAllObservers = function()
{
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	this.m_oObservable.addObserver(this.m_oObserver2.proxy());
	
	var pAllObservers = this.m_oObservable._$getAllObservers();
	assertEquals(2, pAllObservers.length);
	assertTrue(this.m_oObserver1.proxy() == pAllObservers[0]);
	assertTrue(this.m_oObserver2.proxy() == pAllObservers[1]);
};

ObservableTest.prototype.test_getCount = function()
{
	var oObserver1 = this.m_oObserver1.proxy();
	var oObserver2 = this.m_oObserver2.proxy();
	
	assertEquals("Should be no observers registered", 0, this.m_oObservable.getCount());
	this.m_oObservable.addObserver(oObserver1);
	assertEquals("Should be 1 observer registered", 1, this.m_oObservable.getCount());
	this.m_oObservable.addObserver(oObserver2);
	assertEquals("Should be 2 observer registered", 2, this.m_oObservable.getCount());
	this.m_oObservable.removeObserver(oObserver1);
	assertEquals("Should be 1 observer registered", 1, this.m_oObservable.getCount());
	this.m_oObservable.removeObserver(oObserver2);
	assertEquals("Should be no observers registered", 0, this.m_oObservable.getCount());
};

ObservableTest.prototype.test_observerWithNonFunctionCallback = function()
{
	this.m_oObserver1.expects(once()).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	// call valid method
	this.m_oObservable.notifyObservers("method1", []);
	
	// call invalid method
	this.m_oObserver1.str = "foo";
	this.m_oObserver1.arr = [1,2,3];
	this.m_oObserver1.map = {"a":"1"};
	this.m_oObserver1.bool = true;
	this.m_oObserver1.nil = null;
	try {
		this.m_oObservable.notifyObservers("str", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
	try {
		this.m_oObservable.notifyObservers("arr", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
	try {
		this.m_oObservable.notifyObservers("map", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
	try {
		this.m_oObservable.notifyObservers("bool", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
	try {
		this.m_oObservable.notifyObservers("nil", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
};



ObservableTest.prototype.test_observerWithMissingCallback = function()
{
	this.m_oObserver1.expects(once()).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	// call valid method
	this.m_oObservable.notifyObservers("method1", []);

	// call invalid method
	try {
		this.m_oObservable.notifyObservers("nonExistantMethod", []);
		fail();
	} catch (e) {
		assertTrue(true);
	}
};

ObservableTest.prototype.test_observerReceivesNullArgumentNotificationWithnotifyObserversWithTryCatch = function()
{
	this.m_oObserver1.expects(once()).method1();
	
	this.m_oObservable.addObserver(this.m_oObserver1.proxy());
	
	this.m_oObservable.notifyObserversWithTryCatch("method1");
};
