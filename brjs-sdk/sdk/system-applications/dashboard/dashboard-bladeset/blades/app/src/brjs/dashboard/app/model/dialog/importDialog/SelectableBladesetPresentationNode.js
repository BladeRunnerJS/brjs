'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var EditableProperty = require('br/presenter/property/EditableProperty');
var NodeList = require('br/presenter/node/NodeList');
var Property = require('br/presenter/property/Property');
var SelectableBladePresentationNode = require('brjs/dashboard/app/model/dialog/importDialog/SelectableBladePresentationNode');
var Field = require('brjs/dashboard/app/model/form/Field');
var BladesetNameValidator = require('brjs/dashboard/app/model/dialog/validator/BladesetNameValidator');

function SelectableBladesetPresentationNode(oPresentationModel, sBladeset, pBladePresentationNodes, fNewBladesetNameValidationListener) {
	this.m_oPresentationModel = oPresentationModel;

	this.bladesetName = new Property(sBladeset);
	this.blades = new NodeList(pBladePresentationNodes, SelectableBladePresentationNode);
	this.isSelected = new EditableProperty(true);
	this.isIndeterminate = new WritableProperty(false);

	this.displayNewBladesetField = new WritableProperty(true);
	this.newBladesetName = new Field('-- Please name your bladeset --', sBladeset);
	this.newBladesetName.value.addValidator(new BladesetNameValidator(oPresentationModel));
	this.newBladesetName.value.addValidationCompleteListener(this, '_updateDialog');
	this.m_fNewBladesetNameValidationListener = fNewBladesetNameValidationListener;

	this.isSelected.addChangeListener(this, '_onSelectedChanged');
	this.blades.properties('isSelected').addChangeListener(this, '_onChildSelectedChanged');

	this.isSelected.setValue(false);
}

Core.extend(SelectableBladesetPresentationNode, PresentationNode);

SelectableBladesetPresentationNode.prototype._onSelectedChanged = function() {
	this.blades.properties('isSelected').setValue(this.isSelected.getValue());
	this.newBladesetName.value.forceValidation();
	this.displayNewBladesetField.setValue(this.isSelected.getValue());
};

SelectableBladesetPresentationNode.prototype._onChildSelectedChanged = function() {
	var nChildBladesSelected = this.blades.properties('isSelected', true).getSize();

	if (nChildBladesSelected == 0) {
		this.isSelected.setValue(false);
		this.isIndeterminate.setValue(false);
		this.displayNewBladesetField.setValue(false);
	} else if (nChildBladesSelected == this.blades.getPresentationNodesArray().length) {
		this.isSelected.setValue(true);
		this.isIndeterminate.setValue(false);
		this.displayNewBladesetField.setValue(true);
	} else {
		this.isIndeterminate.setValue(true);
		this.displayNewBladesetField.setValue(true);
	}
};

SelectableBladesetPresentationNode.prototype._updateDialog = function() {
	this.m_fNewBladesetNameValidationListener();
};

module.exports = SelectableBladesetPresentationNode;
