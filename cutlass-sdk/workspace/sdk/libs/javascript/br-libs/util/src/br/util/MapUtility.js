;(function() {
	'use strict';

	var Errors = require('br/Errors');

	/**
	 * This is a static class that never needs to be instantiated.
	 * @constructor
	 *
	 * @class
	 * Utility class providing common operations on maps.
	 *
	 * <p>In the context of this class, a <code>Map</code> is considered to be anything that is an instance of
	 *  <code>Object</code>.</p>
	 */
	var MapUtility = {};

	/** @private */
	MapUtility.m_nToStringRecursionCount = 0;

	/** @private */
	MapUtility.m_pMapsProcessedByToString = [];

	/**
	 * Returns <code>true</code> if the given map is empty (has no enumerable properties), and <code>false</code> otherwise.
	 *
	 * @param {Object} srcMap The map that may or may not be empty.
	 * @returns {boolean}
	 */
	MapUtility.isEmpty = function(srcMap) {
		for (var key in srcMap) {
			if (srcMap.hasOwnProperty(key)) {
				return false;
			}
		}

		return true;
	};

	/**
	 * Returns the number of enumerable items within the given map.
	 *
	 * <p>If you find yourself using this method you should consider whether a map is the correct data structure.</p>
	 *
	 * @param {Object} srcMap The map the size is required for.
	 * @returns {number} The number of items within the map.
	 */
	MapUtility.size = function(srcMap) {
		var size = 0;

		for (var key in srcMap) {
			if (srcMap.hasOwnProperty(key)) {
				size += 1;
			}
		}

		return size;
	};

	/**
	 * Returns an array containing the values obtained by iterating the given map object.
	 *
	 * @param {Object} srcMap The map to iterate
	 * @returns {Array} an array of all the enumerable values in the map.
	 */
	MapUtility.valuesToArray = function(srcMap) {
		var values = [];

		for (var key in srcMap) {
			if (srcMap.hasOwnProperty(key)) {
				values.push(srcMap[key]);
			}
		}

		return values;
	};

	/**
	 * Add the items within the given array to the map, using each item as a key pointing to the boolean value
	 *  <code>true</code>. This will overwrite the value for a key that is already defined within the specified map.
	 *
	 * <p>As an example, the array <code>['foo', 'bar']</code> would be converted to the map
	 * <code>{foo:true, bar:true}</code>.</p>
	 *
	 * <p>This method is useful in that it allows a number of arrays to be condensed into a list of the unique values
	 *  within this set of arrays. Once all the arrays have been added, then the {@link #keysToArray} method may be used
	 *  to convert this list of unique entries back to an array.</p>
	 *
	 * @see #removeArrayFromMap
	 *
	 * @param {Object} tgtMap The map to add the items to. May not be null or undefined
	 * @param {Array} keys The array that contains the map keys to add. May not be null or undefined.
	 *
	 * @returns {Object} the passed map.
	 */
	MapUtility.addArrayToMap = function(tgtMap, keys) {
		for (var i = 0, len = keys.length; i < len; ++i) {
			tgtMap[keys[i]] = true;
		}

		return tgtMap;
	};

	/**
	 * Remove all entries within the specified map, whose keys are contained within the given array.
	 * Keys included in the array that do not exist within the map will be ignored.
	 *
	 * @see #addArrayToMap
	 *
	 * @param {Object} tgtMap The map to remove the items from.
	 * @param {Array} keys The array of keys to remove from the map.
	 * @returns {Object} A map containing all name/value pairs that have been removed from the specified map.
	 */
	MapUtility.removeArrayFromMap = function(tgtMap, keys) {
		var deleted = {},
			key, value;

		for(var i = 0, len = keys.length; i < len; ++i) {
			key = keys[i];
			value = tgtMap[key];
			if (delete tgtMap[key]) {
				// if delete is successful then add the deleted value to the map of deleted entries
				deleted[key] = value;
			}
		}
		return deleted;
	};

	/**
	 * Returns a string representation of the map in the form:
	 *
	 * <pre>
	 * map#1{ a: 1, b: 2, c: 3, ... }
	 * </pre>
	 *
	 * <p>The values contained within the map will be returned as per the value of their <code>toString()</code> method.
	 *  Another map will be displayed as <code>[object Object]</code>, as will any object that does not explicitly implement
	 *  or inherit a <code>toString()</code> method.</p>
	 *
	 * <p>This method can be invoked multiple times from within the same callstack, such that if an object contained within
	 *  the map implements a <code>toString()</code> method that calls it, there can be no infinite recursion. For example,
	 *  if the object contained a reference to another map, the output would be of the form:</p>
	 *
	 * <pre>
	 * map#1{ obj: myObject&lt;map#2{ x: 24, y: 25, z: 26 }&gt; }
	 * </pre>
	 *
	 * <p>Whilst if the object contains a circular reference back to the map, the output will be of the form:</p>
	 *
	 * <pre>
	 * map#1{ obj: myObject&lt;map#1&lt;see-earlier-definition&gt;&gt; }
	 * </pre>
	 *
	 * <i>Warning: The output from this method will become unreliable if the <code>toString()</code> method of any of the
	 *  values contained within the specified map throw an exception, however it is strongly advised that
	 *  <code>toString()</code> methods should never throw exceptions.</i>
	 *
	 * @param {Object} srcMap The map to be converted to a String.
	 *
	 * @returns {String} A string representation of the specified map.
	 */
	MapUtility.toString = function(srcMap) {
		var serialized = '',
			mapIdx = this._getMapIndex(srcMap);

		// has this map been already been processed
		if (mapIdx !== -1) {
			serialized += 'map#'+ mapIdx + '{<see-earlier-definition>}';
		} else {
			this.m_nToStringRecursionCount += 1;

			this.m_pMapsProcessedByToString.push(srcMap);
			mapIdx = this.m_pMapsProcessedByToString.length;

			serialized += 'map#'+ mapIdx + '{';
			var isFirst = true;
			for (var key in srcMap) {
				serialized += (isFirst ? '' : ',') + ' ' + key + ': ' + srcMap[key];
				if (isFirst === true) {
					isFirst = false;
				}
			}
			serialized += ' }';

			this.m_nToStringRecursionCount -= 1;

			if (this.m_nToStringRecursionCount === 0) {
				// toString() has completed, clear down all the temporary flags that were set on the maps
				// that were processed by this method
				for (var i = 0, len = this.m_pMapsProcessedByToString.length; i < len; ++i) {
					delete (this.m_pMapsProcessedByToString[i].constructor.toStringMapIndex);
				}

				// clear down the array
				this.m_pMapsProcessedByToString = [];
			}
		}

		return serialized;
	};

	/**
	 * @private
	 * Gets the index that was associated with the specified map when it was output by the {@link #toString} method
	 *  during the current callstack invocation, or returns a code indicating that the map has not been processed
	 *  previously.
	 *
	 * @param {Object} srcMap The map the index is required for.
	 * @returns {Number} The index of the map, or <code>-1</code> if this map has not been processed before.
	 */
	MapUtility._getMapIndex = function(srcMap) {
		var index = -1;
		for (var i = 0, len = this.m_pMapsProcessedByToString.length; i < len; ++i) {
			if (this.m_pMapsProcessedByToString[i] === srcMap) {
				// first map has the index 1
				index = i + 1;
				break;
			}
		}
		return index;
	};

	/**
	 * Merges all of the maps specified in the array into a new map.
	 *
	 * <p>The default behaviour of this method is to throw an exception if two maps contain the same key, however these
	 *  duplicates can be ignored by setting the optional <code>overwriteDuplicateKeys</code> argument to
	 *  <code>true</code>. In this case the value of the key within the merged map will be that of the last map to
	 *  contain the key. For example, merging <code>[ { a: '1' }, { a: '2' } ]</code> would result in the map
	 *  <code>{ a: '2' }</code>.</p>
	 *
	 * @param {Array} mergeMapArr An array of all the maps to be merged.
	 * @param {boolean} overwriteDuplicateKeys (Optional) Flag that can be set to force this method to ignore duplicate
	 *  keys and overwrite their values. If omitted this argument defaults to <code>false</code>.
	 * @param {boolean} throwOnDuplicateKey (Optional) Defaults to <code>true</code>. Indicates if an exception
	 *  should be thrown if a duplicate value is found and the method is not to overwrite duplicates. This should be
	 *  used if the original values should be preserved and not overwritten. If <code>overwriteDuplicateKeys</code> is set
	 *  to <code>true</code> then this parameter is ignored.
	 * @param {boolean} isDeepCopy (Optional) Defaults to <code>false</code>, shallow copy. Identifies if map objects
	 *  should have deep copy applied to them.
	 *
	 * @returns {Object} A new map containing the merged key/value pairs from each of the specified maps.
	 *
	 * @throws {br.util.Error} if one or more of the contents of the maps to merge array is not a <code>Map</code>, or
	 *  if any duplicate keys are found and the <code>overwriteDuplicateKeys</code> argument is <code>false</code>.
	 */
	MapUtility.mergeMaps = function(mergeMapArr, overwriteDuplicateKeys, throwOnDuplicateKey, isDeepCopy) {
		throwOnDuplicateKey = (typeof throwOnDuplicateKey === 'undefined' ? true : throwOnDuplicateKey);
		isDeepCopy = (typeof isDeepCopy === 'undefined' ? false : isDeepCopy);

		var merged = {};
		for (var i = 0, len = mergeMapArr.length; i < len; ++i) {
			if (!(mergeMapArr[i] instanceof Object)) {
				throw new Errors.InvalidParametersError('Failed to merge maps; one of the specified maps was of an invalid type');
			}

			for (var key in mergeMapArr[i]) {
				if (overwriteDuplicateKeys !== true && typeof merged[key] !== 'undefined') {
					if (throwOnDuplicateKey) {
						throw new Errors.InvalidParametersError('Failed to merge maps due to a duplicate key \'' + key + '\': conflicting values \'' + merged[key] + '\'/\'' + mergeMapArr[i][key] + '\'');
					}
					// do not overwrite the value, keep the original and continue with next value
					continue;
				}

				if (typeof merged[key] === 'object' && typeof mergeMapArr[i][key] == 'object' && isDeepCopy ) {
					merged[key] = this.mergeMaps([mergeMapArr[i][key], merged[key]], throwOnDuplicateKey, throwOnDuplicateKey, true);
				} else if (!merged[key] && typeof mergeMapArr[i][key] == 'object' && isDeepCopy ) {
					merged[key] = this.copy(mergeMapArr[i][key], {}, true );
				} else {
					// shallow copy
					merged[key] = mergeMapArr[i][key];
				}
			}
		}
		return merged;
	};

	/**
	 * @private
	 * Converts a map to its inverse, which has keys based on the original map's values, and vice-versa.
	 */
	MapUtility.invert = function(srcMap) {
		var inverted = {};
		for (var key in srcMap) {
			inverted[srcMap[key]] = key;
		}

		return inverted;
	};

	// TODO: determine whether copy() and mergeMaps() be combined into a more useful method
	/**
	 * @private
	 * Creates a shallow copy of the supplied map. If the destination map is supplied, then it adds the map values onto
	 *  the destination map.
	 *
	 * @param {Object} srcMap
	 * @param {Object} tgtMap (optional)
	 * @param {Boolean} isDeepCopy indicates whether a deep copy will occur on the Map.
	 */
	MapUtility.copy = function(srcMap, tgtMap, isDeepCopy) {
		tgtMap = tgtMap || {};
		for (var key in srcMap) {
			if (isDeepCopy && typeof srcMap[key] === 'object') {
				tgtMap[key] = this.deepClone(srcMap[key]);
			} else {
				tgtMap[key] = srcMap[key];
			}
		}
		return tgtMap;
	};

	/**
	 * Creates a shallow clone of the supplied map. Map references are copied one level deep.
	 *
	 * @param {Object} srcMap The map to clone.
	 *
	 * @returns {Object} A shallow clone of the map.
	 */
	MapUtility.clone = function(srcMap) {
		var clone = {};
		for (var key in srcMap) {
			clone[key] = srcMap[key];
		}
		return clone;
	};

	/**
	 * @private
	 * Creates a deep clone of the supplied map. Map references are copied to an arbitrary number of levels deep (note
	 *  that non-map objects are not handled correctly)
	 *
	 * @param {Object} srcMap The map to clone.
	 *
	 * @returns {Object} A deep clone of the map.
	 */
	MapUtility.deepClone = function(srcMap) {
		var clone = {};
		for (var key in srcMap) {
			clone[key] = typeof srcMap[key] === 'object' ? this.deepClone(srcMap[key]) : srcMap[key];
		}
		return clone;
	};

	/**
	 * @private
	 * Helper method to check if parameter values passed in to methods are members of the enumerations they are meant to
	 *  be. BEWARE: The check is whether <code>item</code> is a value on a member of the object, such as an entry in an
	 *  Array.
	 *
	 * @param item exact instance that must be equal(===) to one of the members.
	 * @param srcObj the object that will have its members checked.
	 */
	MapUtility.isMemberValueOf = function(item, srcObj) {
		var isMember = false,
			srcItem;

		for (var key in srcObj) {
			srcItem = srcObj[key];

			if (item === srcItem) {
				isMember = true;
				break;
			}
		}

		return isMember;
	};

	/**
	 * Returns true if the source map contains all the keys of the given map.
	 *
	 * @param {Object} tgtMap The map you are checking
	 * @param {Object} srcMap The map you are using the check against
	 * @returns {Boolean}
	 */
	MapUtility.hasAllKeys = function(tgtMap, srcMap) {
		return Object.keys(srcMap).every(function(key) {
			return typeof tgtMap[key] !== 'undefined';
		});
	};

	br.util.MapUtility = MapUtility;
})();