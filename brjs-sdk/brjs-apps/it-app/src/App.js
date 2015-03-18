'use strict';

var ItbladeViewModel = require('itapp/itbladeset/itblade/ItbladeViewModel');
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );
var TestingClass = require('itapp/itbladeset/itblade/TestingClass');
require('namedspacedjslib/NamedspacedJsLib');

var App = function() {
    var element = document.getElementById("hello-world");
    element.innerHTML="Successfully loaded the application";

	this.addItBladeToView();
	console.log(this.playWithAliases());
	console.log(this.playWithLibs());
};

App.prototype.addItBladeToView = function() {
	var itBladeViewModel = new ItbladeViewModel();
	var koComponent = new KnockoutComponent('itapp.itbladeset.itblade.view-template', itBladeViewModel);
	document.getElementById("Itblade").appendChild(koComponent.getElement());
};

App.prototype.playWithAliases = function() {
	var AliasedClass = require('alias!itapp.itbladeset.itblade.NewName');
	var obj = new AliasedClass();
	return obj.foo();
};

App.prototype.playWithLibs = function() {
	var testClass = new TestingClass();
	return namedspacedjslib.NamedspacedJsLib.hello() + "\n" + testClass.use3rdPartyLib();
};

module.exports = App;
