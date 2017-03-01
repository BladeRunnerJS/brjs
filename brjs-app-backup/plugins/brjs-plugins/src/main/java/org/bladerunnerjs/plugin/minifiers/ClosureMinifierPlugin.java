package org.bladerunnerjs.plugin.minifiers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.input.ReaderInputStream;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.InputSource;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractMinifierPlugin;

import com.Ostermiller.util.ConcatReader;
import com.google.javascript.jscomp.AnonymousFunctionNamingPolicy;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;


public class ClosureMinifierPlugin extends AbstractMinifierPlugin implements MinifierPlugin {
	
	public class Messages {
		public static final String OUTPUT_FROM_MINIFIER = "Minifying complete, output from minifier was:\n%s";
		public static final String ERROR_WHILE_BUNDLING_MSG = "There was an error while minifying, the error from the minifier was:\n%s";
	}
	
	public static final String CLOSURE_WHITESPACE = "closure-whitespace";
	public static final String CLOSURE_SIMPLE = "closure-simple";
	public static final String CLOSURE_SIMPLE_DEBUG = "closure-simple-debug";
	public static final String CLOSURE_MEDIUM = "closure-medium";
	public static final String CLOSURE_MEDIUM_DEBUG = "closure-medium-debug";
	public static final String CLOSURE_ADVANCED = "closure-advanced";
	public static final String CLOSURE_ADVANCED_DEBUG = "closure-advanced-debug";
	
	private Logger logger;
	
	private List<String> settingNames = new ArrayList<>();
	
	{
		settingNames.add(CLOSURE_WHITESPACE);
		settingNames.add(CLOSURE_SIMPLE);
		settingNames.add(CLOSURE_SIMPLE_DEBUG);
		settingNames.add(CLOSURE_MEDIUM);
		settingNames.add(CLOSURE_MEDIUM_DEBUG);
		settingNames.add(CLOSURE_ADVANCED);
		settingNames.add(CLOSURE_ADVANCED_DEBUG);
	}
	
	@Override
	public void setBRJS(BRJS brjs) 
	{
		logger = brjs.logger(this.getClass());
	}
	
	@Override
	public List<String> getSettingNames() {
		return settingNames;
	}
	
	/* using ClosureCompiler API in Java taken from http://blog.bolinfest.com/2009/11/calling-closure-compiler-from-java.html 
	 * 	and https://code.google.com/p/closure-compiler/wiki/FAQ#How_do_I_call_Closure_Compiler_from_the_Java_API? */
	@SuppressWarnings("deprecation")
	@Override
	public Reader minify(String settingName, List<InputSource> inputSources) throws ContentProcessingException, ResourceNotFoundException {
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		
		Compiler compiler = new Compiler( new PrintStream(errorStream) );
		CompilerOptions options = getCompilerOptions(settingName);
		
		/* we have to use an extern, so create a dummy one */
		SourceFile extern = SourceFile.fromCode("externs.js", "function alert(x) {}");
		SourceFile input;
		try
		{
			input = SourceFile.fromInputStream( "input.js", getSingleInputStreamForInputSources(inputSources) );
		}
		catch (IOException ex)
		{
			throw new ContentProcessingException(ex);
		}
		
		List<Reader> readers = new LinkedList<>();
		
		Result result = compiler.compile(extern, input, options);
		if (result.success)
		{
			logger.debug(Messages.OUTPUT_FROM_MINIFIER, errorStream.toString());
			try
			{
				readers.add( new InputStreamReader( new ByteArrayInputStream(compiler.toSource().getBytes()), "UTF-8") );
			}
			catch (UnsupportedEncodingException e)
			{
				throw new ContentProcessingException(e);
			}
		}
		else
		{
			throw new ContentProcessingException(String.format(Messages.ERROR_WHILE_BUNDLING_MSG, errorStream.toString()));
		}
		
		return new ConcatReader( readers.toArray(new Reader[0]) );
	}
	
	@Override
	public Reader generateSourceMap(String minifierLevel, List<InputSource> inputSources) throws ContentProcessingException {
		// TODO: implement this method
		return new StringReader("");
	}
	
	
	private InputStream getSingleInputStreamForInputSources(List<InputSource> inputSources) throws ContentProcessingException, ResourceNotFoundException
	{
		Vector<InputStream> inputStreams = new Vector<InputStream>();
		for (InputSource inputSource : inputSources)
		{
			Reader reader = inputSource.getContentPluginReader();
			inputStreams.add( new ReaderInputStream(reader, "UTF-8") );
		}
		return new SequenceInputStream( inputStreams.elements() );
	}
	
	private CompilerOptions getCompilerOptions(String settingName)
	{
		CompilerOptions options = new CompilerOptions();
		
		if (settingName.equals(CLOSURE_WHITESPACE))
		{
			CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
		}
		else if (settingName.equals(CLOSURE_SIMPLE) || settingName.equals(CLOSURE_SIMPLE_DEBUG))
		{
			CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		}
		else if (settingName.equals(CLOSURE_MEDIUM) || settingName.equals(CLOSURE_MEDIUM_DEBUG))
		{
			CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
			options.setRenamingPolicy(VariableRenamingPolicy.LOCAL, PropertyRenamingPolicy.ALL_UNQUOTED);
			options.setRenamePrivatePropertiesOnly(true);
			options.setCodingConvention(new BRJSCodingConvention());
		}
		else if (settingName.equals(CLOSURE_ADVANCED) || settingName.equals(CLOSURE_ADVANCED_DEBUG))
		{
			CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		}
		else
		{
			throw new RuntimeException("Closure compile does not support the seting " + settingName);
		}
		
		if(settingName.equals(CLOSURE_SIMPLE_DEBUG) || settingName.equals(CLOSURE_MEDIUM_DEBUG) || settingName.equals(CLOSURE_ADVANCED_DEBUG)) {
			options.setAnonymousFunctionNaming(AnonymousFunctionNamingPolicy.UNMAPPED);
			options.setGeneratePseudoNames(true);
			options.setRemoveClosureAsserts(false);
			options.setShadowVariables(false);
		}
		
		options.setLanguageIn(LanguageMode.ECMASCRIPT5);
		return options;
	}
	
}
