package com.caplin.cutlass.structure;

public enum ScopeLevel
{
	UNKNOWN_SCOPE,
	SDK_SCOPE, // TODO: add support for this within CutlassDirectoryLocator?
	LIB_SCOPE, // TODO: add support for this within CutlassDirectoryLocator?
	THIRDPARTY_LIBRARY_SCOPE,
	APP_SCOPE,
	ASPECT_SCOPE,
	BLADESET_SCOPE,
	BLADE_SCOPE,
	WORKBENCH_SCOPE
}