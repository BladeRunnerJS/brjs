;(function() {
	"use strict";

	var Errors = require('br/Errors');

	/**
	 * This is a static class that never needs to be instantiated.
	 * @constructor
	 * 
	 * @class
	 * Utility class providing common operations on maps.
	 * 
	 * <p>In the context of this class, a <code>Map</code> is considered to be anything that is an
	 * instance of <code>Object</code>.</p>
	 */
	var MapUtility = {}
	
	/** @private */
	MapUtility.m_nToStringRecursionCount = 0;

	/** @private */
	MapUtility.m_pMapsProcessedByToString = [];

	/**
	 * Returns <code>true</code> if the given map is empty (has no enumerable properties), and <code>false</code> otherwise.
	 * 
	 * @param {Object} mMap The map that may or may not be empty.
	 * @type boolean
	 */
	MapUtility.isEmpty = function(mMap) {
		for(var sKey in mMap)
		{
			return false;
		}
		
		return true;
	};

	/**
	 * Returns the number of enumerable items within the given map.
	 * 
	 * <p>If you find yourself using this method you should consider whether a map is the correct data structure.</p>
	 * 
	 * @param {Object} mMap The map the size is required for.
	 * @return {number} The number of items within the map.
	 */
	MapUtility.size = function(mMap)
	{
		var nSize = 0;
		
		for(var sKey in mMap)
		{
			++nSize;
		}
		
		return nSize;
	};

	/**
	 * Returns an array containing the values obtained by iterating the given map object.
	 * 
	 * @param {Object} mMap The map to iterate
	 * @returns {Array} an array of all the enumerable values in the map.
	 */
	MapUtility.valuesToArray = function(mMap)
	{
		var pArray = [];
		
		for(var sKey in mMap)
		{
			pArray.push(mMap[sKey]);
		}
		
		return pArray;
	};

	/**
	 * Add the items within the given array to the map, using each item as a key pointing to the
	 * boolean value <code>true</code>. This will overwrite the value for a key that is already defined
	 * within the specified map.
	 * 
	 * <p>As an example, the array <code>["foo", "bar"]</code> would be converted to the map
	 * <code>{foo:true, bar:true}</code>.</p>
	 * 
	 * <p>This method is useful in that it allows a number of arrays to be condensed into a list of the unique values within
	 * this set of arrays. Once all the arrays have been added, then the {@link #keysToArray} method may be used to
	 * convert this list of unique entries back to an array.</p>
	 * 
	 * @see #removeArrayFromMap
	 * 
	 * @param {Object} mMap The map to add the items to.  May not be null or undefined
	 * @param {Array} pArray The array that contains the map keys to add.  May not be null or undefined.
	 * 
	 * @returns {Object} the passed map.
	 */
	MapUtility.addArrayToMap = function(mMap, pArray)
	{
		for(var i = 0, l = pArray.length; i < l; ++i)
		{
			mMap[pArray[i]] = true;
		}
		
		return mMap;
	};

	/**
	 * Remove all entries within the specified map, whose keys are contained within the given array.
	 * Keys included in the array that do not exist within the map will be ignored.
	 * 
	 * @see #addArrayToMap
	 * 
	 * @param {Object} mMap The map to remove the items from.
	 * @param {Array} pArray The array that contains the map keys to remove.
	 * @return {Object} A map containing all name/value pairs that have been removed from the specified map.
	 */
	MapUtility.removeArrayFromMap = function(mMap, pArray)
	{
		var mDeletedEntries = {};
		for(var i = 0, l = pArray.length; i < l; ++i)
		{
			var vEntry = mMap[pArray[i]];
			if (delete mMap[pArray[i]])
			{
				// if delete is successful then add the deleted value to the map of deleted entries
				mDeletedEntries[pArray[i]] = vEntry;
			}
		}
		return mDeletedEntries;
	};

	/**
	 * Returns a string representation of the map in the form:
	 * 
	 * <pre>
	 * map#1{ a: 1, b: 2, c: 3, ... }
	 * </pre>
	 * 
	 * <p>The values contained within the map will be returned as per the value of their
	 * <code>toString()</code> method. Another map will be displayed as <code>[object Object]</code>,
	 * as will any object that does not explicitly implement or inherit a <code>toString()</code>
	 * method.</p>
	 * 
	 * <p>This method can be invoked multiple times from within the same callstack, such that if an
	 * object contained within the map implements a <code>toString()</code> method that calls it, there
	 * can be no infinite recursion. For example, if the object contained a reference to another map,
	 * the output would be of the form:</p>
	 * 
	 * <pre>
	 * map#1{ obj: myObject&lt;map#2{ x: 24, y: 25, z: 26 }&gt; }
	 * </pre>
	 * 
	 * <p>Whilst if the object contains a circular reference back to the map, the output will be of the
	 * form:</p>
	 * 
	 * <pre>
	 * map#1{ obj: myObject&lt;map#1&lt;see-earlier-definition&gt;&gt; }
	 * </pre>
	 * 
	 * <i>Warning: The output from this method will become unreliable if the <code>toString()</code>
	 * method of any of the values contained within the specified map throw an exception, however it
	 * is strongly advised that <code>toString()</code> methods should never throw exceptions.</i>
	 * 
	 * @param {Object} mMap The map to be converted to a String.
	 * @return {String} A string representation of the specified map.
	 */
	MapUtility.toString = function(mMap)
	{
		var sSerializedState = "";
		
		var nMapIndex = this._getMapIndex(mMap);
		
		// has this map been already been processed
		if (nMapIndex !== -1)
		{
			sSerializedState += "map#"+ nMapIndex + "{<see-earlier-definition>}";
		}
		else
		{
			this.m_nToStringRecursionCount++;
			
			this.m_pMapsProcessedByToString.push(mMap);
			nMapIndex = this.m_pMapsProcessedByToString.length;
			
			sSerializedState += "map#"+ nMapIndex + "{";
			var bFirst = true;
			for (var sKey in mMap)
			{
				sSerializedState += (bFirst?"":",") + " " + sKey + ": " + mMap[sKey];
				if (bFirst === true)
				{
					bFirst = false;
				}
			}
			sSerializedState += " }";
			
			this.m_nToStringRecursionCount--;
			
			if (this.m_nToStringRecursionCount === 0)
			{
				// toString() has completed, clear down all the temporary flags that were set on the maps
				// that were processed by this method
				for (var i = 0, nLength = this.m_pMapsProcessedByToString.length; i < nLength; ++i)
				{
					delete (this.m_pMapsProcessedByToString[i].constructor.toStringMapIndex);
				}
				
				// clear down the array
				this.m_pMapsProcessedByToString = [];
			}
		}
		
		return sSerializedState;
	};

	/**
	 * Gets the index that was associated with the specified map when it was output by the
	 * {@link #toString} method during the current callstack invocation, or returns a code indicating
	 * that the map has not been processed previously.
	 * 
	 * @param {Object} mMap The map the index is required for.
	 * @return {Number} The index of the map, or <code>-1</code> if this map has not been processed before.
	 * @private
	 */
	MapUtility._getMapIndex = function(mMap)
	{
		var nIndex = -1;
		for (var i = 0, nLength = this.m_pMapsProcessedByToString.length; i < nLength; ++i)
		{
			if (this.m_pMapsProcessedByToString[i] === mMap)
			{
				// first map has the index 1
				nIndex = i + 1;
				break;
			}
		}
		return nIndex;
	};

	/**
	 * Merges all of the maps specified in the array into a new map.
	 * 
	 * <p>The default behaviour of this method is to throw an exception if two maps contain the same
	 * key, however these duplicates can be ignored by setting the optional
	 * <code>bOverwriteDuplicates</code> argument to <code>true</code>. In this case the value of the
	 * key within the merged map will be that of the last map to contain the key. For example, merging
	 * <code>[ { a: "1" }, { a: "2" } ]</code> would result in the map  <code>{ a: "2" }</code>.
	 * 
	 * @param {Array} pMapsToMerge An array of all the maps to be merged.
	 * @param {boolean} bOverwriteDuplicates (Optional) Flag that can be set to force this method to
	 *		ignore duplicate keys and overwrite their values. If omitted this argument defaults to
	 *		<code>false</code>.
	 * @param {Boolean} bDuplicatesThrowsExceptions (Optional) Defaults to <code>true</code>. Indicates if an exception should be thrown if a duplicate value is found
	 *			and the method is not to overwrite duplicates. This should be used if the original values should be preserved
	 *			and not overwritten. If <code>bOverwriteDuplicates</code> is set to <code>true</code> then this parameter is ignored.
	 * @param {Boolean} bDeepCopy (Optional) Defaults to <code>false</code>, shallow copy.
	 *			Identifies if map objects should have deep copy applied to them.
	 * @type Object
	 * @return A new map containing the merged key/value pairs from each of the specified maps.
	 * @throws {br.util.Error} if one or more of the contents of the maps to merge array
	 *			is not a <code>Map</code>, or if any duplicate keys are found and the
	 *			<code>bOverwriteDuplicates</code> argument is <code>false</code>.
	 */
	MapUtility.mergeMaps = function(pMapsToMerge, bOverwriteDuplicates, bDuplicatesThrowsExceptions, bDeepCopy)
	{
		bDuplicatesThrowsExceptions = (bDuplicatesThrowsExceptions===undefined?true:bDuplicatesThrowsExceptions);
		bDeepCopy = (bDeepCopy===undefined?false:bDeepCopy);
		
		var mMergedMap = {};
		for (var i = 0, nLength = pMapsToMerge.length; i < nLength; ++i)
		{
			if (!(pMapsToMerge[i] instanceof Object)) {
				throw new Errors.InvalidParametersError("Failed to merge maps; one of the specified maps was of an invalid type");
			}
			for (var sKey in pMapsToMerge[i]) {
				if (bOverwriteDuplicates !== true && mMergedMap[sKey] !== undefined) {
					if (bDuplicatesThrowsExceptions) {
						throw new Errors.InvalidParametersError("Failed to merge maps due to a duplicate key \"" + sKey + "\": conflicting values \"" + mMergedMap[sKey] + "\"/\"" + pMapsToMerge[i][sKey] + "\"");
					}
					// do not overwrite the value, keep the original and continue with next value
					continue;
				}
				
				if( typeof mMergedMap[sKey] == "object" && typeof pMapsToMerge[i][sKey] == "object" && bDeepCopy ) {
					mMergedMap[sKey] = this.mergeMaps([pMapsToMerge[i][sKey], mMergedMap[sKey]], bDuplicatesThrowsExceptions, bDuplicatesThrowsExceptions, true);
				} else if( !mMergedMap[sKey] && typeof pMapsToMerge[i][sKey] == "object" && bDeepCopy ) {
					mMergedMap[sKey] = this.copy( pMapsToMerge[i][sKey], {}, true );
				} else {
					// shallow copy
					mMergedMap[sKey] = pMapsToMerge[i][sKey];
				}
			}
		}
		return mMergedMap;
	};

	/**
	 * @private
	 * Converts a map to its inverse, which has keys based on the original map's values, and vice-versa.
	 */
	MapUtility.invert = function(mMap) {
		var mInverse = {};
		for (var sName in mMap) {
			var sValue = mMap[sName];
			mInverse[sValue] = sName;
		}
		return mInverse;
	};

	// TODO: determine whether copy() and mergeMaps() be combined into a more useful method
	/**
	 * @private
	 *
	 * Creates a shallow copy of the supplied map.  If the destination map is supplied, then it adds
	 * the map values onto the destination map.
	 *
	 * @param {Object} mSource
	 * @param {Object} mDestination (optional)
	 * @param {Boolean} bDeepCopy indicates whether a deep copy will occur on the Map.
	 */
	MapUtility.copy = function(mSource, mDestination, bDeepCopy) {
		mDestination = mDestination || {};
		for (var sKey in mSource)
		{
			if( bDeepCopy && typeof mSource[sKey] == "object" )
			{
				mDestination[sKey] = this.deepClone(mSource[sKey]);
			}
			else
			{
				mDestination[sKey] = mSource[sKey];
			}
		}
		return mDestination;
	};

	/**
	 * Creates a shallow clone of the supplied map.  Map references are copied one level deep.
	 *
	 * @param {Object} mSource  The map to clone.
	 * @type Object
	 * @return  A shallow clone of the map.
	 */
	MapUtility.clone = function(mSource) {
		var mClone = {};
		for (var sKey in mSource) {
			mClone[sKey] = mSource[sKey];
		}
		return mClone;
	};

	/**
	 * @private
	 * 
	 * Creates a deep clone of the supplied map.  Map references are copied to an arbitrary number of levels deep
	 * (note that non-map objects are not handled correctly)
	 *
	 * @param {Object} mSource   The map to clone.
	 * @type  Object
	 * @return  A deep clone of the map.
	 */
	MapUtility.deepClone = function(mSource) {
		var mClone = {};
		for (var sKey in mSource) {
			mClone[sKey] = typeof mSource[sKey] == "object" ? this.deepClone(mSource[sKey]) : mSource[sKey];
		}
		return mClone;
	};

	/**
	 * Helper method to check if parameter values passed in to methods are members of the enumerations they are meant to be.
	 * BEWARE: The check is whether oItem is a value on a member of the object, such as an entry in an Array.
	 *
	 * @param oItem exact instance that must be equal(===) to one of the members.
	 * @param oObject the object that will have its members checked.
	 * @private
	 */
	MapUtility.isMemberValueOf = function(oItem, oObject) {
		var bMemberOf = false;
		
		for(var sKey in oObject)
		{
			var oNextItem = oObject[sKey];
			
			if(oItem === oNextItem)
			{
				bMemberOf = true;
				break;
			}
		}
		
		return bMemberOf;
	};

	/**
	 * Returns true if the source map contains all the keys of the given map.
	 * 
	 * @param {Object} mSource The map you are checking
	 * @param {Object} mMap The map you are using the check against
	 * @return {Boolean}
	 */
	MapUtility.hasAllKeys = function(mSource, mMap)
	{
		return Object.keys(mMap).every(function(sMapKey) {
			return mSource[sMapKey] !== undefined;
		});
	};

	br.util.MapUtility = MapUtility;
})();