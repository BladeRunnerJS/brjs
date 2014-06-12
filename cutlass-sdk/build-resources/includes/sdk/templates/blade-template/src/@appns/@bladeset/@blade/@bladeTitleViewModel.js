'use strict';

var ko = require( 'ko' );

function @bladeTitleViewModel() {
	this.message = ko.observable( 'Hello World!' );
}

@bladeTitleViewModel.prototype.buttonClicked = function() {
	console.log( 'button clicked' );
};

module.exports = @bladeTitleViewModel;
