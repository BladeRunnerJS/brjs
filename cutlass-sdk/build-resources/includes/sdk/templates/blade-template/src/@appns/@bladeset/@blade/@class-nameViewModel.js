'use strict';

var ko = require( 'ko' );

function @class-nameViewModel() {
	this.message = ko.observable( 'Hello World!' );
}

@class-nameViewModel.prototype.buttonClicked = function() {
	console.log( 'button clicked' );
};

module.exports = @class-nameViewModel;
