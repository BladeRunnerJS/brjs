"use strict";

/**
 * @module br/presenter/workbench/ui/PresenterJsTreeModelFactory
 */

var Property = require('br/presenter/property/Property');
var PresentationNode = require('br/presenter/node/PresentationNode');
var NodeList = require('br/presenter/node/NodeList');
var Field  = require('br/presenter/node/Field');
var SelectionField = require('br/presenter/node/SelectionField');

/**
 * @class
 * @alias module:br/presenter/workbench/ui/PresenterJsTreeModelFactory
 */
function PresenterJsTreeModelFactory() {
};

PresenterJsTreeModelFactory.createTreeModelFromPresentationModel = function(presentationModel) {
	var treeModel = {core: {data: [{text: 'Presentation Model', state: {opened: true}, children: []}]}, onChange:function(){}};
	this._uniqueId = 0;
	this._processViewModel(presentationModel, treeModel, treeModel.core.data[0].children);
	
	return treeModel;
};

PresenterJsTreeModelFactory._processViewModel = function(presentationNode, treeModel, treeModelItems) {
	for(var itemName in presentationNode) {
		if(!this._isPrivate(itemName)) {
			var item = presentationNode[itemName];
			
			if(item instanceof Property) {
				var nodeLabel = itemName;
				if (item.getValue() !== undefined) {
					nodeLabel += ": " + item.getValue();
				}
				var newId = this._uniqueId++;
				var treeItem = {id: newId, text:nodeLabel};
				
				item.addListener(new TreeItemPropertyListener(treeModel, itemName, treeItem, item));
				
				treeModelItems.push(treeItem);
			}
			else if (item instanceof PresentationNode)
			{
				var expanded = !((item instanceof Field || item instanceof SelectionField));
				var childTreeModel = {text:itemName, state: {opened: expanded}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, treeModel, childTreeModel.children);
			}
			else if (item instanceof NodeList)
			{
				var childTreeModel = {text:itemName, state: {opened: true}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, treeModel, getPresentationNodesArray());
			}
		}
	}
};

PresenterJsTreeModelFactory._isPrivate = function(itemName) {
	return itemName.match(/^m?_/) !== null;
};


var brCore = require('br/Core');
var PropertyListener = require('br/presenter/property/PropertyListener');

function TreeItemPropertyListener(treeModel, treeItemName, treeItem, treeItemProperty) {
	this._treeModel = treeModel;
	this._treeItem = treeItem;
	this._treeItemName = treeItemName;
	this._treeItemProperty = treeItemProperty;
}
// Note: this has to be br.extend() because it's a call-back interface with only optional methods
brCore.extend(TreeItemPropertyListener, PropertyListener);

TreeItemPropertyListener.prototype.onPropertyChanged = function() {
	this._treeItem.text = this._treeItemName + ": " + this._treeItemProperty.getValue();
	this._treeModel.onChange(this._treeItem.id, this._treeItem.text);
};


// TODO: switch to the other line once this package is moved to CommonJs
br.presenter.workbench.ui.PresenterJsTreeModelFactory = PresenterJsTreeModelFactory;
//module.exports = PresenterJsTreeModelFactory;
