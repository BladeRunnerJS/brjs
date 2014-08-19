/**
 * @module br/presenter/view/knockout/KnockoutObservable
 */

br.Core.thirdparty("presenter-knockout");

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable = function()
{
	/** @private */
	this._subscriptions = {};
	
	/** @private */
	this.__ko_proto__ = presenter_ko.observable;
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable.prototype.updateView = function (vValue)
{
	this.notifySubscribers(vValue);
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable.prototype.subscribe = function (callback, callbackTarget, event)
{
	var boundCallback = callbackTarget ? callback.bind(callbackTarget) : callback;
	event = event || "change";
	
	if (!this._subscriptions[event])
	{
		this._subscriptions[event] = [];
	}
	
	this._subscriptions[event].push(boundCallback);
	
	return new br.presenter.view.knockout.KnockoutSubscription(boundCallback, this, event);
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable.prototype.notifySubscribers = function (valueToNotify, event)
{
	event = event || "change";
	
	if (this._subscriptions[event])
	{
		presenter_ko.dependencyDetection.ignore(function() {
			presenter_ko.utils.arrayForEach(this._subscriptions[event].slice(0), function (subscription) {
				// In case a subscription was disposed during the arrayForEach cycle, check
				// for isDisposed on each subscription before invoking its callback
				if (subscription && (subscription.isDisposed !== true))
				{
					subscription(valueToNotify);
				}
			});
		}, this);
	}
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable.prototype.getSubscriptionsCount = function ()
{
	var total = 0;
	
	for (var eventName in this._subscriptions)
	{
		if (this._subscriptions.hasOwnProperty(eventName))
		{
			total += this._subscriptions[eventName].length;
		}
	}
	
	return total;
}

/**
 * @private
 */
br.presenter.view.knockout.KnockoutObservable.prototype.removeKnockoutListener = function (boundCallback, event)
{
	br.util.ArrayUtility.removeItem(this._subscriptions[event], boundCallback);
};
