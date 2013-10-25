caplin.thirdparty("caplin-br");

(function() {
	
	var Utility = require('br/core/Utility');
	
	var UtilityTest = TestCase("UtilityTest").prototype;

	UtilityTest["test locate"] = function() {
		var locate = Utility.locate;
		
		var testObject = {hi: {jim: {console: "hello world"}}};
		
		// happy path
		assertEquals(testObject.hi, locate("hi", testObject));
		assertEquals(testObject.hi.jim, locate("hi.jim", testObject));
		assertEquals(testObject.hi.jim.console, locate("hi.jim.console", testObject));

		// less happy path
		assertEquals(undefined, locate("nothing", testObject));
		assertEquals(undefined, locate("hi.nothing", testObject));
		assertEquals(undefined, locate("hi.jim.nothing", testObject));
		assertEquals(undefined, locate("hi.jim.console.nothing", testObject));
		assertEquals(undefined, locate("nothing.jim.console", testObject));
		assertEquals(undefined, locate("hi.nothing.console", testObject));

		// unhappy path
		assertException(function() {
			locate({});
		}, 'TypeError');
		assertException(function() {
			locate(null);
		}, 'TypeError');
		
		// works with arrays
		assertEquals(testObject.hi.jim.console, locate("1.hi.jim.console", [null, testObject]));
	};
	
	UtilityTest["test isEmpty"] = function() {
		var isEmpty = Utility.isEmpty;

		assertEquals(true, isEmpty({}));
		assertEquals(true, isEmpty(new Object()));

		assertEquals(false, isEmpty({key: "value"}));
		assertEquals(false, isEmpty({key: 0}));
		assertEquals(false, isEmpty({key: false}));
		assertEquals(false, isEmpty({key: undefined}));
		assertEquals(false, isEmpty({key: null}));

		assertEquals(false, isEmpty(Object.create({key: null})));
	};
	
	UtilityTest["test addValuesToSet"] = function() {
		var addValuesToSet = Utility.addValuesToSet;
		
		var set = {};
		addValuesToSet(set, [1, 2, 3, "hello", "there"]);
		
		assertTrue(set[1]);
		assertTrue(set[2]);
		assertTrue(set[3]);
		assertTrue(set["hello"]);
		assertTrue(set["there"]);
		
		assertException(function() {
			addValuesToSet(set, [null]);
		}, 'TypeError');
		assertException(function() {
			addValuesToSet(null, ["hello"]);
		}, 'TypeError');
		assertException(function() {
			addValuesToSet({}, 45);
		}, 'TypeError');
	};
	
})();