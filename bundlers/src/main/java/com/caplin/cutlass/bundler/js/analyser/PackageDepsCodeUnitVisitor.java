package com.caplin.cutlass.bundler.js.analyser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnit;
import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor;

/**
 * Lists all classes or packages referenced outside the given target package.
 */
public class PackageDepsCodeUnitVisitor implements CodeUnitVisitor {

	private Set<String> visited = new HashSet<String>();
	private final boolean isSummary;
	private final String targetPackageName;
	
	public PackageDepsCodeUnitVisitor(String targetPackageName, boolean isSummary){
		this.targetPackageName = targetPackageName;
		this.isSummary = isSummary;
	}
	
	public void visit(CodeUnit code){
		String classname = code.getClassname();
		
		if(classname.endsWith(".js")){
			return;
		}
		
		String packageName = getPackageName(classname);
		if(targetPackageName.equals(packageName)){
			return;
		}
		
		if(this.isSummary){
			if(!visited.contains(packageName)){
				visited.add(packageName);
			}
		}else{
			if(!visited.contains(classname)){
				visited.add(classname);
			}
		}
	}
	
	public String getResult()
	{
		StringBuffer result = new StringBuffer();
		List<String> sorted = new ArrayList<String>(visited);
		Collections.sort(sorted);
		
		for(String dependency:sorted){
			result.append(dependency);
			result.append("\n");
		}
		return result.toString();
	}
	
	private String getPackageName(String classname) {
		int lastDotPos = classname.lastIndexOf(".");
		
		if(lastDotPos == -1)
		{
			return classname;
		}
		
		String result = classname.substring(0,lastDotPos);
		return result;
	}

	public void end(CodeUnit code){}
	
}
