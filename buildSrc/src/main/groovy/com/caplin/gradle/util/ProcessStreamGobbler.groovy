
package com.caplin.gradle.util

public class ProcessStreamGobbler extends Thread
{
	
	InputStream theInputStream = null
	boolean printOut = true

	public ProcessStreamGobbler(InputStream theInputStream, boolean printOut)
	{
		this.theInputStream = theInputStream
		this.printOut = printOut
	}

	/* reads everything from theInputStream until empty. */ 
	public void run()
	{
		try
		{
			InputStreamReader inputStreamReader = new InputStreamReader(theInputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line=null;
			while ( (line = reader.readLine()) != null)
			{
				if (printOut)
				{
					println line;
				}
			}
		} catch (IOException ioe)
		{
		}
	}
}