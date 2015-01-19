/**
 * @module br/presenter/testing/KnockoutInvocationCountPlugin
 */

br.Core.thirdparty("presenter-knockout");

/**
 * @private
 * @class
 * @alias module:br/presenter/testing/KnockoutInvocationCountPlugin
 */
br.presenter.testing.KnockoutInvocationCountPlugin = function()
{
};

br.presenter.testing.KnockoutInvocationCountPlugin.prototype.init = function (element, valueAccessor, allBindingsAccessor, viewModel) {
	
	var eventsToHandle = valueAccessor() || {};
	
	for(var eventNameOutsideClosure in eventsToHandle) {
		var methodToBeReplacedByProxy = eventsToHandle[eventNameOutsideClosure];
		eventsToHandle[eventNameOutsideClosure] = presenter_knockout.bindingHandlers.event._getInvocationCountingProxyMethod(methodToBeReplacedByProxy);
		valueAccessor = function() {
			return eventsToHandle;
		};
		(function() {
			var eventName = eventNameOutsideClosure; // Separate variable to be captured by event handler closure
			if (typeof eventName == "string") {
				presenter_knockout.utils.registerEventHandler(element, eventName, function (event) {
					var handlerReturnValue;
					var handlerFunction = valueAccessor()[eventName];
					if (!handlerFunction)
						return;
					var allBindings = allBindingsAccessor();
					
					try { 
						var argsForHandler = presenter_knockout.utils.makeArray(arguments);
						argsForHandler.unshift(viewModel);
						handlerReturnValue = handlerFunction.apply(viewModel, argsForHandler );
					} finally {
						if (handlerReturnValue !== true) { // Normally we want to prevent default action. Developer can override this be explicitly returning true.
							if (event.preventDefault)
								event.preventDefault();
							else
								event.returnValue = false;
						}
					}
					
					var bubble = allBindings[eventName + 'Bubble'] !== false;
					if (!bubble) {
						event.cancelBubble = true;
						if (event.stopPropagation)
							event.stopPropagation();
					}
				});
			}
		})();
	}
};

br.presenter.testing.KnockoutInvocationCountPlugin.prototype.update = function(eElement, fValueAccessor, fAllBindingsAccessor, oViewModel)
{
	// this method doesn't provide us anything useful we don't already get in init()
};

/** @private */
br.presenter.testing.KnockoutInvocationCountPlugin.prototype._getInvocationCountingProxyMethod = function(fOrigMethod) {
	
	var fMethod = function()
	{
		fOrigMethod.invocationCount++;
		fOrigMethod.apply(this, arguments);
	};
	fOrigMethod.invocationCount = 0;
	return fMethod;
};
