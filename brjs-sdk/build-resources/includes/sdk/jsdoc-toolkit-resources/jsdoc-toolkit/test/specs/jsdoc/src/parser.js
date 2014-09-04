/*global beforeEach, describe, expect, it, jasmine, spyOn, xit */
describe("jsdoc/src/parser", function() {
    var jsdoc = { src: { parser: require('jsdoc/src/parser') } };

    it("should exist", function() {
        expect(jsdoc.src.parser).toBeDefined();
        expect(typeof jsdoc.src.parser).toBe('object');
    });

    it("should export a 'Parser' constructor", function() {
        expect(jsdoc.src.parser.Parser).toBeDefined();
        expect(typeof jsdoc.src.parser.Parser).toBe('function');
    });

    describe("Parser", function() {
        var parser;

        function newParser() {
            parser = new jsdoc.src.parser.Parser();
        }

        newParser();

        it('should have an "astBuilder" property', function() {
            expect(parser.astBuilder).toBeDefined();
        });

        it('should have a "visitor" property', function() {
            expect(parser.visitor).toBeDefined();
        });

        it('should have a "walker" property', function() {
            expect(parser.walker).toBeDefined();
        });

        it('should accept an astBuilder, visitor, and walker as arguments', function() {
            var astBuilder = {};
            var visitor = {};
            var walker = {};

            var myParser = new jsdoc.src.parser.Parser(astBuilder, visitor, walker);

            expect(myParser.astBuilder).toBe(astBuilder);
            expect(myParser.visitor).toBe(visitor);
            expect(myParser.walker).toBe(walker);
        });

        it('should have a "parse" method', function() {
            expect(parser.parse).toBeDefined();
            expect(typeof parser.parse).toBe('function');
        });

        it('should have a "results" method', function() {
            expect(parser.results).toBeDefined();
            expect(typeof parser.results).toBe('function');
        });

        it('should have an "addAstNodeVisitor" method', function() {
            expect(parser.addAstNodeVisitor).toBeDefined();
            expect(typeof parser.addAstNodeVisitor).toBe('function');
        });

        it('should have a "getAstNodeVisitors" method', function() {
            expect(parser.getAstNodeVisitors).toBeDefined();
            expect(typeof parser.getAstNodeVisitors).toBe('function');
        });

        describe('astBuilder', function() {
            // TODO: enable
            xit('should contain an appropriate astBuilder by default', function() {
                expect( parser.astBuilder instanceof (require('jsdoc/src/astbuilder')) ).toBe(true);
            });
        });

        describe('visitor', function() {
            it('should contain an appropriate visitor by default', function() {
                expect( parser.visitor instanceof (require('jsdoc/src/visitor')).Visitor ).toBe(true);
            });
        });

        describe('walker', function() {
            it('should contain an appropriate walker by default', function() {
                expect( parser.walker instanceof (require('jsdoc/src/walker')).Walker ).toBe(true);
            });
        });

        describe("parse", function() {
            beforeEach(newParser);

            it("should fire 'parseBegin' events before it parses any files", function() {
                var spy = jasmine.createSpy(),
                    sourceFiles = ["javascript:/** @name foo */"];

                parser.on("parseBegin", spy).parse(sourceFiles);
                expect(spy).toHaveBeenCalled();
                expect(spy.mostRecentCall.args[0].sourcefiles).toBe(sourceFiles);
            });

            it("should allow 'parseBegin' handlers to modify the list of source files", function() {
                var sourceCode = "javascript:/** @name foo */",
                    newFiles = ["[[replaced]]"],
                    evt;

                function handler(e) {
                    e.sourcefiles = newFiles;
                    evt = e;
                }

                parser.on('parseBegin', handler).parse(sourceCode);
                expect(evt.sourcefiles).toBe(newFiles);
            });

            it("should fire 'jsdocCommentFound' events when parsing source containing jsdoc comments", function() {
                var spy = jasmine.createSpy(),
                    sourceCode = ['javascript:/** @name bar */'];
                parser.on('jsdocCommentFound', spy).parse(sourceCode);
                expect(spy).toHaveBeenCalled();
                expect(spy.mostRecentCall.args[0].comment).toEqual("/** @name bar */");
            });

            it("should fire 'symbolFound' events when parsing source containing named symbols", function() {
                var spy = jasmine.createSpy(),
                    sourceCode = 'javascript:var foo = 1';
                parser.on('symbolFound', spy).parse(sourceCode);
                expect(spy).toHaveBeenCalled();
            });

            it('should fire "newDoclet" events after creating a new doclet', function() {
                var spy = jasmine.createSpy();
                var sourceCode = 'javascript:var foo = 1';
                parser.on('symbolFound', spy).parse(sourceCode);
                expect(spy).toHaveBeenCalled();
            });

            it('should allow "newDoclet" handlers to modify doclets', function() {
                var results;

                var sourceCode = 'javascript:/** @class */function Foo() {}';

                function handler(e) {
                    var doop = require('jsdoc/util/doop');
                    e.doclet = doop(e.doclet);
                    e.doclet.foo = 'bar';
                }

                require('jsdoc/src/handlers').attachTo(parser);
                parser.on('newDoclet', handler).parse(sourceCode);
                results = parser.results();

                expect(results[0].foo).toBe('bar');
            });

            it('should call AST node visitors', function() {
                var Syntax = require('jsdoc/src/syntax').Syntax;

                var args;

                var sourceCode = ['javascript:/** foo */var foo;'];
                var visitor = {
                    visitNode: function(node, e, parser, sourceName) {
                        if (e && e.code && !args) {
                            args = Array.prototype.slice.call(arguments);
                        }
                    }
                };

                require('jsdoc/src/handlers').attachTo(parser);
                parser.addAstNodeVisitor(visitor);
                parser.parse(sourceCode);

                expect(args).toBeDefined();
                expect( Array.isArray(args) ).toBe(true);
                expect(args.length).toBe(4);

                // args[0]: AST node
                expect(args[0].type).toBeDefined();
                expect(args[0].type).toBe(Syntax.VariableDeclarator);

                // args[1]: JSDoc event
                expect(typeof args[1]).toBe('object');
                expect(args[1].code).toBeDefined();
                expect(args[1].code.name).toBeDefined();
                expect(args[1].code.name).toBe('foo');

                // args[2]: parser
                expect(typeof args[2]).toBe('object');
                expect(args[2] instanceof jsdoc.src.parser.Parser).toBe(true);

                // args[3]: current source name
                expect( String(args[3]) ).toBe('[[string0]]');
            });

            it('should reflect changes made by AST node visitors', function() {
                var doclet;

                var sourceCode = ['javascript:/** foo */var foo;'];
                var visitor = {
                    visitNode: function(node, e, parser, sourceName) {
                        if (e && e.code && e.code.name === 'foo') {
                            e.code.name = 'bar';
                        }
                    }
                };

                require('jsdoc/src/handlers').attachTo(parser);
                parser.addAstNodeVisitor(visitor);
                parser.parse(sourceCode);

                doclet = parser.results()[0];

                expect(doclet).toBeDefined();
                expect(typeof doclet).toBe('object');
                expect(doclet.name).toBeDefined();
                expect(doclet.name).toBe('bar');
            });

            it("should fire 'parseComplete' events after it finishes parsing files", function() {
                var eventObject;

                var spy = jasmine.createSpy(),
                    sourceCode = ['javascript:/** @class */function Foo() {}'];

                require('jsdoc/src/handlers').attachTo(parser);
                parser.on('parseComplete', spy).parse(sourceCode);

                expect(spy).toHaveBeenCalled();

                eventObject = spy.mostRecentCall.args[0];
                expect(eventObject).toBeDefined();
                expect( Array.isArray(eventObject.sourcefiles) ).toBe(true);
                expect(eventObject.sourcefiles.length).toBe(1);
                expect(eventObject.sourcefiles[0]).toBe('[[string0]]');
                expect( Array.isArray(eventObject.doclets) ).toBe(true);
                expect(eventObject.doclets.length).toBe(1);
                expect(eventObject.doclets[0].kind).toBe('class');
                expect(eventObject.doclets[0].longname).toBe('Foo');
            });

            it("should fire processingComplete when fireProcessingComplete is called", function() {
                var spy = jasmine.createSpy(),
                    doclets = ['a','b'];
                parser.on('processingComplete', spy).fireProcessingComplete(doclets);
                expect(spy).toHaveBeenCalled();
                expect(typeof spy.mostRecentCall.args[0]).toBe('object');
                expect(spy.mostRecentCall.args[0].doclets).toBeDefined();
                expect(spy.mostRecentCall.args[0].doclets).toBe(doclets);
            });

            // Rhino can't parse ES6
            if (jasmine.jsParser !== 'rhino') {
                it("should not throw errors when parsing files with ES6 syntax", function() {
                    function parse() {
                        var fs = require('jsdoc/fs');
                        var path = require('jsdoc/path');

                        var parserSrc = 'javascript:' + fs.readFileSync(
                            path.join(global.env.dirname, 'test/fixtures/es6.js'), 'utf8' );
                        parser.parse(parserSrc);
                    }

                    expect(parse).not.toThrow();
                });
            }

            it("should be able to parse its own source file", function() {
                var fs = require('jsdoc/fs'),
                    path = require('path'),
                    parserSrc = 'javascript:' + fs.readFileSync( path.join(global.env.dirname,
                        'lib/jsdoc/src/parser.js'), 'utf8' ),
                    parse = function() {
                        parser.parse(parserSrc);
                    };

                expect(parse).not.toThrow();
            });

            it("should comment out a POSIX hashbang at the start of the file", function() {
                function parse() {
                    parser.parse(parserSrc);
                }

                var parserSrc = 'javascript:#!/usr/bin/env node\n/** class */function Foo() {}';

                expect(parse).not.toThrow();
            });
        });

        describe('results', function() {
            beforeEach(newParser);

            it('returns an empty array before files are parsed', function() {
                var results = parser.results();

                expect(results).toBeDefined();
                expect( Array.isArray(results) ).toBe(true);
                expect(results.length).toBe(0);
            });

            it('returns an array of doclets after files are parsed', function() {
                var source = 'javascript:var foo;';
                var results;

                require('jsdoc/src/handlers').attachTo(parser);

                parser.parse(source);
                results = parser.results();

                expect(results).toBeDefined();
                expect(results[0]).toBeDefined();
                expect(typeof results[0]).toBe('object');
                expect(results[0].name).toBeDefined();
                expect(results[0].name).toBe('foo');
            });

            it("should reflect comment changes made by 'jsdocCommentFound' handlers", function() {
                // we test both POSIX and Windows line endings
                var source = "javascript:/**\n * replaceme\r\n * @module foo\n */\n\n" +
                    "/**\n * replaceme\n */\nvar bar;";

                parser.on('jsdocCommentFound', function(e) {
                    e.comment = e.comment.replace('replaceme', 'REPLACED!');
                });
                require("jsdoc/src/handlers").attachTo(parser);

                parser.parse(source);
                parser.results().forEach(function(doclet) {
                    expect(doclet.comment).not.toMatch('replaceme');
                    expect(doclet.comment).toMatch('REPLACED!');
                });
            });
        });

        describe('addAstNodeVisitor', function() {
            function visitorA() {}
            function visitorB() {}

            var visitors;

            beforeEach(newParser);

            it('should work with a single node visitor', function() {
                parser.addAstNodeVisitor(visitorA);

                visitors = parser.getAstNodeVisitors();

                expect(visitors.length).toBe(1);
                expect(visitors[0]).toBe(visitorA);
            });

            it('should work with multiple node visitors', function() {
                parser.addAstNodeVisitor(visitorA);
                parser.addAstNodeVisitor(visitorB);

                visitors = parser.getAstNodeVisitors();

                expect(visitors.length).toBe(2);
                expect(visitors[0]).toBe(visitorA);
                expect(visitors[1]).toBe(visitorB);
            });
        });

        describe('getAstNodeVisitors', function() {
            beforeEach(newParser);

            it('should return an empty array by default', function() {
                var visitors = parser.getAstNodeVisitors();

                expect( Array.isArray(visitors) ).toBe(true);
                expect(visitors.length).toBe(0);
            });

            // other functionality is covered by the addNodeVisitors tests
        });
    });
});
