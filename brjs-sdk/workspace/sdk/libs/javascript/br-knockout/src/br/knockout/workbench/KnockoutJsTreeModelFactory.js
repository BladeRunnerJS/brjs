"use strict";

/**
 * @module br/knockout/workbench/KnockoutJsTreeModelFactory
 */

var ko = require('ko');

/**
 * @class
 * @alias module:br/knockout/workbench/KnockoutJsTreeModelFactory
 */
function KnockoutJsTreeModelFactory() {
};

KnockoutJsTreeModelFactory.createTreeModelFromKnockoutViewModel = function(viewModel) {
	var treeModel = {core: {data: [{text: 'Knockout View Model', state: {opened: true}, children: []}]}};
	this._processViewModel(viewModel, treeModel.core.data[0].children);
	
	return treeModel;
};

KnockoutJsTreeModelFactory._processViewModel = function(viewModel, treeModelItems) {
	for(var itemName in viewModel) {
		if(!this._isPrivate(itemName)) {
			var item = viewModel[itemName];
			
			if(ko.isObservable(item)) {
				treeModelItems.push({text:itemName + ": " + item()});
			}
			else if((typeof item === 'object') && this._hasObservables(item)) {
				var childTreeModel = {text:itemName, state: {opened: true}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, childTreeModel.children);
			}
			else if((typeof item !== 'function')) {
				treeModelItems.push({text:itemName + ": " + item});
			}
		}
	}
};

KnockoutJsTreeModelFactory._hasObservables = function(object) {
	var isObservable = false;
	
	for(var key in object) {
		isObservable = ko.isObservable(object[key]);
		
		if(isObservable) {
			break;
		}
	}
	
	return isObservable;
};

KnockoutJsTreeModelFactory._isPrivate = function(itemName) {
	return itemName.match(/^m?_/) !== null;
};

module.exports = KnockoutJsTreeModelFactory;
