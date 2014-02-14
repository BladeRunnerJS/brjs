/** @module br/AliasRegistry */
"use strict";

/**
 * The <code>AliasRegistry</code> is provides access to the aliases used within
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
 *
 * @module br/AliasRegistry
 * @requires br
 * @requires br/Errors
 */
var br = require('br/Core');
var Errors = require('./Errors');

var aliasData = null;
var isAliasDataSet = false;

/**
 * Returns an array containing the names of all aliases in use within the application.
 *
 * @type Array
 */
exports.getAllAliases = function getAllAliases() {
	ensureAliasDataHasBeenSet();
	return Object.keys(aliasData);
};

/**
 * Returns a filtered subset of the aliases provided by
 * {@link module:br/AliasRegistry.getAllAliases}.
 *
 * <p>An alias is considered to be associated with an interface if the XML configuration for that
 * alias specifically mentions the given interface, or if the class the alias points to happens to
 * implement the given interface.</p>
 *
 * @param {function} interface the interface being used to filter the aliases by.
 * @type Array
 */
exports.getAliasesByInterface = function getAliasesByInterface(protocol) {
	ensureAliasDataHasBeenSet();
	var allAliases = this.getAllAliases();
	var filteredAliases = [];

	for(var i = 0, length = allAliases.length; i < length; ++i) {
		var alias = allAliases[i];
		var aliasInterface = aliasData[alias]["interface"];

		if(aliasInterface === protocol) {
			filteredAliases.push(alias);
		} else if (this.isAliasAssigned(alias)) {
			var aliasClass = this.getClass(alias);

			if(br.isAssignableFrom(aliasClass, protocol) || implementsInterface(aliasClass, protocol)) {
				filteredAliases.push(alias);
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
exports.getClass = function getClass(aliasName) {
	ensureAliasDataHasBeenSet();
	if (!this.isAliasAssigned(aliasName)) {
		throw new Errors.IllegalStateError("No class has been found for alias '" + aliasName +"'");
	}

	return aliasData[aliasName]["class"];
};

/**
 * Returns whether the given alias is defined.
 *
 * @param {String} aliasName alias name.
 * @type boolean
 */
exports.isAlias = function isAlias(aliasName) {
	ensureAliasDataHasBeenSet();
	return aliasName in aliasData;
};

/**
 * Returns whether the given alias has been assigned a value &mdash; i.e. whether an alias has a
 * class value.
 *
 * @param {String} aliasName alias name.
 * @type boolean
 */
exports.isAliasAssigned = function isAliasAssigned(aliasName) {
	ensureAliasDataHasBeenSet();
	return this.isAlias(aliasName) && aliasData[aliasName]["class"] !== undefined;
};

/**
 * Resets the <code>AliasRegistry</code> back to its initial state.
 *
 * <p>This method isn't normally called within an application, but is called automatically before
 * each test is run.</p>
 *
 * @see caplin.core.ServiceRegistry.clear
 */
exports.clear = function clear() {
//	TODO: clean this up as part of removing the clear() function
//	isAliasDataSet = false;
//	aliasData = null;
};

/**
 * Sets the alias data.
 *
 * If the alias data is inconsistent, this will throw Errors.
 */
exports.setAliasData = function setAliasData(unverifiedAliasData) {
	if (isAliasDataSet === true) {
		throw new Errors.IllegalStateError("Alias data has already been set; unable to set again.");
	}

	isAliasDataSet = true;
	aliasData = unverifiedAliasData;

	var aliases = this.getAllAliases();
	var incorrectAliases = [];
	var i;

	for (i = 0; i < aliases.length; ++i) {
		var aliasId = aliases[i];
		var alias = aliasData[aliasId];

		if (this.isAliasAssigned(aliasId) && alias["interface"]) {
			var aliasClass = alias["class"];
			var protocol = alias["interface"];

			if (br.isAssignableFrom(aliasClass, protocol) == false && implementsInterface(aliasClass, protocol) == false) {
				incorrectAliases.push(aliasId);
			}
		}
	}

	if(incorrectAliases.length > 0) {
		var errorMessage = 'The classes for the following aliases do not implement their required interfaces: \n';
		for(i = 0; i < incorrectAliases.length; ++i)
		{
			var incorrectAlias = incorrectAliases[i];
			errorMessage += '[' + incorrectAlias + ']: "' + aliasData[incorrectAlias]["className"] + '" should implement "' + aliasData[incorrectAlias].interfaceName + '";\n';
		}
		this.clear();
		throw new Errors.IllegalStateError(errorMessage);
	}
};

// private utility functions /////////////////////////////////////////////////////////////////////
// TODO: This should not be required.
function implementsInterface(aliasClass, protocol) {
	for(member in protocol.prototype) {
		if(typeof aliasClass.prototype[member] != "function")
		{
			return false;
		}
	}
	return true;
}

function ensureAliasDataHasBeenSet() {
	if (isAliasDataSet !== true) {
		// TODO: the bundler should just require AliasRegistry and initialize this stuff itself.
		var global = Function("return this")(); 
		if (global.caplin && global.caplin.__aliasData) {
			exports.setAliasData(global.caplin.__aliasData);
			return;
		}
		throw new Errors.IllegalStateError("Alias data has not been set.");
	}
}
