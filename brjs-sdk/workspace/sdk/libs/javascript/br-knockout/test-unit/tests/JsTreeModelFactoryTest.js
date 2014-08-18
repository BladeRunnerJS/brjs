(function() {
	var jsTreeModelFactory = require('br/knockout/workbench/KnockoutJsTreeModelFactory');
	
	KnockoutJsTreeModelFactoryTest = TestCase('KnockoutJsTreeModelFactoryTest');
	
	KnockoutJsTreeModelFactoryTest.prototype.testEmptyViewModel = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: []}], treeModel.core.data);
	};
	
	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithOneObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable('42')});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: [{text: 'foo: 42'}]}], treeModel.core.data);
	};
	
	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithOneNonObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo: 42});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: [{text: 'foo: 42'}]}], treeModel.core.data);
	};
	
	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithTwoObservables = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable('value #1'), bar:ko.observable('value #2')});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: [{text: 'foo: value #1'}, {text: 'bar: value #2'}]}], treeModel.core.data);
	};
	
	KnockoutJsTreeModelFactoryTest.prototype.testViewModelWithAnObjectContainingObservables = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({obj: {foo:ko.observable('value #1'), bar:ko.observable('value #2')}});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: [
			{text: 'obj', state:{opened: true}, children: [
				{text: 'foo: value #1'},
				{text: 'bar: value #2'}
			]}
		]}], treeModel.core.data);
	};
})();
