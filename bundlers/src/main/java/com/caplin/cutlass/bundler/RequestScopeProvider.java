package com.caplin.cutlass.bundler;

import java.util.Arrays;
import java.util.List;

import com.caplin.cutlass.bundler.exception.UnknownScopeException;
import com.caplin.cutlass.structure.ScopeLevel;
import com.caplin.cutlass.structure.model.Node;

public class RequestScopeProvider
{
	private static List<ScopeLevel> appAspectScopeLevels = Arrays.asList(new ScopeLevel[]{
			ScopeLevel.SDK_SCOPE, ScopeLevel.LIB_SCOPE, ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, ScopeLevel.BLADESET_SCOPE, ScopeLevel.BLADE_SCOPE, ScopeLevel.ASPECT_SCOPE});
	
	private static List<ScopeLevel> bladesetScopeLevels = Arrays.asList(new ScopeLevel[]{
			ScopeLevel.SDK_SCOPE, ScopeLevel.LIB_SCOPE, ScopeLevel.BLADESET_SCOPE});
	
	private static List<ScopeLevel> bladeScopeLevels = Arrays.asList(new ScopeLevel[]{
			ScopeLevel.SDK_SCOPE, ScopeLevel.LIB_SCOPE, ScopeLevel.BLADESET_SCOPE, ScopeLevel.BLADE_SCOPE});
	
	private static List<ScopeLevel> workbenchScopeLevels = Arrays.asList(new ScopeLevel[]{
			ScopeLevel.SDK_SCOPE, ScopeLevel.LIB_SCOPE, ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, ScopeLevel.BLADESET_SCOPE, ScopeLevel.BLADE_SCOPE, ScopeLevel.ASPECT_SCOPE, ScopeLevel.WORKBENCH_SCOPE});
	
	private static List<ScopeLevel> sdkLevelScopes = Arrays.asList(new ScopeLevel[]{ScopeLevel.SDK_SCOPE, ScopeLevel.LIB_SCOPE});
	
	public static List<ScopeLevel> getScopeLevels(Node requestLevel) throws UnknownScopeException
	{
		switch(requestLevel.getNodeType())
		{
			case ASPECT:
				return appAspectScopeLevels;
			
			case BLADESET:
				return bladesetScopeLevels;
			
			case BLADE:
				return bladeScopeLevels;
			
			case WORKBENCH:
				return workbenchScopeLevels;
			
			case SDK:
				return sdkLevelScopes;

			case LIB:
				return sdkLevelScopes;

			default:
				throw new UnknownScopeException(requestLevel);
		}
	}
	
	public static boolean isValidRequest(Node requestLevel, ScopeLevel resourceScope) throws UnknownScopeException
	{
		List<ScopeLevel> validScopeLevels = getScopeLevels(requestLevel);
		
		return validScopeLevels.contains(resourceScope);
	}
}
