(function() {
	var jsTreeModelFactory = require('br/knockout/workbench/JsTreeModelFactory');
	var PresentationModelViewer = require('br/knockout/workbench/PresentationModelViewer');
	
	PresentationModelViewerTest = TestCase('PresentationModelViewerTest');
	
	PresentationModelViewerTest.prototype.setUp = function() {
		var treeModel = jsTreeModelFactory.createTreeModelFromKnockoutViewModel({
			prop1: ko.observable('value #1'),
			obj1: {
				prop2: ko.observable('value #2')
			},
			obj2: {
				prop3: ko.observable('value #3'),
				prop4: ko.observable('value #4')
			}
		});
		this.presentationModelViewer = new PresentationModelViewer(treeModel);
	};
	
	PresentationModelViewerTest.prototype.rootNodeExpanded = function() {
		return this.presentationModelViewer._treeModel.core.data[0].state.opened;
	};
	
	PresentationModelViewerTest.prototype.obj1NodeExpanded = function() {
		return this.presentationModelViewer._treeModel.core.data[0].children[1].state.opened;
	};
	
	PresentationModelViewerTest.prototype.obj2NodeExpanded = function() {
		return this.presentationModelViewer._treeModel.core.data[0].children[2].state.opened;
	};
	
	PresentationModelViewerTest.prototype.testThatAllNodesAreExpandedByDefault = function() {
		assertTrue(this.rootNodeExpanded());
		assertTrue(this.obj1NodeExpanded());
		assertTrue(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatSearchingForSomethingThatDoesntExistCausesAllNodesToBeClosed = function() {
		this.presentationModelViewer.applySearch("bad search term");
		
		assertFalse(this.rootNodeExpanded());
		assertFalse(this.obj1NodeExpanded());
		assertFalse(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatASearchThatMatchesARootPropertyDoesntExpandTheChildNodes = function() {
		this.presentationModelViewer.applySearch("value #1");
		
		assertTrue(this.rootNodeExpanded());
		assertFalse(this.obj1NodeExpanded());
		assertFalse(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatASearchThatMatchesAChildPropertyDoesntExpandAdjacentChildNodes = function() {
		this.presentationModelViewer.applySearch("value #2");
		
		assertTrue(this.rootNodeExpanded());
		assertTrue(this.obj1NodeExpanded());
		assertFalse(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatASearchThatMatchesOneOfANumberOfChildPropertyDoesntExpandAdjacentChildNodes = function() {
		this.presentationModelViewer.applySearch("value #4");
		
		assertTrue(this.rootNodeExpanded());
		assertFalse(this.obj1NodeExpanded());
		assertTrue(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatASearchThatMatchesAllPropertiesCausesAllNodesToBeExpanded = function() {
		this.presentationModelViewer.applySearch("value #");
		
		assertTrue(this.rootNodeExpanded());
		assertTrue(this.obj1NodeExpanded());
		assertTrue(this.obj2NodeExpanded());
	};
	
	PresentationModelViewerTest.prototype.testThatASearchIsCaseInsensitive = function() {
		this.presentationModelViewer.applySearch("VALUE #1");
		
		assertTrue(this.rootNodeExpanded());
		assertFalse(this.obj1NodeExpanded());
		assertFalse(this.obj2NodeExpanded());
	};
})();
