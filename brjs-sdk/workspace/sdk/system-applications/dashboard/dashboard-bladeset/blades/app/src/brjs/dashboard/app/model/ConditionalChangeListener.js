brjs.dashboard.app.model.ConditionalChangeListener = function(oListener, sMethod, oConditionProperty, vConditionValue)
{
	this.m_oListener = oListener;
	this.m_sMethod = sMethod;
	this.m_oConditionProperty = oConditionProperty;
	this.m_vConditionValue = vConditionValue;
	
	this.m_oConditionProperty.addChangeListener(this, "_onConditionPropertyChanged");
};
br.Core.inherit(brjs.dashboard.app.model.ConditionalChangeListener, br.presenter.property.PropertyListener);
br.Core.inherit(brjs.dashboard.app.model.ConditionalChangeListener, br.presenter.node.NodeListListener);

brjs.dashboard.app.model.ConditionalChangeListener.prototype.onPropertyChanged = function()
{
	this._sendUpdates();
};

brjs.dashboard.app.model.ConditionalChangeListener.prototype.onNodeListChanged = function()
{
	this._sendUpdates();
};

brjs.dashboard.app.model.ConditionalChangeListener.prototype._onConditionPropertyChanged = function()
{
	this._sendUpdates();
};

brjs.dashboard.app.model.ConditionalChangeListener.prototype._sendUpdates = function()
{
	if(this.m_oConditionProperty.getValue() === this.m_vConditionValue)
	{
		this.m_oListener[this.m_sMethod].call(this.m_oListener);
	}
};
