'use strict';

var appMetaService = require( 'service!br.app-meta-service' );

function AppComponent() {
  this.hello_world = 'Sucessfully loaded the application';
  //'unbundled-resources' can be used for any assets you don't want part of the BRJS bundling process.
  this.unbundledResources = appMetaService.getVersionedBundlePath() + "/unbundled-resources"
  this.brLogo = this.unbundledResources + "/br-logo.png"
}

AppComponent.annotations = [
 new ng.core.Component({
	 selector: 'app' // adjust as needed
 }),
 new ng.core.View({
	 template: require('service!br.html-service').getTemplateElement( '@appns.view-template' ).outerHTML
 }),

];

AppComponent.getHello = function() {
	return 'hello world!';
};

AppComponent.logHello = function() {
	console.log(AppComponent.getHello());
};

module.exports = AppComponent;
