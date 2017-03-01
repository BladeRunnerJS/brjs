'use strict';

var Core = require('br/Core');
var BreadCrumb = require("brjs/dashboard/app/model/crumbtrail/BreadCrumb");

function HomeBreadCrumb() {
	// call super constructor
	BreadCrumb.call(this, 'home-breadcrumb', null, '#');
}

Core.extend(HomeBreadCrumb, BreadCrumb);

module.exports = HomeBreadCrumb;
