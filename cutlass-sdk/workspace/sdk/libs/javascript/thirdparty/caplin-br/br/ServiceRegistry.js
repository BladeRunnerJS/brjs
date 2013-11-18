define("br/ServiceRegistry", /** @exports br/ServiceRegistry */ function(require, exports, module) {
	"use strict";

	/**
	 * The <code>ServiceRegistry</code> is a static class and does not need to be constructed.
	 *
	 * @class
	 * The <code>ServiceRegistry</code> is used to allow a given application access to application
	 * services.
	 *
	 * <p>Services are typically registered or requested using an alias name, but older applications
	 * may still register and request using interfaces, which is also still supported. Applications
	 * that use aliases don't normally need to manually register services as these are created lazily
	 * upon first request, but will still need to manually register services that can't be created
	 * using a zero-arg constructor.</p>
	 *
	 * <p>The <code>ServiceRegistry</code> is initialized as follows:</p>
	 *
	 * <ol>
	 *	<li>The application invokes {@link caplin.core.ServiceRegistry.initializeServices} which
	 *		causes all delayed readiness services to be created.</li>
	 *	<li>Once {@link caplin.core.ServiceRegistry.initializeServices} has finished (once one of the
	 *		call-backs fire), the application should then register any services that can't be created
	 *		lazily using zero-arg constructors.</li>
	 *	<li>The application can now start doing it's proper work.</li>
	 * </ol>
	 *
	 * <p>Because blades aren't allowed to depend directly on classes in other blades, interface
	 * definitions are instead created for particular pieces of functionality, and blades can choose
	 * to register themselves as being providers of that functionality. The
	 * <code>ServiceRegistry</code> and the {@link caplin.core.event.EventHub} are both useful in this
	 * regard:
	 *
	 * <ul>
	 *	<li>Many-To-One dependencies are resolved by having a single service instance available via
	 *		the <code>ServiceRegistry</code>.</li>
	 *	<li>Many-To-Many dependencies are resolved by having zero or more classes register with the
	 *		{@link caplin.core.event.EventHub}.</li>
	 * </ul>
	 * @module br/ServiceRegistry
	 */

	var Errors = require('./Errors');
	var AliasRegistry = require('./AliasRegistry');

	var registry = {};

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
	function registerService(alias, serviceInstance) {
		if (serviceInstance === undefined) {
			throw new Errors.InvalidParametersError("The service instance is undefined.");
		}

		if (alias in registry) {
			throw new Errors.IllegalStateError("Service: " + alias + " has already been registered.");
		}

		registry[alias] = serviceInstance;
	}
	exports.registerService = registerService;

	/**
	 * De-register a service that is currently registered in the <code>ServiceRegistry</code>.
	 *
	 * @param {String} sIdentifier The alias or interface name used to uniquely identify the service.
	 */
	function deregisterService(alias) {
		delete registry[alias];
	}
	exports.deregisterService = deregisterService;

	/**
	 * Retrieve the service linked to the identifier within the application. The identifier could be a
	 * service alias or a service interface.
	 *
	 * @param {String} identifier The alias or interface name used to uniquely identify the service.
	 * @throws {Error} If no service could be found for the given identifier.
	 * @type Object
	 */
	function getService(alias) {
		initializeServiceIfRequired(alias);

		if (registry[alias] === undefined){
			throw new Errors.InvalidParametersError("br/ServiceRegistry could not locate a service for: " + alias);
		}

		return registry[alias];
	}
	exports.getService = getService;

	/**
	 * Determine whether a service has been registered for a given identifier.
	 *
	 * @param {String} identifier The alias or interface name used to uniquely identify the service.
	 * @type boolean
	 */
	function isServiceRegistered(alias) {
		return alias in registry;
	}
	exports.isServiceRegistered = isServiceRegistered;

	/**
	 * Resets the <code>ServiceRegistry</code> back to its initial state.
	 *
	 * <p>This method isn't normally called within an application, but is called automatically before
	 * each test is run.</p>
	 *
	 * @see AliasRegistry.clear
	 */
	function clear() {
		registry = {};
	}
	exports.clear = clear;

	// private statics ///////////////////////////////////////////////////////////////////////////////

	/**
	 * @private
	 *
	 * @param {String} identifier a service identifier string.
	 */
	function initializeServiceIfRequired(alias) {
		if (alias in registry === false) {
			var isIdentifierAlias = AliasRegistry.isAliasAssigned(alias);

			if (isIdentifierAlias) {
				var ServiceClass = AliasRegistry.getClass(alias);

				registry[alias] = new ServiceClass();
			}
		}
	}

});