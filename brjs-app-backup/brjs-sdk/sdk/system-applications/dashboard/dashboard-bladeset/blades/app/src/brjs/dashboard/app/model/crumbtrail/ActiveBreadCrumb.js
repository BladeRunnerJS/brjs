'use strict';

var Core = require('br/Core');
var BreadCrumb = require("brjs/dashboard/app/model/crumbtrail/BreadCrumb");

function ActiveBreadCrumb(sName) {
	// call super constructor
	BreadCrumb.call(this, 'active-breadcrumb', sName, null);
}

Core.extend(ActiveBreadCrumb, BreadCrumb);

module.exports = ActiveBreadCrumb;
