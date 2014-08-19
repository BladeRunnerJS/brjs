'use strict';

/**
* This is a utility and does not need to be constructed.
* @module br/util/MapFactory
*/


/**
 * A constant that defines the number of removals (as performed with {@link .removeItem}) before the map is replaced
 *  with a new one.
 * @final
 * @type int
 */
var REMOVAL_LIMIT = 1000;

/**
 * @alias module:br/util/MapFactory
 *
 * @classdesc
 * Utility class that prevents memory leaks in IE when using long running maps that are added to and removed from over
 * time.
 *
 * <p><b>Using this class has a penalty in performance and code beauty. Only use this when you are keeping a map for a
 *  long time and will be removing items and adding items with new names (not previously used) repeatedly.</b></p>
 *
 * <p>Users of this class should not perform any browser detection themselves, as this is handled internally, and normal
 * maps are returned for browsers that do not leak memory.</p>
 */
var MapFactory = {};

/**
 * Creates a map that doesn't leak when used in conjunction with {@link .removeItem}.
 *
 * @return A specially modified map object that allows the <code>MapFactory</code> to determine when any clean-up
 *  should occur, or a normal map object for browsers that do not leak.
 * @type Object
 */
MapFactory.createMap = function() {
	return {};
};

/**
 * Removes an item from the map.
 *
 * <p>If cleanup occurs, then the map will be replaced with a completely new one, and so any references to the map must
 *  be reset whenever this method is called, for example:
 *  <code>this.m_mMap = MapFactory.removeItem(this.m_mMap, sItemKey);</code></p>
 *
 * @param {Object} mapToRemoveFrom The map the item will be removed from.
 * @param {String} keyToRemove The key of the item to be removed from the map.
 * @return {Object} A reference to the updated map &mdash; typically this will just be <code>mapToRemoveFrom</code>,
 *  but may be a new map reference if the map has been replaced with a new instance.
 */
MapFactory.removeItem = function(mapToRemoveFrom, keyToRemove) {
	delete mapToRemoveFrom[keyToRemove];

	if (mapToRemoveFrom.constructor.deleteCount++ >= REMOVAL_LIMIT) {
		var newMap = MapFactory.createMap();

		for (var item in mapToRemoveFrom) {
			newMap[item] = mapToRemoveFrom[item];
		}

		mapToRemoveFrom = newMap;
	}

	return mapToRemoveFrom;
};

module.exports = MapFactory;
