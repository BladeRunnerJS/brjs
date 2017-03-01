'use strict';

var ko = require('ko');
var i18n = require('br/I18n');

function @bladeTitleViewModel() {
	this._eventHub = require('service!br.event-hub');
	this.welcomeMessage = ko.observable('Welcome to your new Blade.');
	this.buttonClickMessage = ko.observable(i18n('@bladeNamespace.button.click.message'));
	this._logWelcome();
}

@bladeTitleViewModel.prototype.buttonClicked = function() {
	console.log('button clicked');
	var channel = this._eventHub.channel('@blade-channel');
	channel.trigger('hello-event', {some: 'Hello World!'});
};

@bladeTitleViewModel.prototype._logWelcome = function() {
	console.log(this.welcomeMessage());
};

module.exports = @bladeTitleViewModel;
