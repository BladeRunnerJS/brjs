'use strict';

var PresentationModel = require('br/presenter/PresentationModel');
var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');

/**
 * @module br/presenter/workbench/model/TreeViewerPM
 */


/**
 * @private
 * @class
 * @alias module:br/presenter/workbench/model/TreeViewerPM
 */
function TreeViewerPM(oSearchTarget) {
	this.searchText = new EditableProperty();
	this.m_oSearchTarget = oSearchTarget;
}

Core.extend(TreeViewerPM, PresentationModel);

TreeViewerPM.prototype.close = function() {
	this.m_oSearchTarget.close();
};

TreeViewerPM.prototype.search = function() {
	var vValue = this.searchText.getValue();
	this.m_oSearchTarget.search(vValue);
};

module.exports = TreeViewerPM;
