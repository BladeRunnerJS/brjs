package org.bladerunnerjs.plugin.plugins.bundlers.css;

public class CssImageReferenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String referencedResourcePath;
	private String cssFileContainingImageReference;
	
	public CssImageReferenceException() {
		super();
	}
	
	public CssImageReferenceException(Exception ex) {
		super(ex);
	}
	
	public String getReferencedResourcePath() {
		return referencedResourcePath;
	}
	
	public void setReferencedResourcePath(String referencedResourcePath) {
		this.referencedResourcePath = referencedResourcePath;
	}
	
	public String getCssFileContainingImageReference() {
		return cssFileContainingImageReference;
	}
	
	public void setCssFileContainingImageReference(String cssFileContainingImageReference) {
		this.cssFileContainingImageReference = cssFileContainingImageReference;
	}
	
	@Override
	public String toString() {
		return "Error in css file '" + cssFileContainingImageReference + "',\nreferencing image file '" + referencedResourcePath + "'";
	}
}
