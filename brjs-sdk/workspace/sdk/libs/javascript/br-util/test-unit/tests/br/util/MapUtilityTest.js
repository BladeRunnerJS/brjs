require('jsunitextensions');

var MapUtility = require('br/util/MapUtility');

var MapUtilityTest = TestCase("MapUtilityTest");

MapUtilityTest.prototype.test_isEmpty = function()
{
	assertTrue("a1", MapUtility.isEmpty({}));
	assertFalse("a2", MapUtility.isEmpty({foo:1}));
	assertFalse("a3", MapUtility.isEmpty({foo:1, bar:2}));
};

MapUtilityTest.prototype.test_size = function()
{
	assertEquals("a1", 0, MapUtility.size({}));
	assertEquals("a2", 1, MapUtility.size({foo:1}));
	assertEquals("a3", 2, MapUtility.size({foo:1, bar:2}));
};

MapUtilityTest.prototype.test_valuesToAray = function()
{
	assertArrayEquals("a1", [], MapUtility.valuesToArray({}));
	assertArrayEquals("a2", [1, 2], MapUtility.valuesToArray({foo:1, bar:2}));
};

MapUtilityTest.prototype.test_addArrayToAnEmptyMap = function()
{
	verifyMapAddition("a1", {}, {}, []);
	verifyMapAddition("a2", {foo: true, bar: true}, {}, ["foo", "bar"]);
};

MapUtilityTest.prototype.test_addArrayToANonEmptyMap = function()
{
	verifyMapAddition("a1", {foo:false}, {foo:false}, []);
	verifyMapAddition("a2", {foo:false, foo2:true, bar:true}, {foo:false}, ["foo2", "bar"]);
};

MapUtilityTest.prototype.test_mapIsReturnedSoCanImmediatelyBeAssignedToNewVariable = function()
{
	var mMap = MapUtility.addArrayToMap({}, ["foo", "bar"]);
	
	assertEquals("a1", {foo: true, bar: true}, mMap);
};

MapUtilityTest.prototype.test_addArrayOverwritesExisitingValueInMap = function()
{
	verifyMapAddition("a1", {foo:true}, {foo:false}, ["foo"]);
};

function verifyMapAddition(sMessage, mExpectedMap, mTestMap, pTestArray)
{
	MapUtility.addArrayToMap(mTestMap, pTestArray);
	assertMapEquals(sMessage, mExpectedMap, mTestMap);
}

MapUtilityTest.prototype.test_removeArrayWithKeysWithinTheMap = function()
{
	verifyMapDeletion("a1", {bar:2}, {foo:1}, {foo:1, bar:2}, ["foo"]);
	verifyMapDeletion("a2", {bar:2}, {foo:1, test:3}, {foo:1, bar:2, test:3}, ["foo", "test"]);
};

MapUtilityTest.prototype.test_removeEmptyKeysArrayFromMapHasNoImpact = function()
{
	verifyMapDeletion("a1", {}, {}, {}, []);
	verifyMapDeletion("a2", {foo:1, bar:2}, {}, {foo:1, bar:2}, []);
};

MapUtilityTest.prototype.test_removeKeysThatAreNotInTheMapHasNoImpact = function()
{
	verifyMapDeletion("a1", {}, {}, {}, ["foo"]);
	verifyMapDeletion("a2", {foo:1, bar:2}, {}, {foo:1, bar:2}, ["test"]);
};

MapUtilityTest.prototype.test_removeKeyForAValueThatIsExplicitlyUndefinedRemovesItFromTheMap = function()
{
	verifyMapDeletion("a1", {}, {foo: undefined}, {foo: undefined}, ["foo"]);
};

function verifyMapDeletion(sMessage, mExpectedMap, mExpectedDeletionsMap, mTestMap, pTestArray)
{
	var mDeletedMap = MapUtility.removeArrayFromMap(mTestMap, pTestArray);
	assertMapEquals(sMessage + " - specified map did not contain the expected entries", mExpectedMap, mTestMap);
	assertMapEquals(sMessage + " - deleted map did not contain the expected entries", mExpectedDeletionsMap, mDeletedMap);
}

MapUtilityTest.prototype.test_mapsDefinedLiterallyCanBeMerged = function()
{
	var mMap1 = { a: "one", b: "two" };
	var mMap2 = { c: "three" };
	
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", { a: "one", b: "two", c: "three" }, mMergedMap);
};

