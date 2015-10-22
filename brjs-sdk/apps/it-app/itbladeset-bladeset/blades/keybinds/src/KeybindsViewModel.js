'use strict';

var ko = require('ko');
var i18n = require('br/I18n');
var keybindingService = require('service!br.keybinding-service');

function KeybindsViewModel() {
	this._eventHub = require('service!br.event-hub');
	this.keyBindMessage = ko.observable('Press Shift+V to switch vegetables: ');
	this.keyBoundVegetable = ko.observable('Carrot');
	
	var self = this;
	keybindingService.registerAction('itapp.a-key-binding', function() {
			self.switchVeg();
		});

	keybindingService.bindAction('itapp.a-key-binding', 'shift+v');
}

KeybindsViewModel.prototype.switchVeg = function() {
	if(this.keyBoundVegetable() === "Carrot"){
		this.keyBoundVegetable("Tomato");
	}
	else
		this.keyBoundVegetable("Carrot");
}

module.exports = KeybindsViewModel;
