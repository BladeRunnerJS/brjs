package com.caplin.cutlass.bundler.js.minification;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.caplin.cutlass.EncodingAccessor;

public class UglifyMinifier implements Minifier
{
	private ScriptableObject scope;
	private String sourceMap;
	
	public UglifyMinifier() throws ScriptException, IOException
	{
		Reader sourceCodeReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("uglify.js"));
		Context cx = Context.enter();
		
		try
		{
			scope = cx.initStandardObjects();
			cx.evaluateReader(scope, sourceCodeReader, "<cmd>", 1, null);
		}
		finally
		{
			Context.exit();
			sourceCodeReader.close();
		}
	}
	
	@Override
	public void minifySourceCode(List<File> sourceFiles, Writer writer, int offsetLine, int offsetIndex) throws IOException
	{
		Context cx = Context.enter();
		
		try
		{
			Object wrappedSourceFiles = Context.javaToJS(convertSourceFiles(sourceFiles), scope);
			ScriptableObject.putProperty(scope, "oSourceFiles", wrappedSourceFiles);
			String minifiedSourceCode = (String) eval("var oResult = minify(); oResult.code", cx);
			sourceMap = (String) eval("oResult.map", cx);
			
			writer.write(minifiedSourceCode + "\n");
		}
		finally
		{
			Context.exit();
		}
	}

	@Override
	public void writeSourceMap(Writer outputWriter) throws IOException
	{
		outputWriter.write(sourceMap);
	}
	
	private Object eval(String sourceCode, Context cx) throws IOException
	{
		return cx.evaluateString(scope, sourceCode, "<cmd>", 1, null);
	}
	
	private List<SourceFile> convertSourceFiles(List<File> sourceFiles) throws IOException
	{
		List<SourceFile> convertedSourceFiles = new ArrayList<SourceFile>();
		
		for(File sourceFile : sourceFiles)
		{
			String sourceCode = FileUtils.readFileToString(sourceFile, EncodingAccessor.getDefaultInputEncoding());
			convertedSourceFiles.add(new SourceFile(sourceFile.getPath(), sourceCode));
		}
		
		return convertedSourceFiles;
	}
}
