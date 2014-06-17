;(function() {
	"use strict";

	// TODO: at the time of testing ie6, ie7 & ie8 beta2 all leak memory -- update this line to take the version into account once MS fixes this bug
	var browserMapsLeakMemory = (navigator.userAgent.match(/MSIE/) != undefined);

	/**
	 * A constant that defines the number of removals (as performed with {@link .removeItem}) before the map is replaced with a new one.
	 * 
	 * @final
	 * @type int
	 */
	var REMOVAL_LIMIT = 1000;
	
	/**
	 * This is a utility and does not need to be constructed.
	 * 
	 * @class
	 * Utility class that prevents memory leaks in IE when using long running maps that are added to and removed from over
	 * time.
	 * 
	 * <p><b>Using this class has a penalty in performance and code beauty. Only use this when you are keeping a
	 * map for a long time and will be removing items and adding items with new names (not previously used)
	 * repeatedly.</b></p> 
	 * 
	 * <p>Users of this class should not perform any browser detection themselves, as this is handled internally, and normal
	 * maps are returned for browsers that do not leak memory.</p>
	 */
	var MapFactory = {};

	/**
	 * Creates a map that doesn't leak when used in conjunction with {@link .removeItem}.
	 * 
	 * @return A specially modified map object that allows the <code>MapFactory</code> to determine when any clean-up should occur, or a
	 * normal map object for browsers that do not leak.
	 * @type Object
	 */
	MapFactory.createMap = function() {
		if (browserMapsLeakMemory) {
			var fClass = new Function();
			fClass.deleteCount = 0;
			return new fClass();
		} else {
			return {};
		}
	};

	/**
	 * Removes an item from the map.
	 * 
	 * <p>If cleanup occurs, then the map will be replaced with a completely new one, and so any references to the map must be reset
	 * whenever this method is called, for example: <code>this.m_mMap = MapFactory.removeItem(this.m_mMap, sItemKey);</code></p>
	 * 
	 * @param {Object} mMap The map the item will be removed from.
	 * @param {String} sKey The key of the item to be removed from the map.
	 * @type Object
	 * @return A reference to the updated map &mdash; typically this will just be <code>mMap</code>, but may be a new map reference if the map has
	 * been replaced with a new instance.
	 */
	MapFactory.removeItem = function(mMap, sKey) {
		delete mMap[sKey];
		
		if (browserMapsLeakMemory) {
			if (mMap.constructor.deleteCount++ >= this.REMOVAL_LIMIT) {
				var mNewMap = this.createMap();
				
				for(var sItem in mMap) {
					mNewMap[sItem] = mMap[sItem];
				}
				
				mMap = mNewMap;
			}
		}
		
		return mMap;
	};

	br.util.MapFactory = MapFactory;
})();