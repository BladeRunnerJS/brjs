"use strict";

/**
 * @module br/ServiceRegistryClass
 */

var Errors = require('./Errors');
var AliasRegistry;
var legacyWarningLogged = false;

/**
* @class
* @alias module:br/ServiceRegistryClass
*
* @classdesc
* The <code>ServiceRegistryClass</code> is used to allow a given application access to application
* services. The <code>ServiceRegistryClass</code> is a static class and does not need to be constructed.
*
* <p>Services are typically registered or requested using an alias name, but older applications
* may still register and request using interfaces, which is also still supported. Applications
* that use aliases don't normally need to manually register services as these are created lazily
* upon first request, but will still need to manually register services that can't be created
* using a zero-arg constructor.</p>
*
* <p>The <code>ServiceRegistryClass</code> is initialized as follows:</p>
*
* <ol>
*	<li>The application invokes {@link module:br/ServiceRegistryClass/initializeServices} which
*		causes all delayed readiness services to be created.</li>
*	<li>Once {@link module:br/ServiceRegistryClass/initializeServices} has finished (once one of the
*		call-backs fire), the application should then register any services that can't be created
*		lazily using zero-arg constructors.</li>
*	<li>The application can now start doing it's proper work.</li>
* </ol>
*
* <p>Because blades aren't allowed to depend directly on classes in other blades, interface
* definitions are instead created for particular pieces of functionality, and blades can choose
* to register themselves as being providers of that functionality. The
* <code>ServiceRegistryClass</code> and the {@link module:br/EventHub} are both useful in this
* regard:
*
* <ul>
*	<li>Many-To-One dependencies are resolved by having a single service instance available via
*		the <code>ServiceRegistryClass</code>.</li>
*	<li>Many-To-Many dependencies are resolved by having zero or more classes register with the
*		{@link module:br/EventHub}.</li>
* </ul>
*
* @see {@link http://bladerunnerjs.org/docs/concepts/service_registry/}
* @see {@link http://bladerunnerjs.org/docs/use/service_registry/}
*/
function ServiceRegistryClass() {
	this.registry = {};
};

// Main API //////////////////////////////////////////////////////////////////////////////////////

/**
* Register an object that will be responsible for implementing the given interface within the
* application.
*
* @param {String} identifier The alias used to uniquely identify the service.
* @param {Object} serviceInstance The object responsible for providing the service.
* @throws {Error} If a service has already been registered for the given interface or if no
* 		instance object is provided.
*/
ServiceRegistryClass.prototype.registerService = function(alias, serviceInstance) {
	if (serviceInstance === undefined) {
		throw new Errors.InvalidParametersError("The service instance is undefined.");
	}

	if (alias in this.registry) {
		throw new Errors.IllegalStateError("Service: " + alias + " has already been registered.");
	}

	this.registry[alias] = serviceInstance;
};

/**
* De-register a service that is currently registered in the <code>ServiceRegistryClass</code>.
*
* @param {String} sIdentifier The alias or interface name used to uniquely identify the service.
*/
ServiceRegistryClass.prototype.deregisterService = function(alias) {
	delete this.registry[alias];
};

/**
* Retrieve the service linked to the identifier within the application. The identifier could be a
* service alias or a service interface.
*
* @param {String} identifier The alias or interface name used to uniquely identify the service.
* @throws {Error} If no service could be found for the given identifier.
* @type Object
*/
ServiceRegistryClass.prototype.getService = function(alias) {
	this._initializeServiceIfRequired(alias);

	if (this.registry[alias] === undefined){
		throw new Errors.InvalidParametersError("br/ServiceRegistryClass could not locate a service for: " + alias);
	}

	return this.registry[alias];
};

/**
* Determine whether a service has been registered for a given identifier.
*
* @param {String} identifier The alias or interface name used to uniquely identify the service.
* @type boolean
*/
ServiceRegistryClass.prototype.isServiceRegistered = function(alias) {
	return alias in this.registry;
};

/**
* Resets the <code>ServiceRegistryClass</code> back to its initial state.
*
* <p>This method isn't normally called within an application, but is called automatically before
* each test is run.</p>
*/
ServiceRegistryClass.prototype.legacyClear = function() {
	if(!legacyWarningLogged) {
		legacyWarningLogged = true;
		var logConsole = (window.jstestdriver) ? jstestdriver.console : window.console;
		logConsole.warn('ServiceRegistry.legacyClear() is deprecated. Please use sub-realms instead.');
	}

	this.registry = {};
};

/** @private */
ServiceRegistryClass.prototype._initializeServiceIfRequired = function(alias) {
	if (alias in this.registry === false) {
		var isIdentifierAlias = AliasRegistry.isAliasAssigned(alias);

		if (isIdentifierAlias) {
			var ServiceClass = AliasRegistry.getClass(alias);

			this.registry[alias] = new ServiceClass();
		}
	}
};

module.exports = ServiceRegistryClass;

AliasRegistry = require('./AliasRegistry');
