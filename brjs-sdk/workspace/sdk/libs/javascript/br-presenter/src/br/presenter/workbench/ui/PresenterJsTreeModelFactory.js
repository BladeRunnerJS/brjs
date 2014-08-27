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
	var treeModel = {core: {data: [{text: 'Presentation Model', state: {opened: true}, children: []}]}};
	this._processViewModel(presentationModel, treeModel.core.data[0].children);
	
	return treeModel;
};

PresenterJsTreeModelFactory._processViewModel = function(presentationNode, treeModelItems) {
	for(var itemName in presentationNode) {
		if(!this._isPrivate(itemName)) {
			var item = presentationNode[itemName];
			
			if(item instanceof Property) {
				var nodeLabel = itemName;
				if (item.getValue() !== undefined) {
					nodeLabel += ": " + item.getValue();
				}
				
				treeModelItems.push({text:nodeLabel});
			}
			else if (item instanceof PresentationNode)
			{
				var expanded = !((item instanceof Field || item instanceof SelectionField));
				var childTreeModel = {text:itemName, state: {opened: expanded}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, childTreeModel.children);
			}
			else if (item instanceof NodeList)
			{
				var childTreeModel = {text:itemName, state: {opened: true}, children:[]};
				
				treeModelItems.push(childTreeModel);
				this._processViewModel(item, getPresentationNodesArray());
			}
		}
	}
};

PresenterJsTreeModelFactory._isPrivate = function(itemName) {
	return itemName.match(/^m?_/) !== null;
};

// TODO: switch to the other line once this package is moved to CommonJs
br.presenter.workbench.ui.PresenterJsTreeModelFactory = PresenterJsTreeModelFactory;
//module.exports = PresenterJsTreeModelFactory;
