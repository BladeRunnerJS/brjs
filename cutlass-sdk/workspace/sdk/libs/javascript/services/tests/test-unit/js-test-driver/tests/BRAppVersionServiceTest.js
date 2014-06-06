BRAppVersionService = TestCase("BRAppVersionService");

BRAppVersionService.prototype.test_AppVersionCanBeSet = function()
{
	var oService = br.ServiceRegistry.getService("br.app-version-version");
	oService.setVersion("123");
	assertEquals(oService.getVersion(), "123");
};

BRAppVersionService.prototype.test_AppVersionCantBeSetAfterItsAlreadyBeenSet = function()
{
	var oService = br.ServiceRegistry.getService("br.app-version-version");
	oService.setVersion("123");
	assertException(function() {
		oService.setVersion("987");
	}, 'IllegalStateError');
};

BRAppVersionService.prototype.test_ErrorIsThrownIfAppVersionHasntBeenSet = function()
{
	var oService = br.ServiceRegistry.getService("br.app-version-version");
	assertException(function() {
		oService.getVersion("987");
	}, 'IllegalStateError');
};
