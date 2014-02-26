"use strict";

var br = require('br/Core');
var Property = require('br/presenter/property/Property');
var PresentationModel = require('br/presenter/PresentationModel');

function @class-name() {
	this.message = new Property("Hello World!");
}
br.extend( @class-name, PresentationModel );

@class-name.prototype.buttonClicked = function() {
	console.log("button clicked");
};

module.exports = @class-name;
