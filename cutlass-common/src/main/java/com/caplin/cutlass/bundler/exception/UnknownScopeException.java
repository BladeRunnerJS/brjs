package com.caplin.cutlass.bundler.exception;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.structure.model.Node;

public class UnknownScopeException extends ContentProcessingException
{
	private static final long serialVersionUID = -6336566544386277866L;

	public UnknownScopeException(Node requestLevel)
	{
		super(requestLevel.getNodeType() + " is not a valid request level");
	}
}
