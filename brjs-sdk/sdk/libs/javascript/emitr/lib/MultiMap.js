var Map = require('./Map');

function MultiMap() {
	this._map = new Map();
}
MultiMap.prototype = {
	'getValues': function getValues(key) {
		var val;
		if (arguments.length === 0) {
			// return all values for all keys.
			val = [];
			this._map.forEach(function(values) {
				val.push.apply(val, values);
			});
		} else {
			// return all the values for the provided key.
			val = this._map.get(key);
			if (val === undefined) {
				val = [];
				this._map.set(key, val);
			}
		}
		return val;
	},
	'clear': function clear() {
		this._map = new Map();
	},
	'add': function add(key, value) {
		this.getValues(key).push(value);
	},
	'filter': function filter(key, filterFunction) {
		if (this._map.has(key) === false) { return; }
		var values = this._map.get(key).filter(filterFunction);

		if (values.length === 0) {
			this._map['delete'](key);
		} else {
			this._map.set(key, values);
		}
	},
	'filterAll': function(filterFunction) {

		//TODO: The following line can be removed and instead a third 'map' parameter 
		// can be added to the forEach callback once the following webkit bug is resovled
		// https://bugs.webkit.org/show_bug.cgi?id=138563

		var map = this._map;
		this._map.forEach(function(values, key) {
			var newValues = values.filter(filterFunction);
			if (newValues.length === 0) {
				map['delete'](key);
			} else {
				map.set(key, newValues);
			}
		});
	},
	'removeLastMatch': function removeLast(key, matchFunction) {
		if (this._map.has(key) === false) { return false; }
		var values = this._map.get(key);
		for (var i = values.length - 1; i >= 0; --i) {
			if (matchFunction(values[i])) {
				values.splice(i, 1);
				return true;
			}
		}
		return false;
	},
	'hasAny': function has(key) {
		return this._map.has(key);
	},
	'delete': function del(key) {
		this._map['delete'](key);
	}
};

module.exports = MultiMap;