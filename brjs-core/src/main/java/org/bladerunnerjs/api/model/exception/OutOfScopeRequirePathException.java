package org.bladerunnerjs.api.model.exception;

public class OutOfScopeRequirePathException extends RequirePathException {

	private static final long serialVersionUID = 1L;
	private String requirePath;
	private String nodeName;
	private String scopedLocations;
	private String assetPath;
	
	public OutOfScopeRequirePathException(String requirePath, String nodeName, String scopedLocations, String assetPath) {
		this.requirePath = requirePath;
		this.nodeName = nodeName;
		this.scopedLocations = scopedLocations;
		this.assetPath = assetPath;
	}

	@Override
	public String getMessage() {
		return String.format("The asset with the require path '%s' was found at '%s', but in it was not in one of the valid scopes."+
				" The scope was '%s' and the valid locations for assets in this scope were '%s'", requirePath, assetPath, nodeName, scopedLocations);
	}
	
}