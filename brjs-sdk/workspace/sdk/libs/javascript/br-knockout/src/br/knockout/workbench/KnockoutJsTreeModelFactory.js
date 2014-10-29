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
	var treeModel = {core: {data: [{text: 'Knockout View Model', state: {opened: true}, children: []}]}, onChange:function(){}};
	this._uniqueID = 0;
	this._processViewModel(viewModel, treeModel, treeModel.core.data[0].children);
	
	return treeModel;
};

KnockoutJsTreeModelFactory._processViewModel = function(viewModel, treeModel, treeModelItems) {
	for(var itemName in viewModel) {
		if(!this._isPrivate(itemName)) {
			var item = viewModel[itemName];
			
			if(ko.isObservable(item)) {
				var treeItem = {id: "node_id_" + this._uniqueID++ , text:itemName + ": " + item()};
				item.subscribe(this._createNewTreeItemSubscriber(treeModel, itemName, treeItem));
				treeModelItems.push(treeItem);
			}
			else if((typeof item === 'object') && this._hasObservables(item)) {
				var childTreeModel = {text:itemName, state: {opened: true}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, treeModel, childTreeModel.children);
			}
			else if((typeof item !== 'function')) {
				treeModelItems.push({text:itemName + ": " + item});
			}
		}
	}
};

KnockoutJsTreeModelFactory._createNewTreeItemSubscriber = function(treeModel, itemName, treeItem) {
	return function(newValue) {
		treeItem.text = itemName + ": " + newValue;
		treeModel.onChange(treeItem.id, treeItem.text);
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
