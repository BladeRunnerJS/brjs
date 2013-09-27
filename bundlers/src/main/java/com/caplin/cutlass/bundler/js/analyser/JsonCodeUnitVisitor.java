package com.caplin.cutlass.bundler.js.analyser;

import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnit;
import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor;

public class JsonCodeUnitVisitor implements CodeUnitVisitor {

	private StringBuffer result = new StringBuffer();

	public JsonCodeUnitVisitor(){
	}
	
	public void visit(CodeUnit code) {
		String classname = code.getClassname();
		result.append("{ name : '" + classname + "'");
		if(code.getChildCount() > 0){
			result.append(", children : [");
		}
	}

	public void end(CodeUnit code) {
		
		if(code.getChildCount() > 0){
			removeTrailingComma();
			result.append("]");
		}
		result.append("}, ");
	}
	
	private void removeTrailingComma(){
		int trailingCommaPos = result.length() - 2;
		result.deleteCharAt(trailingCommaPos);
	}
	
	public String getResult() {
		return result.toString();
	}
	
}