"use strict";

var ko = require( 'ko' );

function @class-name() {
	this.message = ko.observable( '' );
}

@class-name.prototype.buttonClicked = function() {
	console.log("button clicked");
};

module.exports = @class-name;
