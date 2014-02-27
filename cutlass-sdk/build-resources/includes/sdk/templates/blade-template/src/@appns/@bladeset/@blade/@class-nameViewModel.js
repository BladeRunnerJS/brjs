"use strict";

var ko = require( 'ko' );

function @class-nameViewModel() {
	this.message = ko.observable( '' );
}

@class-name.prototype.buttonClicked = function() {
	console.log("button clicked");
};

module.exports = @class-name;
