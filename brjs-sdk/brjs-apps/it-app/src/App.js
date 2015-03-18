'use strict';

var ItbladeViewModel = require('itapp/itbladeset/itblade/ItbladeViewModel');
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );
var TestingClass = require('itapp/itbladeset/itblade/TestingClass');
var CommonJsLib = require('commonjslib/CommonJsLib');
var Testlib3p = require('testlib3p');
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
	return obj.implementMe();
};

App.prototype.playWithLibs = function() {
	var helloTests = [Testlib3p.hello, CommonJsLib.hello, namedspacedjslib.NamedspacedJsLib.hello];
	var libsOutput = "";
	for(var i = 0 ; i < helloTests.length ; i++) {
		libsOutput += helloTests[i]() + '\n';
	}

	return libsOutput;
};

module.exports = App;
