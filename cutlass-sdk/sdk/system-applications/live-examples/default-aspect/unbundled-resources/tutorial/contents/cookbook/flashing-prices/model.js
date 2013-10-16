novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.WritableProperty(0.42);
	this.amount.addFormatter(caplin.element.formatter.DecimalFormatter, {dp: 3});
	
	window.setInterval(this._updatePrice.bind(this), 1500);
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype._updatePrice = function() 
{
	var nRandomOffset = ((Math.random() * 20) - 10) / 1000;
	this.amount.setValue(this.amount.getValue() + nRandomOffset);
};
