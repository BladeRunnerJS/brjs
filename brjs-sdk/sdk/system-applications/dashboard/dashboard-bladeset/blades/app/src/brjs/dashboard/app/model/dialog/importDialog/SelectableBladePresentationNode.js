'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');
var Property = require('br/presenter/property/Property');

function SelectableBladePresentationNode(sBlade) {
	this.bladeName = new Property(sBlade);
	this.isSelected = new EditableProperty(true);
}

Core.extend(SelectableBladePresentationNode, PresentationNode);

module.exports = SelectableBladePresentationNode;
