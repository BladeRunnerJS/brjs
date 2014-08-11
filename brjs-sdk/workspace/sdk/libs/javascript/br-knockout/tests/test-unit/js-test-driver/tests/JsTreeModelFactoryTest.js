(function() {
	var jsTreeModelFactory = require('br/knockout/workbench/JsTreeModelFactory');
	
	JsTreeModelFactoryTest = TestCase('JsTreeModelFactoryTest');
	
	JsTreeModelFactoryTest.prototype.setUp = function() {
		
	};
	
	JsTreeModelFactoryTest.prototype.testEmptyViewModel = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: []}], treeModel.core.data);
	};
	
	JsTreeModelFactoryTest.prototype.testViewModelWithOneObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable('42')});
		
		assertEquals([{text: 'Knockout View Model', state:{opened: true}, children: [{text: 'foo: 42'}]}], treeModel.core.data);
	};
})();
