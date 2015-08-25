'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var Property = require('br/presenter/property/Property');

function AppSummaryPresentationNode(sAppName, sAppInfoUrl, sImageUrl) {
	this.appName = new Property(sAppName);
	this.appInfoUrl = new Property(sAppInfoUrl);
	this.imageUrl = new Property(sImageUrl);
}

Core.extend(AppSummaryPresentationNode, PresentationNode);

module.exports = AppSummaryPresentationNode;
