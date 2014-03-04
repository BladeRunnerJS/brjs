package org.bladerunnerjs.testing.specutility.engine;

import java.util.List;


public class ExceptionsBuilder
{

	BuilderChainer builderChainer;
	List<Throwable> exceptions;
	SpecTest specTest;
	
	public ExceptionsBuilder(SpecTest specTest, List<Throwable> exceptions)
	{
		this.specTest = specTest;
		this.exceptions = exceptions;
		builderChainer = new BuilderChainer(specTest);
	}

	public void arentCaught()
	{
		specTest.catchAndVerifyExceptions = false;
	}
	
}
