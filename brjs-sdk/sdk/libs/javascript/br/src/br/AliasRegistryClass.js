"use strict";

/**
* @module br/AliasRegistryClass
*/

var br = require('br/Core');
var Errors = require('./Errors');

/**
* @class
* @alias module:br/AliasRegistryClass
*
* @classdesc
* The <code>AliasRegistryClass</code> class provides access to the aliases used within
* the application.
*
* <p>An alias is simply an alternate logical name for a class, such that specifying
* this logical name in your source code, whether it be Javascript, HTML or CSS, will
* cause the class to be bundled and sent to the browser. It is therefore, at it's
* simplest, a mechanism for ensuring that all the code your app needs, and no more,
* is bundled and sent to the browser. Though class dependencies are usually specified
* by directly referring to other classes, there are times when this is
* inappropriate:</p>
*
* <ol>
*   <li>We sometimes need a level of indirection, so that dependencies can be expressed
*    without knowing the concrete class that will end up being used (e.g. services).</li>
*   <li>It is sometimes useful to specify components declaratively, where it would be confusing
*    to have to refer to the classes that provide some feature, rather than talking in terms
*    of the feature itself.</li>
* </ol>
*
* <p>Another useful facet of aliases is that they can be used to automatically
* discover all of the classes which implement a particular interface, which
* makes it a good candidate for creating SPI type, auto-discovery mechanisms.</p>
*/
var AliasRegistryClass = function(aliasData)
{
	this._aliasData = aliasData;
};

/**
* Returns an array containing the names of all aliases in use within the application.
*
* @type Array
*/
AliasRegistryClass.prototype.getAllAliases = function getAllAliases() {
	return Object.keys(this._aliasData);
};

/**
* Returns a filtered subset of the aliases provided by
* {@link module:br/AliasRegistry/getAllAliases}.
*
* <p>An alias is considered to be associated with an interface if the XML configuration for that
* alias specifically mentions the given interface, or if the class the alias points to happens to
* implement the given interface.</p>
*
* @param {function} requiredInterface the interface being used to filter the aliases by.
* @type Array
*/
AliasRegistryClass.prototype.getAliasesByInterface = function getAliasesByInterface(requiredInterface) {
	var allAliases = this.getAllAliases();
	var filteredAliases = [];

	for(var i = 0, length = allAliases.length; i < length; ++i) {
		var aliasName = allAliases[i];
		var aliasInterface = this._getInterfaceRef(aliasName);

		if(aliasInterface === requiredInterface) {
			filteredAliases.push(aliasName);
		} else if (this.isAliasAssigned(aliasName)) {
			var aliasClass = this.getClass(aliasName);

			if(br.classIsA(aliasClass, requiredInterface)) {
				filteredAliases.push(aliasName);
			}
		}
	}

	return filteredAliases;
};

/**
* Returns a class corresponding to the requested alias name.
*
* @throws {Errors.IllegalState} if the given alias doesn't exist.
* @param {String} aliasName alias name.
* @type function
*/
AliasRegistryClass.prototype.getClass = function getClass(aliasName) {
	if (!this.isAliasAssigned(aliasName)) {
		throw new Errors.IllegalStateError("No class has been found for alias '" + aliasName +"'");
	}

	var classRef = this._getClassRef(aliasName);
	var interfaceRef = this._getInterfaceRef(aliasName);

	if(interfaceRef) {
		if(!br.classIsA(classRef, interfaceRef)) {
			var alias = this._aliasData[aliasName];
			var AliasInterfaceError = require("br/AliasInterfaceError");

			throw new AliasInterfaceError(aliasName, alias['class'], alias['interface']);
		}
	}

	return classRef;
};

/**
* Returns whether the given alias is defined.
*
* @param {String} aliasName alias name.
* @type boolean
*/
AliasRegistryClass.prototype.isAlias = function isAlias(aliasName) {
	return aliasName in this._aliasData;
};

/**
* Returns whether the given alias has been assigned a value &mdash; i.e. whether an alias has a
* class value.
*
* @param {String} aliasName alias name.
* @type boolean
*/
AliasRegistryClass.prototype.isAliasAssigned = function isAliasAssigned(aliasName) {
	return this.isAlias(aliasName) && this._aliasData[aliasName]["class"] !== undefined;
};

/**
 * @private
 */
AliasRegistryClass.prototype._getClassRef = function(aliasName) {
	var alias = this._aliasData[aliasName];

	if(alias.classRef === undefined) {
		alias.classRef = require(alias["class"]);
	}

	return alias.classRef;
};

/**
 * @private
 */
AliasRegistryClass.prototype._getInterfaceRef = function(aliasName) {
	var alias = this._aliasData[aliasName];

	if(alias.interfaceRef === undefined) {
		var interfaceName = alias['interface'];
		alias.interfaceRef = (interfaceName) ? require(interfaceName) : null;
	}

	return alias.interfaceRef;
};

module.exports = AliasRegistryClass;
