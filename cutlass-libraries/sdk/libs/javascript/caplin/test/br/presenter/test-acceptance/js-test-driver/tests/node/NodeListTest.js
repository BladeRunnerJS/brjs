br.test.GwtTestRunner.initialize();

describe("View to model interactions for NodeList", function() {
	fixtures("NodeListFixtureFactory");
	
	it("contains a single node by default", function() {
		given("component.viewOpened = true");
		then("component.view.(.nodeListItem).count = 1");
			and("component.view.(.nodeListItem).text = 'node #1'");
	});
	
	it("allows the initially displayed node to be removed", function() {
		given("component.viewOpened = true");
		when("component.model.showZeroNodes.invoked => true");
		then("component.view.(.nodeListItem).count = 0");
	});
	
	it("allows multiple nodes to be added", function() {
		given("component.viewOpened = true");
		when("component.model.showTwoNodes.invoked => true");
		then("component.view.(.nodeListItem).count = 2");
			and("component.view.(.nodeListItem:first).text = 'node #1'");
			and("component.view.(.nodeListItem:last).text = 'node #2'");
	});
	
	it("allows single removed node to be added back in again", function() {
		given("test.continuesFrom = 'allows the initially displayed node to be removed'");
		when("component.model.showOneNode.invoked => true");
		then("component.view.(.nodeListItem).count = 1");
			and("component.view.(.nodeListItem).text = 'node #1'");
	});
	
	it("allows multiple displayed nodes to be removed", function() {
		given("test.continuesFrom = 'allows multiple nodes to be added'");
		when("component.model.showZeroNodes.invoked => true");
		then("component.view.(.nodeListItem).count = 0");
	});	
	
	it("allows multiple removed nodes to be added again", function() {
		given("test.continuesFrom = 'allows multiple displayed nodes to be removed'");
		when("component.model.showTwoNodes.invoked => true");
		then("component.view.(.nodeListItem).count = 2");
			and("component.view.(.nodeListItem:first).text = 'node #1'");
			and("component.view.(.nodeListItem:last).text = 'node #2'");
	});
	
	it("allows multiple removed nodes to be added again in reverse order", function() {
		given("test.continuesFrom = 'allows multiple displayed nodes to be removed'");
		when("component.model.showTwoNodesReverseOrder.invoked => true");
		then("component.view.(.nodeListItem).count = 2");
			and("component.view.(.nodeListItem:first).text = 'node #2'");
			and("component.view.(.nodeListItem:last).text = 'node #1'");
	});
	
	it("displays the first node with the standard template", function() {
		given("templateAwareComponent.viewOpened = true");
		then("templateAwareComponent.view.(#single-node-list .nodeListItem).count = 1");
			and("templateAwareComponent.view.(#single-node-list .alternateNodeListItem).count = 0");
			and("templateAwareComponent.view.(#single-node-list .nodeListItem).text = 'node #1'");
	});
	
	it("displays other nodes with the alternate template", function() {
		given("templateAwareComponent.viewOpened = true");
		when("templateAwareComponent.model.showTwoNodes.invoked => true");
		then("templateAwareComponent.view.(#single-node-list .nodeListItem).count = 1");
			and("templateAwareComponent.view.(#single-node-list .alternateNodeListItem).count = 1");
			and("templateAwareComponent.view.(#single-node-list .nodeListItem).text = 'node #1'");
			and("templateAwareComponent.view.(#single-node-list .alternateNodeListItem).text = 'node #2'");
	});

	it("displays two independent template-aware node lists with the correct templates", function() {
		given("templateAwareComponent.viewOpened = true");
		when("templateAwareComponent.model.showTwoNodes.invoked => true");
			and("templateAwareComponent.model.showAnotherNodeList.invoked => true");
		then("templateAwareComponent.view.(#two-node-lists .nodeListItem).count = 2");
			and("templateAwareComponent.view.(#two-node-lists .alternateNodeListItem).count = 2");
			and("templateAwareComponent.view.(#two-node-lists .nodeListItem:first).text = 'node #1'");
			and("templateAwareComponent.view.(#two-node-lists .nodeListItem:last).text = 'node #3'");
			and("templateAwareComponent.view.(#two-node-lists .alternateNodeListItem:first).text = 'node #2'");
			and("templateAwareComponent.view.(#two-node-lists .alternateNodeListItem:last).text = 'node #4'");
	});

	it("binds the same node list twice correctly", function() {
		given("test.continuesFrom = 'displays two independent template-aware node lists with the correct templates'");
		then("templateAwareComponent.view.(#single-node-list .nodeListItem).count = 1");
			and("templateAwareComponent.view.(#single-node-list .alternateNodeListItem).count = 1");
			and("templateAwareComponent.view.(#single-node-list .nodeListItem).text = 'node #1'");
			and("templateAwareComponent.view.(#single-node-list .alternateNodeListItem).text = 'node #2'");
	});
});
