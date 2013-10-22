caplin.namespace("live.examples.presenter.tutorial");

live.examples.presenter.tutorial.MessageServiceStub = function() {
	caplin.services.testing.MessageServiceStub.call(this); 	
};

caplin.implement(live.examples.presenter.tutorial.MessageServiceStub, caplin.services.testing.MessageServiceStub);

live.examples.presenter.tutorial.MessageServiceStub.prototype.subscribe = function(sSubject, oListener)
{
	caplin.services.testing.MessageServiceStub.prototype.subscribe(sSubject, oListener);
	this.invokeSendDataToListenerOnDataSubscribers("/FX/EURUSD", {"BestBid" : 4.44, "BestAsk" : 5.55});
};

live.examples.presenter.tutorial.MessageServiceStub.prototype._getRandomNumber = function(vMin, vMax)
{
	return Math.random() * (vMax - vMin) + vMin;
};