require('../../../../../_resources-test-at/html/test-form.html');
(function() {
    var KnockoutPropertySubclass = require('br-presenter/_test-src/KnockoutPropertySubclass');
    KnockoutPropertyTest = TestCase("KnockoutPropertyTest");

    KnockoutPropertyTest.prototype.test_removeAllListenersDoesntPreventKnockoutUpdates = function()
    {
        var oWritableProperty = new KnockoutPropertySubclass();
        oWritableProperty.removeAllListeners();
        assertNull("1a", oWritableProperty.m_vViewValue);
        
        oWritableProperty.setValue("some-value");
        assertEquals("2a", "some-value", oWritableProperty.m_vViewValue);
    };

    KnockoutPropertyTest.prototype.test_subClassesOfPropertyCanImplementPropertyListener = function()
    {
        var oWritableProperty = new KnockoutPropertySubclass();
        assertEquals("1a", 0, oWritableProperty.m_nUpdateCounter);
        assertNull("1b", oWritableProperty.m_vViewValue);
        
        oWritableProperty.setValue("some-value");
        assertEquals("2a", 1, oWritableProperty.m_nUpdateCounter);
        assertEquals("2b", "some-value", oWritableProperty.m_vViewValue);
    };
})();
