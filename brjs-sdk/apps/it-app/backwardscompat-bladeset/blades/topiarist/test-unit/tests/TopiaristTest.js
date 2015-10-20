(function() {
	'use strict';

	require('jsmockito');

	var TopiaristTest = TestCase('TopiaristTest');
	var BaseClass = require('itapp/backwardscompat/topiarist/BaseClass');
	var TestInterface = require('itapp/backwardscompat/topiarist/TestInterface');
	var ImplementingClass = require('itapp/backwardscompat/topiarist/ImplementingClass');
	var topiarist = require('topiarist');

	TopiaristTest.prototype.setUp = function () {

	};

	TopiaristTest.prototype.testExtend = function() {
		var ExtendingClass = function() {
			this.value = 12345;
		};
		topiarist.extend(ExtendingClass, BaseClass);
		ExtendingClass.prototype.methodB = function(multiplier) {
			return ExtendingClass.superclass.prototype.methodB.call(this, multiplier + 1);
		};

		var instance = new ExtendingClass();

		assertTrue(instance instanceof BaseClass);
		assertTrue(topiarist.isA(instance, BaseClass));
		assertTrue(topiarist.classIsA(ExtendingClass, BaseClass));
		assertEquals('Hello Bob', instance.methodA('Bob'));
		assertEquals(24690, instance.methodB(1));
	};

	TopiaristTest.prototype.testExtendAlternative = function() {
		var ExtendingClass = function() {
			this.value = 12345;
		};
		topiarist.extend(ExtendingClass, BaseClass, {
			methodB: function (multiplier) {
				return ExtendingClass.superclass.prototype.methodB.call(this, multiplier + 1);
			}
		});

		var instance = new ExtendingClass();

		assertTrue(instance instanceof BaseClass);
		assertTrue(topiarist.isA(instance, BaseClass));
		assertTrue(topiarist.classIsA(ExtendingClass, BaseClass));
		assertEquals('Hello Bob', instance.methodA('Bob'));
		assertEquals(24690, instance.methodB(1));
	};

	TopiaristTest.prototype.testInherit = function() {
		var InheritingClass = function() {
			this.value = 12345;
		};
		topiarist.inherit(InheritingClass, BaseClass);
		InheritingClass.prototype.methodB = function(multiplier) {
			return BaseClass.prototype.methodB.call(this, multiplier + 1);
		};

		var instance = new InheritingClass();

		assertFalse(instance instanceof BaseClass);
		assertTrue(topiarist.isA(instance, BaseClass));
		assertTrue(topiarist.classIsA(InheritingClass, BaseClass));
		assertEquals('Hello Bob', instance.methodA('Bob'));
		assertEquals(24690, instance.methodB(1));
	};

	TopiaristTest.prototype.testMixin = function() {
		var ClassMixedInto = function() {
			this.value = 12345;
		};
		topiarist.mixin(ClassMixedInto, BaseClass);

		var instance = new ClassMixedInto();

		assertFalse(instance instanceof BaseClass);
		assertTrue(topiarist.isA(instance, BaseClass));
		assertTrue(topiarist.classIsA(ClassMixedInto, BaseClass));
		assertEquals('Hello Bob', instance.methodA('Bob'));
		assertEquals(54321, instance.methodB(1));
	};

	TopiaristTest.prototype.testImplement = function() {

		var instance = new ImplementingClass();

		assertFalse(instance instanceof TestInterface);
		assertTrue(topiarist.isA(instance, TestInterface));
		assertTrue(topiarist.classIsA(ImplementingClass, TestInterface));
		assertTrue(topiarist.fulfills(instance, TestInterface));
		assertTrue(topiarist.classFulfills(ImplementingClass, TestInterface));
	};

	TopiaristTest.prototype.testHasImplemented = function() {

		var ClassThatHasImplement = function() {

		};

		ClassThatHasImplement.prototype.thingOne = function() {};
		ClassThatHasImplement.prototype.thingTwo = function() {};

		topiarist.hasImplemented(ClassThatHasImplement, TestInterface);

		var instance = new ClassThatHasImplement();

		assertFalse(instance instanceof TestInterface);
		assertTrue(topiarist.isA(instance, TestInterface));
		assertTrue(topiarist.classIsA(ClassThatHasImplement, TestInterface));
		assertTrue(topiarist.fulfills(instance, TestInterface));
		assertTrue(topiarist.classFulfills(ClassThatHasImplement, TestInterface));
	};

	TopiaristTest.prototype.testImplicitImplementation = function() {

		var ImplicitlyImplementingClass = function() {

		};

		ImplicitlyImplementingClass.prototype.thingOne = function() {};
		ImplicitlyImplementingClass.prototype.thingTwo = function() {};

		var instance = new ImplicitlyImplementingClass();

		assertFalse(instance instanceof TestInterface);
		assertFalse(topiarist.isA(instance, TestInterface));
		assertFalse(topiarist.classIsA(ImplicitlyImplementingClass, TestInterface));
		assertTrue(topiarist.fulfills(instance, TestInterface));
		assertTrue(topiarist.classFulfills(ImplicitlyImplementingClass, TestInterface));
	};

	TopiaristTest.prototype.testClassThatDoesNotFulfillInterface = function() {

		var ClassThatDoesNotFulfillInterface = function() {

		};

		ClassThatDoesNotFulfillInterface.prototype.thingTwo = function() {};

		var instance = new ClassThatDoesNotFulfillInterface();

		assertFalse(topiarist.fulfills(instance, TestInterface));
		assertFalse(topiarist.classFulfills(ClassThatDoesNotFulfillInterface, TestInterface));
	};

}());