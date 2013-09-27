package com.caplin.cutlass.bundler.js.analyser;

import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnit;
import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor;

public class HTMLCodeUnitVisitor implements CodeUnitVisitor {

	private StringBuffer result = new StringBuffer();
	private boolean lineBreaks;

	public HTMLCodeUnitVisitor(boolean lineBreaks){
		this.lineBreaks = lineBreaks;
	}
	
	public void visit(CodeUnit code) {
		if(code.getLevel() == 0){
			tag("ul");
		}
		String classname = code.getClassname();
		if(code.getChildCount() == 0){
			indent(code);
			tag("li");
			result.append(classname);
			tagEnd("li");
		}else{
			indent(code);
			tag("li");
			result.append(classname);
			tag("ul");
		}
	}

	public void end(CodeUnit code) {
		
		if(code.getChildCount() > 0){
			indent(code);
			tagEnd("ul");
			tagEnd("li");
		}
		if(code.getLevel() == 0){
			newline();
			tagEnd("ul");
		}
	}
	
	private void indent(CodeUnit code) {
		newline();
		for (int i = 0; i <= code.getLevel(); i++) {
			result.append(" ");
		}
	}
	
	private void tag(String name) {
		tag(name, false);
	}

	private void tagEnd(String name) {
		tag(name, true);
	}
	
	private void tag(String name, boolean end) {
		result.append("<");
		if (end) {
			result.append("/");
		}
		result.append(name);
		result.append(">");
	}

	public String getResult() {
		return result.toString();
	}
	
	private void newline() {
		if(lineBreaks){
			result.append("\n");
		}
	}
}