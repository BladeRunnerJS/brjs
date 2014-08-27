'use strict';

require( 'jasmine' );

var oldConsoleLog = console.log;

var @bladeTitleViewModel = require( '@bladeRequirePrefix/@bladeTitleViewModel' );

describe('@bladeTitle Tests', function() {

  beforeEach(function() {
	console.log = jasmine.createSpy();
  });

  afterEach(function() {
	console.log = oldConsoleLog;
  } );

  it( 'Should log hello on load', function() {
	new @bladeTitleViewModel();
	expect(console.log).toHaveBeenCalledWith('Welcome to your new Blade.');
  });

});
