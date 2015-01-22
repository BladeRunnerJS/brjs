"use strict";

var global = Function("return this")();

var Map = global.Map;

// Uses a map for string keys and two arrays for nonstring keys.
// Another alternative would have been to add a nonenumerable id to everything that was set.
function MapShim() {
	this._map = {};
	this._keys = [];
	this._values = [];
}
MapShim.prototype = {
	'set': function set(key, value) {
		if (typeof key === 'string') {
			this._map[key] = value;
			return value;
		}
		var idx = this._keys.indexOf(key);
		if (idx < 0) {
			idx = this._keys.length;
			this._keys[idx] = key;
		}
		this._values[idx] = value;
		return value;
	},
	'get': function get(key) {
		if (typeof key === 'string') {
			return this._map[key];
		}
		return this._values[this._keys.indexOf(key)];
	},
	'delete': function(key) {
		if (typeof key === 'string') {
			delete this._map[key];
			return;
		}
		var idx = this._keys.indexOf(key);
		if (idx >= 0) {
			this._keys.splice(idx, 1);
			this._values.splice(idx, 1);
		}
	},
	'has': function(key) {
		return (typeof key === 'string' && key in this._map) || (this._keys.indexOf(key) >= 0);
	},
	'forEach': function(callback) {
		for (var key in this._map) {
			if (this._map.hasOwnProperty(key)) {
				callback(this._map[key], key, this);
			}
		}
		for (var i = this._keys.length - 1; i >= 0; --i) {
			callback(this._values[i], this._keys[i], this);
		}
	}
};

// Older versions of Firefox had Map, but didn't have forEach, so we'll use the shim there too.
if (Map === undefined || Map.prototype.forEach === undefined) {
	Map = MapShim;
}

module.exports = Map;