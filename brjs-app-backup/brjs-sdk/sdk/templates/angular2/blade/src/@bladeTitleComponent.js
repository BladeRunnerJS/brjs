'use strict';

var i18n = require('br/I18n');
var ng = require("angular2");

function @bladeTitleComponent() {

	this._eventHub = require('service!br.event-hub');
	this.welcomeMessage = 'Welcome to your new Blade.';
	this.buttonClickMessage = i18n('@bladeNamespace.button.click.message');
	this._logWelcome();

}

// has to be class level
@bladeTitleComponent.annotations = [
 new ng.core.Component({
	 selector: '@bladeTitle' // adjust as needed
 }),
 new ng.core.View({
	 template: require('service!br.html-service').getTemplateElement( '@bladeNamespace.view-template' ).outerHTML
 }),

];


@bladeTitleComponent.prototype._logWelcome = function() {
	console.log(this.welcomeMessage);
};

@bladeTitleComponent.prototype.buttonClicked = function() {
	console.log('button clicked');
	var channel = this._eventHub.channel('@blade-channel');
	channel.trigger('hello-event', {some: 'Hello World!'});
};

module.exports = @bladeTitleComponent;
