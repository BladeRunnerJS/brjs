package org.bladerunnerjs.legacy.command.test.testrunner;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunner.TestType;

public class TestRunResult {

	private boolean success;
	private MemoizedFile baseDir;
	private MemoizedFile testDir;
	private long startTime;
	private long endTime;
	private TestType testType;
	
	public TestRunResult(MemoizedFile baseDir, MemoizedFile testDir, TestType testType) {
		this.baseDir = baseDir;
		this.testDir = testDir;
		this.testType = testType;
		this.success = true;
		this.startTime = -1;
		this.endTime = -1;
	}
	
	public TestType getTestType()
	{
		return testType;
	}
	
	public void setTestType(TestType type) {
		testType = type;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean getSuccess() {
		return success;
	}
	
	public void setBaseDirectory(MemoizedFile baseDir) {
		this.baseDir = baseDir;
	}
	
	public MemoizedFile getBaseDirectory() {
		return baseDir;
	}

	public void setTestDirectory(MemoizedFile testDir) {
		this.testDir = testDir;
	}
	
	public MemoizedFile getTestDirectory() {
		return testDir;
	}
	
	public void setStartTime(long currentTimeMillis) {
		startTime = currentTimeMillis;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setEndTime(long currentTimeMillis) {
		endTime = currentTimeMillis;		
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public long getDurationInSeconds() throws Exception {
		if (startTime < 0) {
			throw new Exception("Start time has not been set.");
		}
		if (endTime < 0) {
			throw new Exception("End time has not been set.");
		}
		long millisDuration = endTime - startTime;
		return millisDuration / 1000;
	}

}
