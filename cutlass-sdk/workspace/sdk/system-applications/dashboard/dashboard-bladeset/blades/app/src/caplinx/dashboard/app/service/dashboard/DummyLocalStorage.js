caplinx.dashboard.app.service.dashboard.DummyLocalStorage = function()
{
	this.m_pStorage = new Array();
}

caplinx.dashboard.app.service.dashboard.DummyLocalStorage.prototype.getItem = function(sKey)
{
	return this.m_pStorage[sKey];
}

caplinx.dashboard.app.service.dashboard.DummyLocalStorage.prototype.setItem = function(sKey,vValue)
{
	this.m_pStorage[sKey] = vValue;
}

caplinx.dashboard.app.service.dashboard.DummyLocalStorage.prototype.clearItems = function()
{
	this.m_pStorage = new Array();
}
