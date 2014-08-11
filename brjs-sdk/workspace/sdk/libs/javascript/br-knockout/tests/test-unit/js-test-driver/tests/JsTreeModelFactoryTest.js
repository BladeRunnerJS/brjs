(function() {
	var jsTreeModelFactory = require('br/knockout/workbench/JsTreeModelFactory');
	
	JsTreeModelFactoryTest = TestCase('JsTreeModelFactoryTest');
	
	JsTreeModelFactoryTest.prototype.setUp = function() {
		
	};
	
	JsTreeModelFactoryTest.prototype.testEmptyViewModel = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({});
		
		assertEquals([], treeModel.core.data);
	};
	
	JsTreeModelFactoryTest.prototype.testViewModelWithOneObservable = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({foo:ko.observable()});
		
		assertEquals([{text:'foo'}], treeModel.core.data);
	};
})();
