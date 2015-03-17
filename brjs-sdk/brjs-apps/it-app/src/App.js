'use strict';

var ItbladeViewModel = require('itapp/itbladeset/itblade/ItbladeViewModel');
var KnockoutComponent = require( 'br/knockout/KnockoutComponent' );

var App = function() {
    var element = document.getElementById("hello-world");
    element.innerHTML="Successfully loaded the application";

	var itBladeViewModel = new ItbladeViewModel();
	var koComponent = new KnockoutComponent('itapp.itbladeset.itblade.view-template', itBladeViewModel);
	document.getElementById("Itblade").appendChild(koComponent.getElement());
};

module.exports = App;
