'use strict';

var i18n = require('br/I18n');

function @bladeTitleDirective() {
    var self = this
    this._eventHub = require('service!br.event-hub');

    var HtmlService = require('service!br.html-service');
    this.restrict = 'E';
    this.replace = true;
    this.template = HtmlService.getTemplateElement('@bladeNamespace.view-template').outerHTML;
    this.privateScope

    this.link = function (scope, iElement, iAttrs, controller) {
        self.privateScope = scope // open up scope access to other functions
        self._logWelcome();
    }

    this.controller = function ($scope) {
        $scope.welcomeMessage = 'Welcome to your new Blade.';
        $scope.buttonClickMessage = i18n('@bladeNamespace.button.click.message');

        $scope.buttonClicked = function () {
            console.log('button clicked');
            var channel = self._eventHub.channel('@blade-channel');
            channel.trigger('hello-event', {some: 'Hello World!'});
        }

    }

}

@bladeTitleDirective.prototype.welcomeMessage = function() {
	return this.privateScope.welcomeMessage; // will only work after angular initialization
};


@bladeTitleDirective.prototype._logWelcome = function() {
	console.log(this.welcomeMessage());
};

module.exports = @bladeTitleDirective;
