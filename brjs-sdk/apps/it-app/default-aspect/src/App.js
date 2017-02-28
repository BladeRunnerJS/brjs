'use strict';

var ItbladeViewModel = require('itapp/itbladeset/itblade/ItbladeViewModel');
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );
var CommonJsLib = require('commonjslib/CommonJsLib');
var Testlib3p = require('testlib3p');
var i18n = require( 'br/I18n' );

var App = function() {
    var element = document.getElementById("hello-world");
    element.innerHTML="Successfully loaded the application";
	this.outputTable = document.getElementById("outputTable");

	this.addItBladeToView();
	this.functionalityTest("i18n", this.testI18n);
	this.functionalityTest("aliasing", this.playWithAliases);
	this.functionalityTest("aliasing-implement-fail", this.aliasFail);
	this.functionalityTest("aliasing-app-override", this.playWithAppLevelAliases);
	this.functionalityTest("common-js", CommonJsLib.hello);
	this.functionalityTest("third-party-lib", Testlib3p.hello);
	this.functionalityTest("jndi", this.testJNDI);
	this.functionalityTest("xml-bundle", this.testBundledXml);
};

App.prototype.functionalityTest = function(testMessage, fun, context) {
	context = context || this;
	var newRow = this.outputTable.insertRow(-1);
	var testMessageCell = newRow.insertCell(0);
	var testOutputCell = newRow.insertCell(1);
	testMessageCell.innerHTML = "<b>" + testMessage + "</b>";
	testOutputCell.innerHTML = fun.call(context);
};

App.prototype.testI18n = function() {
	return i18n('itapp.hello.world');
};

App.prototype.testJNDI = function() {
	return window.jndiToken;
};

App.prototype.addItBladeToView = function() {
	var itBladeViewModel = new ItbladeViewModel();
	var koComponent = new KnockoutComponent('itapp.itbladeset.itblade.view-template', itBladeViewModel);
	document.getElementById("Itblade").appendChild(koComponent.getElement());
};

App.prototype.playWithAliases = function() {
	var AliasedClass = require('alias!itapp.itbladeset.itblade.NewName');
	var obj = new AliasedClass();
	return obj.implementMe();
};

App.prototype.playWithAppLevelAliases = function() {
	var AliasedClass = require('alias!itapp.itbladeset.itblade.AppLevelOverrideAlias');
	var obj = new AliasedClass();
	return obj.implementMe();
};

App.prototype.aliasFail = function() {
	try {
		require('alias!itapp.itbladeset.itblade.ImplementFail');
	}
	catch (err) {
		if(err.name === "AliasInterfaceError") {
			return "Aliasing successfully prevented";
		}
	}
	return "Aliasing did not throw an AliasInterfaceError";
};

App.prototype.testBundledXml = function () {
    var childNode = require('service!br.xml-service').getXmlDocument("bundledXml")[0].childNodes[0];
    if (childNode.text) {
        return childNode.text;
    }
	return childNode.innerHTML;
};

module.exports = App;
