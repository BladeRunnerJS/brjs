caplinx.dashboard.app.testing.LocalStorageFixture = function()
{
	this.m_oLocalStorage = null;
};
br.Core.inherit(caplinx.dashboard.app.testing.LocalStorageFixture, br.test.Fixture);

caplinx.dashboard.app.testing.LocalStorageFixture.prototype.setLocalStorage = function(oLocalStorage) {
	this.m_oLocalStorage = oLocalStorage;
};

caplinx.dashboard.app.testing.LocalStorageFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

caplinx.dashboard.app.testing.LocalStorageFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return true;
};

caplinx.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
{
	this.m_oLocalStorage.setItem(sPropertyName, vValue);
};
caplinx.dashboard.app.testing.LocalStorageFixture.prototype.doGiven = caplinx.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen;
caplinx.dashboard.app.testing.LocalStorageFixture.prototype.doWhen = caplinx.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen;

caplinx.dashboard.app.testing.LocalStorageFixture.prototype.doThen = function(sPropertyName, vValue)
{
	assertEquals(vValue, this.m_oLocalStorage.getItem(sPropertyName));
};

