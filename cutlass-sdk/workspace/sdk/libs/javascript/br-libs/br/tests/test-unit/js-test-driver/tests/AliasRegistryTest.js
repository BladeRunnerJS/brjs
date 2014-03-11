(function(){
	"use strict";
	
	require("jstestdriverextensions");
	require("jsmockito");
	var br = require('br/Core');
	var Errors = require('br/Errors');
	var AliasRegistry = require('br/AliasRegistry').constructor;
	
	var aliasRegistry;
	var testAliasData = {};


	//Define all classes and interfaces
	var Alias1Interface = function(){};
	Alias1Interface.prototype.interfaceFunction = function(){};

	var Alias1AlternateInterface = function(){};
	Alias1AlternateInterface.prototype.alternateInterfaceFunction = function(){};

	var Alias2Interface = function(){};
	Alias2Interface.prototype.interfaceFunction2 = function(){};

	
	// setup our Alias classes
	var pkg = {
		a: {},
		b: {}
	};
	pkg.a.AliasClass1 = function() {};
	pkg.a.AliasClass1.prototype.interfaceFunction = function(){};
	pkg.a.AliasClass1.prototype.alternateInterfaceFunction = function(){};

	pkg.b.AliasClass2 = function() {};
	pkg.b.AliasClass2.prototype.interfaceFunction2 = function(){};

	br.hasImplemented(pkg.a.AliasClass1, Alias1Interface);
	br.hasImplemented(pkg.a.AliasClass1, Alias1AlternateInterface);
	br.hasImplemented(pkg.b.AliasClass2, Alias2Interface);
	
	
	var onAliasRegistry = defineTestCase("AliasRegistryTest",
		function() //setUp
		{
			JsHamcrest.Integration.JsTestDriver();
			JsMockito.Integration.JsTestDriver();

			//alias registry data
			testAliasData = {
				"some.alias1":
				{
					"class":pkg.a.AliasClass1,
					"className":"pkg.a.AliasClass1",
					"interface":Alias1Interface,
					"interfaceName":"Alias1Interface"
				},
				"some.alias2":
				{
					"class":pkg.b.AliasClass2,
					"className":"pkg.b.AliasClass2"
				},
				"some.alias3":
				{
				}
			};

			aliasRegistry = new AliasRegistry();
			aliasRegistry.setAliasData(testAliasData);

			this.definitionRegistry = require('br/TestDefinitionRegistry').install();
			this.definitionRegistry.define('br/AliasRegistry', aliasRegistry);
		}
	);
	
	//setAliasData

	onAliasRegistry.testThat("Service Registry instance can be used", function()
	{
		assertNotEquals("The aliases list should be empty", [], require('br/AliasRegistry').getAllAliases());
	});

	onAliasRegistry.testThat("Return an empty list of aliases if there is no Alias", function()
	{
		aliasRegistry = new AliasRegistry();
		aliasRegistry.setAliasData({});
		assertEquals("The aliases list should be empty", [], aliasRegistry.getAllAliases());
	});

	onAliasRegistry.testThat("Return the list of aliases from the alias JSON", function()
	{
		assertEquals("Incorrect alias list", ["some.alias1", "some.alias2", "some.alias3"], aliasRegistry.getAllAliases());
	});

	onAliasRegistry.testThat("No aliases are returned for an unknown interface", function()
	{
		var fSomeInterface = function(){};
		fSomeInterface.prototype.someMethod = function(){};
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(fSomeInterface);

		assertEquals("Incorrect alias list", [], pFilteredAliases);
	});

	onAliasRegistry.testThat("All aliases that explicitly associate themselves with an interface are returned", function()
	{
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias1Interface);
		assertEquals("Incorrect alias list", ["some.alias1"], pFilteredAliases);
	});

		onAliasRegistry.testThat("All aliases that actually implement an interface are returned", function()
		{
			var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias2Interface);
			assertEquals("Incorrect alias list", ["some.alias2"], pFilteredAliases);
		});

		onAliasRegistry.testThat("All aliases that actually implement an interface are returned, even if they explicitly implement another interface", function()
		{
			var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias1AlternateInterface);
			assertEquals("Incorrect alias list", ["some.alias1"], pFilteredAliases);
		});

		onAliasRegistry.testThat("Check isAlias returns the correct values", function()
		{
			assertTrue(aliasRegistry.isAlias("some.alias1"));
			assertTrue(aliasRegistry.isAlias("some.alias3"));
			assertFalse(aliasRegistry.isAlias("some.alias4"));
		});

		onAliasRegistry.testThat("Check isAliasAssigned returns the correct values", function()
		{
			assertTrue(aliasRegistry.isAliasAssigned("some.alias1"));
			assertFalse(aliasRegistry.isAliasAssigned("some.alias3"));
		});

		onAliasRegistry.testThat("Fails fast when alias is not found", function()
		{
			var self = this;
			assertException("Should throw an error if the alias doesn't exist",
				function() {
					aliasRegistry.getClass("some.alias3");
				},
				Errors.ILLEGAL_STATE
			);
		});

		onAliasRegistry.testThat("Returns the correct class when an existing alias is requested", function()
		{
			var fClass = aliasRegistry.getClass("some.alias1");
			assertEquals("The class retireved is incorrect", pkg.a.AliasClass1, fClass);
		});

		onAliasRegistry.testThat("Fails fast if alias is not implementor of the alias interface", function()
		{
			testAliasData["some.alias1"]["class"] = function(){};
			aliasRegistry = new AliasRegistry();
			assertException("Should throw an error if the alias is not implementor of the alias interface",
				function() {
					aliasRegistry.setAliasData(testAliasData);
				},
				Errors.ILLEGAL_STATE
			);
		});

})();