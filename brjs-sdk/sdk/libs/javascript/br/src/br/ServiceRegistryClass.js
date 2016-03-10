'use strict';

/**
 * @module br/ServiceRegistryClass
 */

var Errors = require('br/Errors');
var fell = require('fell');
var log = fell.getLogger('br.ServiceRegistry');

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
*	<li>The application can now start doing its proper work.</li>
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
function ServiceRegistryClass(serviceBox) {
	if (typeof serviceBox === 'undefined') {
		this._serviceBox = require('br/servicebox/serviceBox');
	} else {
		this._serviceBox = serviceBox;
	}
};

// Main API //////////////////////////////////////////////////////////////////////////////////////

/**
* Register an object that will be responsible for implementing the given interface within the
* application.
*
* @param {String} alias The alias used to uniquely identify the service.
* @param {Object} serviceInstance The object responsible for providing the service.
* @throws {Error} If a service has already been registered for the given interface or if no
* 		instance object is provided.
*/
ServiceRegistryClass.prototype.registerService = function(alias, serviceInstance) {
	if (serviceInstance === undefined) {
		throw new Errors.InvalidParametersError('The service instance is undefined.');
	}

	if (this.isServiceRegistered(alias)) {
		throw new Errors.IllegalStateError('Service: ' + alias + ' has already been registered.');
	}

	var serviceFactory = function() {
		return Promise.resolve(serviceInstance);
	};
	serviceFactory.dependencies = [];

	this._serviceBox.factories[alias] = serviceFactory;
	this._serviceBox.services[alias] = serviceInstance;
};

/**
* De-register a service that is currently registered in the <code>ServiceRegistryClass</code>.
*
* @param {String} alias The alias or interface name used to uniquely identify the service.
*/
ServiceRegistryClass.prototype.deregisterService = function(alias) {
	delete this._serviceBox.factories[alias];
	delete this._serviceBox.services[alias];
};

/**
* Retrieve the service linked to the identifier within the application. The identifier could be a
* service alias or a service interface.
*
* @param {String} alias The alias or interface name used to uniquely identify the service.
* @throws {Error} If no service could be found for the given identifier.
* @type Object
*/
ServiceRegistryClass.prototype.getService = function(alias) {
	var services = this._serviceBox.services;

	if (alias in services) {
		return services[alias];
	}

	return this._initializeService(alias);
};

/** @private */
ServiceRegistryClass.prototype._initializeService = function(alias) {

	var AliasRegistry = require('./AliasRegistry');
	var services = this._serviceBox.services;
	if (!AliasRegistry.isAliasAssigned(alias)) {
		throw new Errors.InvalidParametersError('br/ServiceRegistryClass could not locate a service for: ' + alias);
	}

	var ServiceCtor = AliasRegistry.getClass(alias);

	if (typeof this._serviceBox.factories[alias] === 'undefined') {
		this._serviceBox.factories[alias] = function() {
			Promise.resolve(new ServiceCtor());
		};
	}

	services[alias] = new ServiceCtor();

	return services[alias];
};

/**
* Determine whether a service has been registered for a given identifier.
*
* @param {String} alias The alias or interface name used to uniquely identify the service.
* @type boolean
*/
ServiceRegistryClass.prototype.isServiceRegistered = function(alias) {
	return alias in this._serviceBox.factories;
};

/**
* Resets the <code>ServiceRegistryClass</code> back to its initial state.
*
* <p>This method isn't normally called within an application, but is called automatically before
* each test is run.</p>
*/
ServiceRegistryClass.prototype.legacyClear = function() {
	if (!legacyWarningLogged) {
		legacyWarningLogged = true;
		var logConsole = (window.jstestdriver) ? jstestdriver.console : window.console;
		logConsole.warn('ServiceRegistry#legacyClear is deprecated. Please use sub-realms instead.');
	}

	this.dispose();
};

ServiceRegistryClass.prototype.dispose = function() {
	var services = this._serviceBox.services;
	Object.keys(services).forEach(
		function(name) {
			var service = services[name];
			if (typeof service.dispose === 'function') {
				if (service.dispose.length === 0) {
					try {
						service.dispose();
						log.debug(ServiceRegistryClass.LOG_MESSAGES.DISPOSE_CALLED, name);
					} catch (e) {
						log.error(ServiceRegistryClass.LOG_MESSAGES.DISPOSE_ERROR, name, e);
					}
				} else {
					log.info(ServiceRegistryClass.LOG_MESSAGES.DISPOSE_0_ARG, name);
				}
			} else {
				log.debug(ServiceRegistryClass.LOG_MESSAGES.DISPOSE_MISSING, name);
			}
		}
	);

	this._serviceBox.factories = {};
	this._serviceBox.services = {};
}

ServiceRegistryClass.LOG_MESSAGES = {
	DISPOSE_CALLED: "dispose() called on service registered for '{0}'",
	DISPOSE_ERROR: "error thrown when calling dispose() on service registered for '{0}'. The error was: {1}",
	DISPOSE_0_ARG: "dispose() not called on service registered for '{0}' since it's dispose() method requires more than 0 arguments",
	DISPOSE_MISSING: "dispose() not called on service registered for '{0}' since no dispose() method was defined"
}

module.exports = ServiceRegistryClass;
