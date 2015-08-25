'use strict';

var Property = require('br/presenter/property/Property');
var WritableProperty = require('br/presenter/property/WritableProperty');

/**
 * @module br/presenter/property/Snapshot
 */

/**
 * Create a new <code>Snapshot</code> instance containing the current state
 * of the given properties.
 * 
 * @class
 * @alias module:br/presenter/property/Snapshot
 * 
 * @classdesc
 * <p>The snapshot class allows the state of a collection of properties to be preserved,
 * so that it becomes possible to roll the properties back to a known state at a future
 * point in time. The {@link module:br/presenter/property/Properties} class may be helpful
 * in composing the list of properties to be snapshot, when those properties are
 * distributed throughout the presentation model.</p>
 * 
 * @param {Array} pProperties The list of properties to be snapshot.
 */
function Snapshot(pProperties) {
	/** @private */
	this.mValues = [];

	for (var i = 0; i < pProperties.length; i++) {
		if (pProperties[i] instanceof WritableProperty) {
			var vValue = pProperties[i].getValue();
			if (vValue instanceof Array) {
				vValue = this._cloneArray(vValue);
			}

			var saved = {
				value: vValue,
				property: pProperties[i]
			};
			this.mValues.push(saved);
		}
	}
}

/** @private */
Snapshot.prototype._cloneArray = function(pIn) {
	var pResult = [];
	for (var i = 0; i < pIn.length; i++) {
		pResult.push(pIn[i]);
	}

	return pResult;
};

/**
 * Revert the properties within the snapshot back to their state when the snapshot
 * was originally taken.
 */
Snapshot.prototype.apply = function() {
	for (var i = 0; i < this.mValues.length; i++) {
		var saved = this.mValues[i];
		saved.property.setValue(saved.value);
	}
};

module.exports = Snapshot;
