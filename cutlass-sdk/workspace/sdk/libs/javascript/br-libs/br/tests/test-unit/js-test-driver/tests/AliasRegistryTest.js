require("jstestdriverextensions");
require("jsmockito");

(function() {

	var AliasRegistry = require('br/AliasRegistry').constructor;
	
	var onAliasRegistry = defineTestCase("AliasRegistryTest",
			function() //setUp
			{
				JsHamcrest.Integration.JsTestDriver();
				JsMockito.Integration.JsTestDriver();

				//Define all classes and interfaces
				Alias1Interface = function(){};
				Alias1Interface.prototype.interfaceFunction = function(){};

				Alias1AlternateInterface = function(){};
				Alias1AlternateInterface.prototype.alternateInterfaceFunction = function(){};

				Alias2Interface = function(){};
				Alias2Interface.prototype.interfaceFunction2 = function(){};

				
				
//				caplin.namespace("novox.a");
//				novox.a.AliasClass1 = function(){};
//
//				caplin.namespace("novox.b");
//				novox.b.AliasClass2 = function(){};
//
//				caplin.implement(novox.a.AliasClass1, Alias1Interface);
//				caplin.implement(novox.a.AliasClass1, Alias1AlternateInterface);
//				caplin.implement(novox.b.AliasClass2, Alias2Interface);
//
//				//alias registry data
//				caplin.__aliasData = {
//					"novox.alias1":
//					{
//						"class":novox.a.AliasClass1,
//						"className":"novox.a.AliasClass1",
//						"interface":Alias1Interface,
//						"interfaceName":"Alias1Interface"
//					},
//					"novox.alias2":
//					{
//						"class":novox.b.AliasClass2,
//						"className":"novox.b.AliasClass2"
//					},
//					"novox.alias3":
//					{
//					}
//				};
			}
		);
	
	//setAliasData

		onAliasRegistry.testThat("Service Registry instance can be used", function()
		{
			assertNotEquals("The aliases list should be empty", [], require('br/AliasRegistry').getAllAliases());
		});

		onAliasRegistry.testThat("Return an empty list of aliases if there is no Alias", function()
		{
			var aliasRegistry = new AliasRegistry();
			aliasRegistry.setAliasData({});
			assertEquals("The aliases list should be empty", [], aliasRegistry.getAllAliases());
		});

//		onAliasRegistry.testThat("Return the list of aliases from the alias JSON", function()
//		{
//			var pAllAliases = caplin.core.AliasRegistry.getAllAliases();
//
//			assertEquals("Incorrect alias list", ["novox.alias1", "novox.alias2", "novox.alias3"], pAllAliases);
//
//		});
//
//		onAliasRegistry.testThat("No aliases are returned for an unknown interface", function()
//		{
//			var fSomeInterface = function(){};
//			fSomeInterface.prototype.someMethod = function(){};
//			var pFilteredAliases = caplin.core.AliasRegistry.getAliasesByInterface(fSomeInterface);
//
//			assertEquals("Incorrect alias list", [], pFilteredAliases);
//
//		});
//
//		onAliasRegistry.testThat("All aliases that explicitly associate themselves with an interface are returned", function()
//		{
//			var pFilteredAliases = caplin.core.AliasRegistry.getAliasesByInterface(Alias1Interface);
//
//			assertEquals("Incorrect alias list", ["novox.alias1"], pFilteredAliases);
//
//		});
//
//		onAliasRegistry.testThat("All aliases that actually implement an interface are returned", function()
//		{
//			var pFilteredAliases = caplin.core.AliasRegistry.getAliasesByInterface(Alias2Interface);
//
//			assertEquals("Incorrect alias list", ["novox.alias2"], pFilteredAliases);
//
//		});
//
//		onAliasRegistry.testThat("All aliases that actually implement an interface are returned, even if they explicitly implement another interface", function()
//		{
//			var pFilteredAliases = caplin.core.AliasRegistry.getAliasesByInterface(Alias1AlternateInterface);
//
//			assertEquals("Incorrect alias list", ["novox.alias1"], pFilteredAliases);
//
//		});
//
//		onAliasRegistry.testThat("Check isAlias returns the correct values", function()
//		{
//			assertTrue(caplin.core.AliasRegistry.isAlias("novox.alias1"));
//			assertTrue(caplin.core.AliasRegistry.isAlias("novox.alias3"));
//			assertFalse(caplin.core.AliasRegistry.isAlias("novox.alias4"));
//		});
//
//		onAliasRegistry.testThat("Check isAliasAssigned returns the correct values", function()
//		{
//			assertTrue(caplin.core.AliasRegistry.isAliasAssigned("novox.alias1"));
//			assertFalse(caplin.core.AliasRegistry.isAliasAssigned("novox.alias3"));
//		});
//
//		onAliasRegistry.testThat("Fails fast when alias is not found", function()
//		{
//			var self = this;
//
//			assertException("Should throw an error if the alias doesn't exist",
//				function() {
//					caplin.core.AliasRegistry.getClass("novox.alias3");
//				},
//				br.Errors.ILLEGAL_STATE
//			);
//
//		});
//
//		onAliasRegistry.testThat("Returns the correct class when an existing alias is requested", function()
//		{
//			var fClass = caplin.core.AliasRegistry.getClass("novox.alias1");
//
//			assertEquals("The class retireved is incorrect", novox.a.AliasClass1, fClass);
//		});
//
//		onAliasRegistry.testThat("Fails fast if alias is not implementor of the alias interface", function()
//		{
//			caplin.__aliasData["novox.alias1"]["class"] = function(){};
//
//			assertException("Should throw an error if the alias is not implementor of the alias interface",
//					function() {
//						caplin.core.AliasRegistry.getClass("novox.alias1");
//					},
//					br.Errors.ILLEGAL_STATE
//			);
//		});
})();

