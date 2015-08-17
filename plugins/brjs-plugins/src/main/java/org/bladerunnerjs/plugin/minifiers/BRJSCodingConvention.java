package org.bladerunnerjs.plugin.minifiers;

import com.google.javascript.jscomp.CodingConvention;
import com.google.javascript.jscomp.CodingConventions;

public class BRJSCodingConvention extends CodingConventions.Proxy implements CodingConvention {
	private static final long serialVersionUID = 1L;

	protected BRJSCodingConvention() {
		this(CodingConventions.getDefault());
	}

	protected BRJSCodingConvention(CodingConvention convention) {
		super(convention);
	}

	@Override
	public boolean isPrivate(String name) {
		return (name.startsWith("_") || name.startsWith("m_")) && !isExported(name);
	}
}
