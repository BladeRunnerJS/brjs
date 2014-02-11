br.Core.thirdparty('jsunitextensions');

MapUtilityTest = TestCase("MapUtilityTest");

MapUtilityTest.prototype.test_isEmpty = function()
{
	assertTrue("a1", br.util.MapUtility.isEmpty({}));
	assertFalse("a2", br.util.MapUtility.isEmpty({foo:1}));
	assertFalse("a3", br.util.MapUtility.isEmpty({foo:1, bar:2}));
};

MapUtilityTest.prototype.test_size = function()
{
	assertEquals("a1", 0, br.util.MapUtility.size({}));
	assertEquals("a2", 1, br.util.MapUtility.size({foo:1}));
	assertEquals("a3", 2, br.util.MapUtility.size({foo:1, bar:2}));
};

MapUtilityTest.prototype.test_valuesToAray = function()
{
	assertArrayEquals("a1", [], br.util.MapUtility.valuesToArray({}));
	assertArrayEquals("a2", [1, 2], br.util.MapUtility.valuesToArray({foo:1, bar:2}));
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
	var mMap = br.util.MapUtility.addArrayToMap({}, ["foo", "bar"]);
	
	assertEquals("a1", {foo: true, bar: true}, mMap);
};

MapUtilityTest.prototype.test_addArrayOverwritesExisitingValueInMap = function()
{
	verifyMapAddition("a1", {foo:true}, {foo:false}, ["foo"]);
};

function verifyMapAddition(sMessage, mExpectedMap, mTestMap, pTestArray)
{
	br.util.MapUtility.addArrayToMap(mTestMap, pTestArray);
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
	var mDeletedMap = br.util.MapUtility.removeArrayFromMap(mTestMap, pTestArray);
	assertMapEquals(sMessage + " - specified map did not contain the expected entries", mExpectedMap, mTestMap);
	assertMapEquals(sMessage + " - deleted map did not contain the expected entries", mExpectedDeletionsMap, mDeletedMap);
}

MapUtilityTest.prototype.test_mapsDefinedLiterallyCanBeMerged = function()
{
	var mMap1 = { a: "one", b: "two" };
	var mMap2 = { c: "three" };
	
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", { a: "one", b: "two", c: "three" }, mMergedMap);
};

MapUtilityTest.prototype.test_mapsDefinedAsNewObjectsCanBeMerged = function()
{
	var mMap1 = new Object();
	mMap1.a = "A";
	mMap1.b = "B";
	var mMap2 = new Object();
	mMap2["c"] = "C";
	
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", { a: "A", b: "B", c: "C" }, mMergedMap);
};

MapUtilityTest.prototype.test_emptyMapsCanBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = {};
	
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2]);
	
	assertMapEquals("1.1", {}, mMergedMap);
};

MapUtilityTest.prototype.test_manyMapsCanBeMerged = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { c: "3" };
	var mMap3 = { d: "4", e: "5" };
	var mMap4 = {};
	var mMap5 = { f: "6", g: "7", h: "8", i: "9" };
	
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2, mMap3, mMap4, mMap5]);
	
	assertMapEquals("1.1", { a: "1", b: "2", c: "3", d: "4", e: "5", f: "6", g: "7", h: "8", i: "9" }, mMergedMap);
};

MapUtilityTest.prototype.test_exceptionIsThrownIfNonMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = "";
	
	assertException("1.1", 
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfNullMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = null;
	
	assertException("1.1", 
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfUndefinedMapIsAttemptedToBeMerged = function()
{
	var mMap1 = {};
	var mMap2 = undefined;
	
	assertException("1.1", 
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFound = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	assertException("1.1", 
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};


MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFoundAndThrowExceptionFlagIsSet = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	assertException("1.1", 
				// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2], false, true); },
				"InvalidParametersError");
}

MapUtilityTest.prototype.test_exceptionIsThrownIfDuplicateIsFoundEvenIfValuesAreIdentical = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "2" };
	
	assertException("1.1", 
				function() { br.util.MapUtility.mergeMaps([mMap1, mMap2]); },
				"InvalidParametersError");
};

MapUtilityTest.prototype.test_duplicateKeysCanBeMergedWhenFlagIsSet = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2], true);
	
	assertMapEquals("1.1", { a: "1", b: "3" }, mMergedMap);
};

