package org.bladerunnerjs.utility;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bladerunnerjs.api.SourceModule;
import org.junit.Ignore;
import org.junit.Test;

public class NonCircularTransitivePreExportDependencyGraphCreatorTest {
	private FakeSourceModule a = new FakeSourceModule("a");
	private FakeSourceModule b = new FakeSourceModule("b");
	private FakeSourceModule c = new FakeSourceModule("c");
	private FakeSourceModule d = new FakeSourceModule("d");
	
	private DependencyGraph dependencyGraph(SourceModule... sourceModuleArray) throws Exception {
		Set<SourceModule> sourceModules = new HashSet<>(Arrays.asList(sourceModuleArray));
		return new DependencyGraph(NonCircularTransitivePreExportDependencyGraphCreator.createGraph(
			DefineTimeDependencyGraphCreator.createGraph(null, sourceModules, true), DefineTimeDependencyGraphCreator.createGraph(null, sourceModules, false)));
		
	}
	
	@Test
	public void scenario1() throws Exception {
		a.preDependsOn(b);
		b.dependsOn(a);
		
		DependencyGraph graph = dependencyGraph(a, b);
		
		assertEquals("b", graph.dependenciesOf(a));
		assertEquals("", graph.dependenciesOf(b));
	}
	
	@Test
	public void scenario2() throws Exception {
		a.dependsOn(b);
		b.preDependsOn(c);
		c.dependsOn(b);
		
		DependencyGraph graph = dependencyGraph(a, b, c);
		
		assertEquals("b", graph.dependenciesOf(a));
		assertEquals("c", graph.dependenciesOf(b));
		assertEquals("", graph.dependenciesOf(c));
	}
	
	@Test
	public void scenario3() throws Exception {
		a.dependsOn(b);
		b.dependsOn(c);
		c.preDependsOn(d);
		d.dependsOn(c);
		
		DependencyGraph graph = dependencyGraph(a, b, c, d);
		
		assertEquals("b", graph.dependenciesOf(a));
		assertEquals("c", graph.dependenciesOf(b));
		assertEquals("d", graph.dependenciesOf(c));
		assertEquals("", graph.dependenciesOf(d));
	}
	
	@Ignore // The NonCircularTransitivePreExportDependencyGraphCreator is known not to solve scenario 4
	@Test
	public void scenario4() throws Exception {
		a.dependsOn(b);
		b.dependsOn(c);
		c.preDependsOn(d);
		d.dependsOn(c);
		d.dependsOn(a);
		
		DependencyGraph graph = dependencyGraph(a, b, c, d);
		
		assertEquals("b", graph.dependenciesOf(a));
		assertEquals("c", graph.dependenciesOf(b));
		assertEquals("d", graph.dependenciesOf(c));
		assertEquals("", graph.dependenciesOf(d));
	}
	
	@Test
	public void scenario5() throws Exception {
		a.preDependsOn(b);
		b.dependsOn(c);
		c.dependsOn(a);
		
		b.dependsOn(d);
		d.preDependsOn(b);
		c.dependsOn(d);
		
		DependencyGraph graph = dependencyGraph(a, b, c, d);
		
		assertEquals("b", graph.dependenciesOf(a));
		assertEquals("", graph.dependenciesOf(b));
		assertEquals("", graph.dependenciesOf(c));
		assertEquals("b", graph.dependenciesOf(d));
	}
}