MapUtilityTest.prototype.test_mapsDefinedAsNewObjectsCanBeMerged = function()
{
	var mMap1 = new Object();
	mMap1.a = "A";
	mMap1.b = "B";
	var mMap2 = new Object();
	mMap2["c"] = "C";
	
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", { a: "A", b: "B", c: "C" }, mMergedMap);
};

MapUtilityTest.prototype.test_emptyMapsCanBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = {};
	
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", {}, mMergedMap);
};

MapUtilityTest.prototype.test_manyMapsCanBeMerged = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { c: "3" };
	var mMap3 = { d: "4", e: "5" };
	var mMap4 = {};
	var mMap5 = { f: "6", g: "7", h: "8", i: "9" };
	
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2, mMap3, mMap4, mMap5]);
	
	assertMapEquals("1.1", { a: "1", b: "2", c: "3", d: "4", e: "5", f: "6", g: "7", h: "8", i: "9" }, mMergedMap);
};

MapUtilityTest.prototype.test_exceptionIsThrownIfNonMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = "";
	
	assertException("1.1", 
				function() { MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfNullMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = null;
	
	assertException("1.1", 
				function() { MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfUndefinedMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = undefined;
	
	assertException("1.1", 
				function() { MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFound = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	assertException("1.1", 
				function() { MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};


MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFoundAndThrowExceptionFlagIsSet = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	assertException("1.1", 
				// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
				function() { MapUtility.mergeMaps([mMap1, mMap2], false, true); },
				"InvalidParametersError");
}

MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFoundEvenIfValuesAreIdentical = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "2" };
	
	assertException("1.1", 
				function() { MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_duplicateKeysCanBeMergedWhenFlagIsSet = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2], true);
	
	assertMapEquals("1.1", { a: "1", b: "3" }, mMergedMap);
};

MapUtilityTest.prototype.test_duplicateKeysAreIgnoredWhenThrowExceptionFlagIsSetToFalse = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
	var mMergedMap = MapUtility.mergeMaps([mMap1, mMap2], false, false);
	
	assertMapEquals("original map value should have been kept", { a: "1", b: "2" }, mMergedMap);
};

MapUtilityTest.prototype.test_toStringWithSingleStringNameValuePairMap = function()
{
	var mMap = { a: "1" };
	
	assertEquals("1.1", "map#1{ a: 1 }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithSeveralStringNameValuePairsMap = function()
{
	var mMap = { a: "1", b: "2", c: "3" };
	
	assertEquals("1.1", "map#1{ a: 1, b: 2, c: 3 }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnotherMap = function()
{
	var mSubMap = { x: "24", y: "25", z: "26" };
	var mMap = { a: "1", b: "2", c: "3", nextMap: mSubMap };
	
	assertEquals("1.1", "map#1{ a: 1, b: 2, c: 3, nextMap: [object Object] }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithNoToStringMethod = function()
{
	var oObject = {
		mMyMap: { x: "24", y: "25", z: "26" }
	};
	var mMap = { obj: oObject };
	
	assertEquals("1.1", "map#1{ obj: [object Object] }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithAReferenceToAnotherMap = function()
{
	var oObject = {
		mMyMap: { x: "24", y: "25", z: "26" },
		toString: function() {
			return "myObject<" + MapUtility.toString(this.mMyMap) + ">";
		}
	};
	var mMap = { obj: oObject };
	
	assertEquals("1.1", "map#1{ obj: myObject<map#2{ x: 24, y: 25, z: 26 }> }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithAReferenceBackToMap = function()
{
	var mMap = { };
	var oObject = {
		mMyMap: mMap,
		toString: function() {
			return "myObject(" + MapUtility.toString(this.mMyMap) + ")";
		}
	};
	mMap["obj"] = oObject;
	
	assertEquals("1.1", "map#1{ obj: myObject(map#1{<see-earlier-definition>}) }", MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_shallowMergeTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = mOrgMap; // if a deep copy were to happen this may cause stack overflow
	
	var mMergingMap = {"hello":"world", "goodbye":"universe"};
	
	var mMergeResult = MapUtility.mergeMaps([mOrgMap,mMergingMap] );
	
	assertTrue("should be fish:pie", mOrgMap.fish == mMergeResult.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mMergeResult.monkey);
	assertTrue("shallow copied objects should be reference", mOrgMap.deep == mMergeResult.deep);
	
	assertTrue("merge has not occurred for hello", mMergingMap.hello == mMergeResult.hello);
	assertTrue("merge has not occurred for goodbye", mMergingMap.goodbye == mMergeResult.goodbye);
};

MapUtilityTest.prototype.test_deepMergeTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = {"ferret":"trousers"};
	
	var mMergingMap = {"hello":"world", "goodbye":"universe"};
	mMergingMap.deep = {"cow": "pat"};
	mMergingMap.deep2 = {"funky":"chicken"};

	// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
	var mMergeResult = MapUtility.mergeMaps([mOrgMap,mMergingMap], false, false, true);
	
	assertTrue("should be fish:pie", mOrgMap.fish == mMergeResult.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mMergeResult.monkey);
	
	assertFalse("deep copied objects should be copies and not the same reference", mOrgMap.deep == mMergeResult.deep);
	assertFalse("deep2 copied objects should be copies and not the same reference", mMergingMap.deep2 == mMergeResult.deep2);
	
	assertTrue("should be ferret:trousers", mOrgMap.deep.ferret == mMergeResult.deep.ferret);
	assertTrue("should be funky:chicken", mMergingMap.deep2.funky == mMergeResult.deep2.funky);
};

MapUtilityTest.prototype.test_deepMergeAllowingDuplicatesTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = {"ferret":"trousers"};
	
	var mMergingMap = {"hello":"world", "goodbye":"universe"};
	mMergingMap.deep = {"cow": "pat"};
	mMergingMap.deep2 = {"funky":"chicken"};

	// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
	var mMergeResult = MapUtility.mergeMaps([mOrgMap,mMergingMap], true, false, true);
	
	assertTrue("should be fish:pie", mOrgMap.fish == mMergeResult.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mMergeResult.monkey);
	
	assertFalse("deep copied objects should be copies and not the same reference", mOrgMap.deep == mMergeResult.deep);
	assertFalse("deep2 copied objects should be copies and not the same reference", mMergingMap.deep2 == mMergeResult.deep2);

	assertTrue("should be ferret:trousers", mOrgMap.deep.ferret == mMergeResult.deep.ferret);
	assertTrue("should be funky:chicken", mMergingMap.deep.cow == mMergeResult.deep.cow);
	assertTrue("should be funky:chicken", mMergingMap.deep2.funky == mMergeResult.deep2.funky);
};

MapUtilityTest.prototype.test_shallowCopyTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = mOrgMap; // if a deep copy were to happen this may cause stack overflow
	var mCopy = MapUtility.copy(mOrgMap);
	assertTrue("should be fish:pie", mOrgMap.fish == mCopy.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mCopy.monkey);
	assertTrue("shallow copied objects should be reference", mOrgMap.deep == mCopy.deep);
	
	assertEquals("sizes differ", MapUtility.size(mOrgMap), MapUtility.size(mCopy));
};

MapUtilityTest.prototype.test_deepCopyTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = {"ferret":"trousers"};

	var mCopy = MapUtility.copy(mOrgMap, {}, true);
	assertTrue("should be fish:pie", mOrgMap.fish == mCopy.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mCopy.monkey);
	assertFalse("deep copied objects should be copies and not the same reference", mOrgMap.deep == mCopy.deep);
	assertTrue("should be ferret:trousers", mOrgMap.deep.ferret == mCopy.deep.ferret);
	
	assertTrue("sizes differ", MapUtility.size(mOrgMap) == MapUtility.size(mCopy));
};

MapUtilityTest.prototype.test_hasAllKeys_mapsContainSameKeys = function()
{
	var mSource = {a:1, b:2, c:3};
	var mMap = {a:4, b:5, c:6};

	assertTrue(MapUtility.hasAllKeys(mSource, mMap));
};

MapUtilityTest.prototype.test_hasAllKeys_mapsDoesntContainSameKeys = function()
{
	var mSource = {a:1, b:2, d:3};
	var mMap = {a:4, b:5, c:6};

	assertFalse(MapUtility.hasAllKeys(mSource, mMap));
};

MapUtilityTest.prototype.test_hasAllKeys_mapsContainNullKeys = function()
{
	var mSource = {a:null, b:2, c:3};
	var mMap = {a:4, b:5, c:6};

	assertTrue(MapUtility.hasAllKeys(mSource, mMap));
};

MapUtilityTest.prototype.test_hasAllKeys_emptyArguments = function()
{
	// test both empty
	var mSource = {};
	var mMap = {};

	assertTrue(MapUtility.hasAllKeys(mSource, mMap));

	// test first empty
	mSource = {};
	mMap = {a:1};

	assertFalse(MapUtility.hasAllKeys(mSource, mMap));

	// test second empty
	mSource = {a:1};
	mMap = {};

	assertTrue(MapUtility.hasAllKeys(mSource, mMap));
};
