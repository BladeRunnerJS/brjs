/*global describe, expect, it */
describe('jsdoc/src/syntax', function() {
    var Syntax = require('jsdoc/src/syntax').Syntax;

    it('should exist', function() {
        expect(Syntax).toBeDefined();
        expect(typeof Syntax).toBe('object');
    });

    it('should define all of the node types that are defined by Esprima', function() {
        var esprimaSyntax = require('esprima').Syntax;

        Object.keys(esprimaSyntax).forEach(function(nodeType) {
            expect(Syntax[nodeType]).toBeDefined();
            expect(Syntax[nodeType]).toBe(esprimaSyntax[nodeType]);
        });
    });

    it('should define the LetStatement node type', function() {
        expect(Syntax.LetStatement).toBeDefined();
        expect(Syntax.LetStatement).toBe('LetStatement');
    });
});
