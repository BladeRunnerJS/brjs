package org.bladerunnerjs.model;

import java.util.List;

public interface TestableNode {
	void runTests(TestType... testTypes);
	List<TypedTestPack> testTypes();
	TypedTestPack testType(String type);
}
