brjs.dashboard.app.service.dashboard.DummyLocalStorage = function()
{
	this.m_pStorage = new Array();
}

brjs.dashboard.app.service.dashboard.DummyLocalStorage.prototype.getItem = function(sKey)
{
	return this.m_pStorage[sKey];
}

brjs.dashboard.app.service.dashboard.DummyLocalStorage.prototype.setItem = function(sKey,vValue)
{
	this.m_pStorage[sKey] = vValue;
}

brjs.dashboard.app.service.dashboard.DummyLocalStorage.prototype.clearItems = function()
{
	this.m_pStorage = new Array();
}
