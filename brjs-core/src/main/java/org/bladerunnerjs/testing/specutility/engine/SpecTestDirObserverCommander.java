package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;

import org.bladerunnerjs.testing.utility.SpecTestDirObserver;


public class SpecTestDirObserverCommander
{
	
	public static final int POLL_INTERVAL = 1000;
	public static final int DEFAULT_WAITFOR_TIMEOUT = 10 * 1000;

	private SpecTestDirObserver observer;

	public SpecTestDirObserverCommander(SpecTest specTest, SpecTestDirObserver observer)
	{
		this.observer = observer;
	}

	public void detectsChanges()
	{
		assertTrue("no changes detected since last check", observer.getDirObserver().hasChangedSinceLastCheck());
	}

	public void willEventuallyDetectChanges()
	{
		willEventuallyDetectChanges(DEFAULT_WAITFOR_TIMEOUT);
	}
	
	public void willEventuallyDetectChanges(int timeout)
	{
		waitForEventOrNoEvent(timeout, true);
	}

	public void doesntDetectChanges()
	{
		doesntDetectChanges(DEFAULT_WAITFOR_TIMEOUT);
	}
	
	public void doesntDetectChanges(int timeout)
	{
		waitForEventOrNoEvent(timeout, false);
	}

	
	
	private void waitForEventOrNoEvent(int timeout, boolean eventExpected)
	{
		int curTime = 0;
		while (curTime <= timeout)
		{
			boolean changesDetected = observer.getDirObserver().hasChangedSinceLastCheck();
			if (changesDetected)
			{
				if (eventExpected)
				{
					return;
				}
				else 
				{
					fail("Observer detected changes that weren't expected");
				}
			}
			
			curTime += POLL_INTERVAL;
			try 
			{
				Thread.sleep(POLL_INTERVAL);
			}
			catch (InterruptedException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		
		if (eventExpected)
		{
			fail("Observer didn't detected changes");
		}
		else 
		{
			return;
		}
	}
	
}
