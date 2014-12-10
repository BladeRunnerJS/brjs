/* jshint node: true */
/* global it */

var json   = require('../');
var gulp   = require("gulp");
require('should');
require('mocha');

it('should modify property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    obj.version = '2.0.0';
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "name": "test object",' +
      '  "version": "2.0.0",' +
      '  "nested": {' +
      '    "name": "nested object",' +
      '    "version": "1.0.0"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should add property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    obj.description = 'this is test';
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "name": "test object",' +
      '  "version": "1.0.0",' +
      '  "description": "this is test",' +
      '  "nested": {' +
      '    "name": "nested object",' +
      '    "version": "1.0.0"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should remove property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    delete obj.name;
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "version": "1.0.0",' +
      '  "nested": {' +
      '    "name": "nested object",' +
      '    "version": "1.0.0"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should modify nested property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    obj.nested.version = '2.0.1';
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "name": "test object",' +
      '  "version": "1.0.0",' +
      '  "nested": {' +
      '    "name": "nested object",' +
      '    "version": "2.0.1"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should add nested property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    obj.nested.description = 'this is test for nested';
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "name": "test object",' +
      '  "version": "1.0.0",' +
      '  "nested": {' +
      '    "name": "nested object",' +
      '    "version": "1.0.0",' +
      '    "description": "this is test for nested"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should remove nested property of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    delete obj.nested.name;
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "name": "test object",' +
      '  "version": "1.0.0",' +
      '  "nested": {' +
      '    "version": "1.0.0"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});


it('should multiple properties of JSON object (by function editor)', function(done) {

  var stream = gulp.src('test/test.json').pipe(json(function(obj) {
    obj.version = '2.0.0';
    obj.description = 'this is test';
    delete obj.name;
    obj.nested.version = '2.0.1';
    obj.nested.description = 'this is test for nested';
    delete obj.nested.name;
    return obj;
  }));

  stream.on('data', function(file) {
    var expected = JSON.parse(
      '{' +
      '  "version": "2.0.0",' +
      '  "description": "this is test",' +
      '  "nested": {' +
      '    "version": "2.0.1",' +
      '    "description": "this is test for nested"' +
      '  }' +
      '}'
    );
    JSON.parse(file.contents).should.eql(expected);
    done();
  });
});
