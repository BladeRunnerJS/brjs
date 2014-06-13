brjs.dashboard.app.testing.LocalStorageFixture = function()
{
	this.m_oLocalStorage = null;
};
br.Core.inherit(brjs.dashboard.app.testing.LocalStorageFixture, br.test.Fixture);

brjs.dashboard.app.testing.LocalStorageFixture.prototype.setLocalStorage = function(oLocalStorage) {
	this.m_oLocalStorage = oLocalStorage;
};

brjs.dashboard.app.testing.LocalStorageFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

brjs.dashboard.app.testing.LocalStorageFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return true;
};

brjs.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
{
	this.m_oLocalStorage.setItem(sPropertyName, vValue);
};
brjs.dashboard.app.testing.LocalStorageFixture.prototype.doGiven = brjs.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen;
brjs.dashboard.app.testing.LocalStorageFixture.prototype.doWhen = brjs.dashboard.app.testing.LocalStorageFixture.prototype._doGivenAndDoWhen;

brjs.dashboard.app.testing.LocalStorageFixture.prototype.doThen = function(sPropertyName, vValue)
{
	assertEquals(vValue, this.m_oLocalStorage.getItem(sPropertyName));
};