MapUtilityTest.prototype.test_duplicateKeysAreIgnoredWhenThrowExceptionFlagIsSetToFalse = function()
{
	var mMap1 = { a: "1", b: "2" };
	var mMap2 = { b: "3" };
	
	// pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy
	var mMergedMap = br.util.MapUtility.mergeMaps([mMap1, mMap2], false, false);
	
	assertMapEquals("original map value should have been kept", { a: "1", b: "2" }, mMergedMap);
};

MapUtilityTest.prototype.test_toStringWithSingleStringNameValuePairMap = function()
{
	var mMap = { a: "1" };
	
	assertEquals("1.1", "map#1{ a: 1 }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithSeveralStringNameValuePairsMap = function()
{
	var mMap = { a: "1", b: "2", c: "3" };
	
	assertEquals("1.1", "map#1{ a: 1, b: 2, c: 3 }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnotherMap = function()
{
	var mSubMap = { x: "24", y: "25", z: "26" };
	var mMap = { a: "1", b: "2", c: "3", nextMap: mSubMap };
	
	assertEquals("1.1", "map#1{ a: 1, b: 2, c: 3, nextMap: [object Object] }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithNoToStringMethod = function()
{
	var oObject = {
		mMyMap: { x: "24", y: "25", z: "26" }
	};
	var mMap = { obj: oObject };
	
	assertEquals("1.1", "map#1{ obj: [object Object] }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithAReferenceToAnotherMap = function()
{
	var oObject = {
		mMyMap: { x: "24", y: "25", z: "26" },
		toString: function() {
			return "myObject<" + br.util.MapUtility.toString(this.mMyMap) + ">";
		}
	};
	var mMap = { obj: oObject };
	
	assertEquals("1.1", "map#1{ obj: myObject<map#2{ x: 24, y: 25, z: 26 }> }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_toStringWithAMapContainingAnObjectWithAReferenceBackToMap = function()
{
	var mMap = { };
	var oObject = {
		mMyMap: mMap,
		toString: function() {
			return "myObject(" + br.util.MapUtility.toString(this.mMyMap) + ")";
		}
	};
	mMap["obj"] = oObject;
	
	assertEquals("1.1", "map#1{ obj: myObject(map#1{<see-earlier-definition>}) }", br.util.MapUtility.toString(mMap));
};

MapUtilityTest.prototype.test_shallowMergeTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = mOrgMap; // if a deep copy were to happen this may cause stack overflow
	
	var mMergingMap = {"hello":"world", "goodbye":"universe"};
	
	var mMergeResult = br.util.MapUtility.mergeMaps([mOrgMap,mMergingMap] );
	
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
	var mMergeResult = br.util.MapUtility.mergeMaps([mOrgMap,mMergingMap], false, false, true);
	
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
	var mMergeResult = br.util.MapUtility.mergeMaps([mOrgMap,mMergingMap], true, false, true);
	
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
	var mCopy = br.util.MapUtility.copy(mOrgMap);
	assertTrue("should be fish:pie", mOrgMap.fish == mCopy.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mCopy.monkey);
	assertTrue("shallow copied objects should be reference", mOrgMap.deep == mCopy.deep);
	
	assertEquals("sizes differ", br.util.MapUtility.size(mOrgMap), br.util.MapUtility.size(mCopy));
};

MapUtilityTest.prototype.test_deepCopyTest = function()
{
	var mOrgMap = {"fish":"pie","monkey":"cheese"};
	mOrgMap.deep = {"ferret":"trousers"};

	var mCopy = br.util.MapUtility.copy(mOrgMap, {}, true);
	assertTrue("should be fish:pie", mOrgMap.fish == mCopy.fish);
	assertTrue("should be monkey:cheese", mOrgMap.monkey == mCopy.monkey);
	assertFalse("deep copied objects should be copies and not the same reference", mOrgMap.deep == mCopy.deep);
	assertTrue("should be ferret:trousers", mOrgMap.deep.ferret == mCopy.deep.ferret);
	
	assertTrue("sizes differ", br.util.MapUtility.size(mOrgMap) == br.util.MapUtility.size(mCopy));
};