package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class MissingAppJarsException extends ModelOperationException
{
	private static final long serialVersionUID = 1L;
	public static final String OUTDATED_JAR_MESSAGE = "The app '%s' is either missing BRJS jar(s), contains BRJS jar(s) it shouldn't or the BRJS jar(s) are outdated."+
			" You should delete all jars prefixed with '%s' in the WEB-INF/lib directory and copy in all jars contained in '%s'.";
	
	public MissingAppJarsException(App app) {
		super( String.format(OUTDATED_JAR_MESSAGE, app.getName(), "brjs-", app.root().dir().getRelativePath(app.root().appJars().dir()) ) );
	}
	
}
