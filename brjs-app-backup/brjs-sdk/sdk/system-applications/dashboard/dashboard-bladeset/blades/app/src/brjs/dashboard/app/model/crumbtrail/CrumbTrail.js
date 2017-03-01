'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var NodeList = require('br/presenter/node/NodeList');
var BreadCrumb = require("brjs/dashboard/app/model/crumbtrail/BreadCrumb");
var HomeBreadCrumb = require("brjs/dashboard/app/model/crumbtrail/HomeBreadCrumb");
var LinkBreadCrumb = require("brjs/dashboard/app/model/crumbtrail/LinkBreadCrumb");
var ActiveBreadCrumb = require("brjs/dashboard/app/model/crumbtrail/ActiveBreadCrumb");

function CrumbTrail(oPresentationModel) {
	this.crumbs = new NodeList([], BreadCrumb);
	this.visible = new WritableProperty();

	oPresentationModel.getPageUrlService().addPageUrlListener(this._onPageUrlUpdated.bind(this), true);
}

Core.extend(CrumbTrail, PresentationNode);

CrumbTrail.prototype._onPageUrlUpdated = function(sPageUrl) {
	var pCrumbTrail = [new HomeBreadCrumb()];
	var bIsVisible = false;

	if (sPageUrl.match(/^#apps\/.*workbench$/)) {
		bIsVisible = true;
		var pParts = sPageUrl.match(/^#apps\/(.*)workbench$/)[1].split('/');
		var sApp = pParts[0];
		var sBladeset = pParts[1];
		var sBlade = pParts[2];
		var sAppUrl = '#apps/' + sApp;
		pCrumbTrail.push(new LinkBreadCrumb(sApp, sAppUrl));
		pCrumbTrail.push(new LinkBreadCrumb(sBladeset, sAppUrl));
		pCrumbTrail.push(new LinkBreadCrumb(sBlade, sAppUrl));
		pCrumbTrail.push(new ActiveBreadCrumb('Workbench'));
	} else if (sPageUrl.match(/^#apps\/.*/)) {
		var sApp = sPageUrl.split('/')[1];

		bIsVisible = true;
		pCrumbTrail.push(new ActiveBreadCrumb(sApp));
	}

	this.crumbs.updateList(pCrumbTrail);
	this.visible.setValue(bIsVisible);
};

module.exports = CrumbTrail;
