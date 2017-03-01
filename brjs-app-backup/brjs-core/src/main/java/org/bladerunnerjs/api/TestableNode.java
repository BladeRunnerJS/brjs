package org.bladerunnerjs.api;

import java.util.List;

/**
 * A set of functions for recognising test types (unit, acceptance, integration or performance) and running them within the existing TestNode,
 * which represents a suite of tests.
 */

public interface TestableNode {
	
	/**
	 * The method runs the tests specified by the test type from within the current TestableNode.
	 * 
	 * @param testTypes may be at least one of the following: UT (unit tests), AT (acceptance tests), IT (integration tests) or PT (performance
	 * tests)
	 */
	void runTests(TestType... testTypes);
	
	/**
	 * The method retrieves the available test types from within the current TestableNode.
	 * 
	 * @return a list of {@link TypedTestPack} showing the location and name of the tests available.
	 */
	List<TypedTestPack> testTypes();
	
	/**
	 * The method retrieves the available {@link TypedTestPack} of specified type (unit, acceptance, integration or performance) 
	 * from within the current TestableNode.
	 * 
	 * @param type may be one of the following: UT (unit tests), AT (acceptance tests), IT (integration tests) or PT (performance
	 * tests)
	 * 
	 * @return a {@link TypedTestPack} object showing the location and name of the test pack available.
	 */
	TypedTestPack testType(String type);
}
