'use strict';

var Core = require('br/Core');
var BreadCrumb = require("brjs/dashboard/app/model/crumbtrail/BreadCrumb");

function LinkBreadCrumb(sName, sUrl) {
	// call super constructor
	BreadCrumb.call(this, 'link-breadcrumb', sName, sUrl);
}

Core.extend(LinkBreadCrumb, BreadCrumb);

module.exports = LinkBreadCrumb;
