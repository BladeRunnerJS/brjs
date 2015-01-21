'use strict';

/**
* @module br/util/Utility
*/

var Errors = require('br/Errors');
var fell = require('fell');

/**
 * This is a static utility class and does not need to be instantiated.
 * 
 * @private
 * @class
 * @alias module:br/util/Utility
 */
function Utility() {
}

/**
 * Used when creating a unique ID
 * @private
 */
Utility.UNIQUE_ID = 0;

/**
 * Creates an ID in the form of a GUID that is unique for a single application instance. This ID is NOT globally unique so multiple users of the same application could create the same ID.
 * @private
 * @returns {String} a unique ID for the the present instance of the application.
 */
Utility.createApplicationInstanceUniqueId = function() {
	return 'ApplicationInstanceUniqueId_' + (++Utility.UNIQUE_ID);
};

Utility.nextObjectIdentifier = 1;

/**
 * @private
 * @throws br.util.Error Always throws exception when called
 * @param {String} className
 * @param {String} methodName
 */
Utility.interfaceMethod = function(className, methodName) {
	var errorMsg = 'Error in ' + className + ' base class: ' + methodName + '() has not been implemented.';

	// log the problem
	fell.error(errorMsg);

	// since we cannot recover, throw an exception
	throw new Errors.UnimplementedInterfaceError(errorMsg);
};

/**
 * Allows you to execute a method called several times just once. This is useful when you need several actions to be
 *  executed as an atomic operation.
 *
 * @private
 * @param {Object} obj The caller object.
 * @param {String} methodName The method name to be executed.
 */
Utility.performOnce = function(obj, methodName) {
	if (!this.m_mObjects) {
		this.m_mObjects = {};

		var self = this;
		window.setTimeout(function() {

			for ( var sObjId in self.m_mObjects) {
				var mMethods = self.m_mObjects[sObjId];

				for ( var methodName in mMethods) {
					var fCallback = mMethods[methodName];
					fCallback();
				}
			}

			self.m_mObjects = null;
		}, 0);
	}

	if (!obj.__objId) {
		obj.__objId = br.util.Utility.nextObjectIdentifier++;
	}

	if (!this.m_mObjects[obj.__objId]) {
		this.m_mObjects[obj.__objId] = {};
	}

	this.m_mObjects[obj.__objId][methodName] = obj[methodName].bind(obj);
};

module.exports = Utility;
