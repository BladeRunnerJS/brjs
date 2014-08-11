"use strict";

var ko = require('ko');

function JsTreeModelFactory() {
};

JsTreeModelFactory.createTreeModelFromKnockoutViewModel = function(viewModel) {
	var treeModel = {core:{data:[]}};
	this._processViewModel(viewModel, treeModel.core.data);
	
	return treeModel;
};

JsTreeModelFactory._processViewModel = function(viewModel, treeModelItems) {
	for(var itemName in viewModel) {
		if(!this._isPrivate(itemName)) {
			var item = viewModel[itemName];
			
			if(ko.isObservable(item)) {
				treeModelItems.push({text:itemName + ": " + item()});
			}
			else if((typeof item === 'object') && this._hasObservables(item)) {
				var childTreeModel = {text:itemName, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, childTreeModel.children);
			}
			else if((typeof item !== 'function')) {
				treeModelItems.push({text:itemName + ": " + item});
			}
		}
	}
};

JsTreeModelFactory._hasObservables = function(object) {
	var isObservable = false;
	
	for(var key in object) {
		isObservable = ko.isObservable(object[key]);
		
		if(isObservable) {
			break;
		}
	}
	
	return isObservable;
};

JsTreeModelFactory._isPrivate = function(itemName) {
	return itemName.match(/^m?_/) !== null;
};

module.exports = JsTreeModelFactory;
