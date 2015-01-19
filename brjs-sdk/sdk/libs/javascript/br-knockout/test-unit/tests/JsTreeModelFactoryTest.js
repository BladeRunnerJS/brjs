(function() {
	var jsTreeModelFactory = require('br/knockout/workbench/KnockoutJsTreeModelFactory');
	var ko = require('ko');

	KnockoutJsTreeModelFactoryTest = TestCase('KnockoutJsTreeModelFactoryTest');

	KnockoutJsTreeModelFactoryTest.prototype.testEmptyViewModel = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({});

		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: []}], treeModel.core.data);
	};

	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithOneObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable('42')});

		assertEquals('Knockout View Model', treeModel.core.data[0].text);
		assertEquals('foo: 42', treeModel.core.data[0].children[0].text);
		assertEquals({opened: true}, treeModel.core.data[0].state);
	};

	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithOneNonObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo: 42});

		assertEquals('Knockout View Model', treeModel.core.data[0].text);
		assertEquals('foo: 42', treeModel.core.data[0].children[0].text);
		assertEquals({opened: true}, treeModel.core.data[0].state);
	};

	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithTwoObservables = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable('value #1'), bar:ko.observable('value #2')});

		assertEquals('Knockout View Model', treeModel.core.data[0].text);
		assertEquals('foo: value #1', treeModel.core.data[0].children[0].text);
		assertEquals('bar: value #2', treeModel.core.data[0].children[1].text);
		assertEquals({opened: true}, treeModel.core.data[0].state);
	};

	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithAnObjectContainingObservables = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({obj: {foo:ko.observable('value #1'), bar:ko.observable('value #2')}});

		assertEquals('Knockout View Model', treeModel.core.data[0].text);
		assertEquals('obj', treeModel.core.data[0].children[0].text);
		assertEquals({opened: true}, treeModel.core.data[0].children[0].state);
		assertEquals('foo: value #1', treeModel.core.data[0].children[0].children[0].text);
		assertEquals('bar: value #2', treeModel.core.data[0].children[0].children[1].text);
	};
})();
