'use strict';

module.exports = {
	SUBCLASS_NOT_CONSTRUCTOR: 'Subclass was not a constructor.',
	SUPERCLASS_NOT_CONSTRUCTOR: 'Superclass was not a constructor when extending {0}.',
	PROTOTYPE_NOT_CLEAN: 'Prototype must be clean to extend another class. {1} has already been defined on the ' +
		'prototype of {0}.',
	NOT_CONSTRUCTOR: '{0} definition for {1} must be a constructor, was {2}.',
	DOES_NOT_IMPLEMENT: 'Class {0} does not implement the attributes \'{1}\' from protocol {2}.',
	PROPERTY_ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
	NULL: '{0} for {1} must not be null or undefined.',
	ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
	WRONG_TYPE: '{0} for {1} should have been of type {2}, was {3}.',
	TWO_CONSTRUCTORS: 'Two different constructors provided for {0}, use only one of the classDefinition argument ' +
		'and extraProperties.constructor.',
	BAD_INSTALL: 'Can only install to the global environment or a constructor, can\'t install to a {0}.'
};
