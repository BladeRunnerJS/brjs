/**
 * @module br/presenter/view/knockout/KnockoutSubscription
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/KnockoutSubscription
 */
br.presenter.view.knockout.KnockoutSubscription = function(fBoundCallback, oKnockoutObservable, event)
{
	this.event = event;
	this.target = oKnockoutObservable;
	this.m_fBoundCallback = fBoundCallback;
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutSubscription.prototype.dispose = function()
{
	this.target.removeKnockoutListener(this.m_fBoundCallback, this.event);
};

