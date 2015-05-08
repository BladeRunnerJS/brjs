(function(){
	"use strict";

	// TODO: this line can be deleted once CommonJs supports aliases
	require('br/workaround/CommonJsAliasWorkaround');

	require("jsmockito");
	var br = require('br/Core');
	var Errors = require('br/Errors');
	var AliasClass1 = require('br/a/AliasClass1');
	var Alias1Interface = require('br/Alias1Interface');
	var Alias2Interface = require('br/Alias2Interface');
	var Alias1AlternateInterface = require('br/Alias1AlternateInterface');

	var AliasRegistry = require('br/AliasRegistryClass');
	var AliasRegistryTest = TestCase("AliasRegistryTest").prototype;

	var AliasInterfaceError = require("br/AliasInterfaceError");

	var aliasRegistry = null;

	AliasRegistryTest.setUp = function()
	{
		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();

		aliasRegistry = require('br/AliasRegistry');
	};

	AliasRegistryTest["test Service Registry instance can be used"] = function()
	{
		assertNotEquals("The aliases list should be empty", [], require('br/AliasRegistry').getAllAliases());
	};

	AliasRegistryTest["test Return an empty list of aliases if there is no Alias"] = function()
	{
		aliasRegistry = new AliasRegistry({});
		assertEquals("The aliases list should be empty", [], aliasRegistry.getAllAliases());
	};

	AliasRegistryTest["test Return the list of aliases from the alias JSON"] = function()
	{
		assertEquals("Incorrect alias list", ["some.alias2", "some.alias1"], aliasRegistry.getAllAliases().filter(function(str) {return str.match(/^some\.alias/)}));
	};

	AliasRegistryTest["test No aliases are returned for an unknown interface"] = function()
	{
		var fSomeInterface = function(){};
		fSomeInterface.prototype.someMethod = function(){};
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(fSomeInterface);

		assertEquals("Incorrect alias list", [], pFilteredAliases);
	};

	AliasRegistryTest["test All aliases that explicitly associate themselves with an interface are returned"] = function()
	{
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias1Interface);
		assertEquals("Incorrect alias list", ["some.alias1"], pFilteredAliases);
	};

	AliasRegistryTest["test All aliases that actually implement an interface are returned"] = function()
	{
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias2Interface);
		assertEquals("Incorrect alias list", ["some.alias2"], pFilteredAliases);
	};

	AliasRegistryTest["test All aliases that actually implement an interface are returned, even if they explicitly implement another interface"] = function()
	{
		var pFilteredAliases = aliasRegistry.getAliasesByInterface(Alias1AlternateInterface);
		assertEquals("Incorrect alias list", ["some.alias1"], pFilteredAliases);
	};

	AliasRegistryTest["test Check isAlias returns the correct values"] = function()
	{
		assertTrue(aliasRegistry.isAlias("some.alias1"));
		assertFalse(aliasRegistry.isAlias("some.alias4"));
	};

	AliasRegistryTest["test Check isAliasAssigned returns the correct values"] = function()
	{
		assertTrue(aliasRegistry.isAliasAssigned("some.alias1"));
	};

	AliasRegistryTest["test Fails fast when alias is not found"] = function()
	{
		var self = this;
		assertException("Should throw an error if the alias doesn't exist",
			function() {
				aliasRegistry.getClass("some.alias3");
			},
			Errors.ILLEGAL_STATE
		);
	};

	AliasRegistryTest["test Returns the correct class when an existing alias is requested"] = function()
	{
		var fClass = aliasRegistry.getClass("some.alias1");
		assertEquals("The class retireved is incorrect", AliasClass1, fClass);
	};

	AliasRegistryTest["test Fails fast if alias is not an implementer of the alias interface"] = function()
	{
		var aliasRegistry = new AliasRegistry({
			"some.alias1": {
				"class":"br/a/AliasClass1",
				"className":"br.a.AliasClass1",
				"interface":"br/Alias2Interface",
				"interfaceName":"br.Alias2Interface"
			}});

		assertException("Should throw an error if the alias is not implementor of the alias interface",
			function() {
				aliasRegistry.getClass('some.alias1');
			},
			Errors.AliasInterfaceError
		);
	};

	AliasRegistryTest["test Null class can be set for abstract aliases"] = function()
	{
		aliasRegistry= new AliasRegistry({
			"some.alias1": {
				"interface":"br/Alias1Interface",
				"interfaceName":"br.Alias1Interface"
			}});
		assertTrue(aliasRegistry.isAlias("some.alias1"));
	};

})();
