package com.caplin.cutlass.bundler.js.analyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.caplin.cutlass.bundler.js.Match;

/**
 *  Contains a set of dependency trees of CodeUnit objects each of which represents either
 *  a class or a library.
 */
public class DefaultCodeAnalyser implements CodeAnalyser {
	/* map of seed file name -> dependency tree */
	private Map<String, CodeUnit> dependencies = new HashMap<String, CodeUnit>();
	private Stack<CodeUnit> ancestors;
	
	public DefaultCodeAnalyser() {
	}
	
	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.analyser.CodeAnalyser#setRoot(java.lang.String)
	 */
	@Override
	public void setRoot(String rootName){
		CodeUnit root = new CodeUnit(rootName, null, false, false);
		ancestors = new Stack<CodeUnit>();
		ancestors.push(root);
		dependencies.put(rootName, root);
	}

	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.analyser.CodeAnalyser#add(com.caplin.cutlass.bundler.js.Match)
	 */
	@Override
	public void add(Match match){
		String name = match.getDependencyName();
		CodeUnit parent = ancestors.peek();
		CodeUnit newUnit = new CodeUnit(name, parent, match.isStaticDependency(), match.isThirdPartyDependency());
		parent.add(newUnit);
		ancestors.push(newUnit);
	}

	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.analyser.CodeAnalyser#addCompleted()
	 */
	@Override
	public void addCompleted(){
		ancestors.pop();
	}
	
	
	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.analyser.CodeAnalyser#emit(com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor)
	 */
	@Override
	public void emit(CodeUnitVisitor visitor){
		for (Entry<String, CodeUnit> entry : dependencies.entrySet()) {
			entry.getValue().visit(visitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.analyser.CodeAnalyser#emitString()
	 */
	@Override
	public String emitString(){
		StringVisitor visitor = new StringVisitor();
		emit(visitor);
		return visitor.result.toString();
	}

	public class CodeUnit {

		private List<CodeUnit> children = new ArrayList<CodeUnit>();
		private final String classname;
		private int level = 0;
		private boolean staticDependency = false;
		private boolean thirdPartyLibrary = false;
		
		private CodeUnit(String classname, CodeUnit parent, boolean staticDependency, boolean thirdPartyLibrary) {
			this.staticDependency = staticDependency;
			this.thirdPartyLibrary = thirdPartyLibrary;
			this.classname = classname;
			if(parent != null){
				level = parent.level + 1; 
			}
		}
		
		public String getClassname(){
			return classname;
		}
		
		public int getLevel(){
			return level;
		}

		public int getChildCount(){
			return children.size();
		}
		
		public boolean isStaticDependency(){
			return staticDependency;
		}
		
		public boolean isThirdPartyLibrary(){
			return thirdPartyLibrary;
		}
		
		private void add(CodeUnit unit) {
			children.add(unit);
		}
		
		private void visit(CodeUnitVisitor visitor){
			visitor.visit(this);
			for(CodeUnit child : children){
				child.visit(visitor);
			}
			visitor.end(this);
		}
	}
	
	public interface CodeUnitVisitor{
		public void visit(CodeUnit code);
		public void end(CodeUnit code);
	}
	
	private class  StringVisitor implements CodeUnitVisitor{
		private StringBuffer result = new StringBuffer();
		
		public void visit(CodeUnit code){
			for(int i = 0; i < code.getLevel(); i++){
				result.append(".");
			}
			result.append(code.getClassname());
			if(code.isStaticDependency()){
				result.append(" - STATIC");
			}
			if(code.isThirdPartyLibrary()){
				result.append(" - LIB");
			}

			result.append("\n");
		}
		
		public void end(CodeUnit code){}
	}
}
