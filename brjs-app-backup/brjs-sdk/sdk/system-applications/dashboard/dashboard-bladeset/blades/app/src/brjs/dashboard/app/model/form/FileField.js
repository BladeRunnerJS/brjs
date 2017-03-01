'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var Property = require('br/presenter/property/Property');
var WritableProperty = require('br/presenter/property/WritableProperty');

function FileField(sAccepts) {
	this.accepts = new WritableProperty(sAccepts);
	this.fileSelected = new Property(false);
	this.enabled = new WritableProperty(true);
	this.fileName = new WritableProperty('');
	this.fileInputVisible = new WritableProperty(true);
	this.fileInfoVisible = new WritableProperty(false);
}

Core.extend(FileField, PresentationNode);

FileField.prototype.onChange = function(oViewModel, oEvent) {
	this.m_eFileInput = oEvent.currentTarget;

	this._setFileName();


	if (this.m_eFileInput.value) {
		this.fileSelected._$setInternalValue(true);
		this.fileInputVisible.setValue(false);
		this.fileInfoVisible.setValue(true);
	} else {
		this.fileSelected._$setInternalValue(false);
		this.fileInputVisible.setValue(true);
		this.fileInfoVisible.setValue(false);
	}
};

FileField.prototype.getFileInput = function() {
	return this.m_eFileInput;
};

FileField.prototype.chooseDifferentFile = function() {
	this.fileSelected._$setInternalValue(false);
	this.fileInputVisible.setValue(true);
	this.fileInfoVisible.setValue(false);
};

FileField.prototype._setFileName = function() {
	var sFileName = '';

	if (this.m_eFileInput.files && this.m_eFileInput.files.length === 1) {
		sFileName = this.m_eFileInput.files[0].name;
	} else {
		pFileName = this.m_eFileInput.value.split('/');
		sFileName = pFileName[pFileName.length - 1];
	}

	this.fileName.setValue(sFileName);
};

module.exports = FileField;
