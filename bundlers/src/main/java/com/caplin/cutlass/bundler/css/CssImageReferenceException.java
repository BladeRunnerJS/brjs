package com.caplin.cutlass.bundler.css;

import com.caplin.cutlass.structure.ScopeLevel;

public class CssImageReferenceException extends RuntimeException
{
	private static final long serialVersionUID = -6241570449544062831L;
	private String referencedImagePath;
	private ScopeLevel imageScopeLevel;
	private String cssFileContainingImageReference;
	
	public CssImageReferenceException()
	{
		super();
	}
	
	public CssImageReferenceException(Exception ex)
	{
		super(ex);
	}
	
	public String getReferencedImagePath() {
		return referencedImagePath;
	}
	
	public void setReferencedImagePath(String referencedImagePath) {
		this.referencedImagePath = referencedImagePath;
	}
	
	public ScopeLevel getImageScopeLevel() {
		return imageScopeLevel;
	}
	
	public void setImageScopeLevel(ScopeLevel imageScopeLevel) {
		this.imageScopeLevel = imageScopeLevel;
	}
	
	public String getCssFileContainingImageReference() {
		return cssFileContainingImageReference;
	}
	
	public void setCssFileContainingImageReference(String cssFileContainingImageReference) {
		this.cssFileContainingImageReference = cssFileContainingImageReference;
	}
	
	@Override
	public String toString()
	{
		return "Error in css file '" + cssFileContainingImageReference + "',\nreferencing image file '" 
					+ referencedImagePath + "'\nwith the scopeLevel " + imageScopeLevel;
	}
	
	
	
}
