package org.bladerunnerjs.spec.node.utility.filechange;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.SpecTestDirObserver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class WatchingDirectoryObserverTest extends SpecTest
{

	private App app;
	private SpecTestDirObserver observer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app");
			observer = new SpecTestDirObserver();
	}
	
	@Test
	public void observerReturnsTrueOnFirstCheck() throws Exception {
		given(app).hasBeenCreated()
			.and(observer).isObservingDir(app.dir(), brjs);
		then(observer).detectsChanges();
	}
	
	@Test
	public void observerReturnsTrueIfFilesHaveBeenAdded() throws Exception {
		given(app).hasBeenCreated()
			.and(observer).isObservingDir(app.dir(), brjs)
			.and(observer).hasDetectedChanges();
		when(app).fileCreated("someFile.txt");
		then(observer).willEventuallyDetectChanges();
	}
	
	@Test
	public void observerReturnsTrueIfFilesHaveBeenDeleted() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFile("someFile.txt")
			.and(observer).isObservingDir(app.dir(), brjs)
			.and(observer).hasDetectedChanges();
		when(app).fileDeleted("someFile.txt");
		then(observer).willEventuallyDetectChanges();
	}
	
	@Test
	public void observerReturnsTrueIfFilesHaveChanged() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("someFile.txt", "initial file contents")
			.and(observer).isObservingDir(app.dir(), brjs)
			.and(observer).hasDetectedChanges();
		when(app).fileContentsChangeTo("someFile.txt", "some new file contents");
		then(observer).willEventuallyDetectChanges();
	}
	
	@Ignore
	@Test
	public void observerIgnoresDotFiles() throws Exception {
		given(app).hasBeenCreated()
			.and(observer).isObservingDir(app.dir(), brjs)
			.and(observer).hasDetectedChanges();
		when(app).fileCreated(".someFile");
		then(observer).doesntDetectChanges();
	}
	
}
