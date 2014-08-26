KnockoutPropertyTest = TestCase("KnockoutPropertyTest");

KnockoutPropertyTest.prototype.test_removeAllListenersDoesntPreventKnockoutUpdates = function()
{
	var oWritableProperty = new br.presenter.view.knockout.KnockoutPropertySubclass();
	oWritableProperty.removeAllListeners();
	assertNull("1a", oWritableProperty.m_vViewValue);
	
	oWritableProperty.setValue("some-value");
	assertEquals("2a", "some-value", oWritableProperty.m_vViewValue);
};

KnockoutPropertyTest.prototype.test_subClassesOfPropertyCanImplementPropertyListener = function()
{
	var oWritableProperty = new br.presenter.view.knockout.KnockoutPropertySubclass();
	assertEquals("1a", 0, oWritableProperty.m_nUpdateCounter);
	assertNull("1b", oWritableProperty.m_vViewValue);
	
	oWritableProperty.setValue("some-value");
	assertEquals("2a", 1, oWritableProperty.m_nUpdateCounter);
	assertEquals("2b", "some-value", oWritableProperty.m_vViewValue);
};
