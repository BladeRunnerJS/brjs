package org.bladerunnerjs.api.model.exception;

public class OutOfScopeRequirePathException extends RequirePathException {

	private static final long serialVersionUID = 1L;
	private String requirePath;
	private String nodeName;
	private String scopedLocations;
	private String assetLocation;
	
	public OutOfScopeRequirePathException(String requirePath, String nodeName, String scopedLocations, String assetLocation) {
		this.requirePath = requirePath;
		this.nodeName = nodeName;
		this.scopedLocations = scopedLocations;
		this.assetLocation = assetLocation;
	}

	@Override
	public String getMessage() {
		return "The asset '" + requirePath + "' was found but it was not in the valid scopes. The scope was:\n" + nodeName
				+ ":\n" + scopedLocations + "\nbut the asset was found in '" + assetLocation + "'.";
	}
	
}
