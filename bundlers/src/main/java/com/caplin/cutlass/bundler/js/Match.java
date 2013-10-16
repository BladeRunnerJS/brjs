package com.caplin.cutlass.bundler.js;

public class Match {

	public LetterNode node;
	public StringBuffer buffer = new StringBuffer();
	
	private String identifier = "";
	private boolean staticDependency = false;
	private boolean thirdpartyDependency = false;

	public Match( char first, LetterNode root ) {
		buffer.append( first );
		node = root;
	}

	public String getDependencyName() {
		return identifier;
	}

	public void setStaticDependency( boolean staticDependency ) {
		this.staticDependency = staticDependency;
	}

	public boolean isStaticDependency() {
		return staticDependency;
	}

	public void setThirdPartyDependency( boolean thirdPartyDependency ) {
		this.thirdpartyDependency = thirdPartyDependency;
	}

	public boolean isThirdPartyDependency() {
		return thirdpartyDependency;
	}
	
	public boolean isAlias() {
		return node.isAlias();
	}
	
	public boolean processNextCharacter( char latest ) {
		
		LetterNode found = node.find( latest );
		
		if ( found == null ) {
			return false;
		}
		
		this.node = found;
		buffer.append( latest );
		storeValueIfItsAnIdentifier();
		
		return true;
	}
	
	public boolean hasMatchedAnIdentifier() {
		if( identifier.length() > 0 ) {
			return true;
		}
		
		return false;
	}
	
	private boolean isIdentifierEnd() {
		return node.isIdentifierEnd();
	}
	
	private void storeValueIfItsAnIdentifier() {
		if( isIdentifierEnd() ) {
			identifier = buffer.toString();
		}
	}
}