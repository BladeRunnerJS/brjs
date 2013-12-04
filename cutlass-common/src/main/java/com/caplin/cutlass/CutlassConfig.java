package com.caplin.cutlass;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;


public class CutlassConfig
{
	// TODO: these constants should be deleted and the code updated to use SdkModel instead
	public static final String COMMON_CSS = "common";
	public static final String SDK_THEME_NAME = "sdk";
	
	// TODO: all of these constants should be deleted and the code updated to use SdkModel instead
	public static final String PRODUCT_NAME = BRJS.PRODUCT_NAME;
	public static final String COMMAND_RUNNER_NAME = "bladerunner";
	public static final String BLADESET_SUFFIX = "-bladeset";
	public static final String DEFAULT_ASPECT_NAME = "default";
	public static final String ASPECT_SUFFIX = "-aspect";
	public static final String DEFAULT_ASPECT_DIR = DEFAULT_ASPECT_NAME + ASPECT_SUFFIX;
	public static final String THIRDPARTY_BUNDLE_SUFFIX = "_thirdparty.bundle";
	public static final String BLADES_CONTAINER_DIR = "blades";
	public static final String APPLICATIONS_DIR = "apps";
	public static final String SDK_DIR = "sdk";
	public static final String USER_LIBS_DIR = "libs";
	public static final String CONF_DIR = "conf";
	public static final String TEMP_DIR = "cutlass-tmp";
	public static final String TESTS_DIR = "tests";
	public static final String TEST_RESULTS_DIR = "test-results";
	public static final String XML_TEST_RESULTS_DIR = TEST_RESULTS_DIR+"/xml";
	public static final String HTML_TEST_RESULTS_DIR = TEST_RESULTS_DIR+"/html";
	public static final String THIRDPARTY_DIR_IN_APP = "thirdparty-libraries";
	
	public static final String RELATIVE_SRC_DIR = "src";
	public static final String RELATIVE_SRC_TEST_DIR = "src-test";
	public static final List<String> POSSIBLE_SRC_DIR_NAMES = Arrays.asList(RELATIVE_SRC_DIR,RELATIVE_SRC_TEST_DIR);
	
	public static final String LIBRARY_MANIFEST_FILENAME = "library.manifest";
	
	public static final String APP_CONF_FILENAME = "app.conf";
	
	public static final String DEFAULT_APP_LOCALES = "en";
	
	public static final String AUTO_DEPLOY_CONTEXT_FILENAME = ".deploy";
	
	public static final String CUTLASS_SDK_HIBERNATE_DIALECT = "org.hibernate.dialect.H2Dialect";
	public static final String HIBERNATE_CFG_XML = "/WEB-INF/classes/hibernate.cfg.xml";
	public static final String WEBCENTRIC_XML_FOLDER = "/webcentric";
	public static final String DEFAULT_ASPECT_WEBCENTRIC_XML_FOLDER = "/default" + ASPECT_SUFFIX + WEBCENTRIC_XML_FOLDER;
	
	public static final String TEST_INTEGRATION_DIRNAME = "test-integration";
	public static final String WEBDRIVER_DIRNAME = "webdriver";
	public static final String TEST_INTEGRATION_PATH = TEST_INTEGRATION_DIRNAME+"/"+WEBDRIVER_DIRNAME+"/tests";
	public static final String TEST_INTEGRATION_CLASSES_DIRNAME = "classes";
	
	public static final String SERVLET_PATH_PREFIX = "/servlet/";
	
	public static final String DEFAULT_INVALID_ARGUMENTS_FOR_TASK_MESSAGE = "Please provide valid arguments for this task.";
	
	/* servlet context attribute for SectionRedirectFilter */
	public static final String DEV_MODE_FLAG = "dev-mode";
	
	/* bundler tokens */
	public static final String CSS_BUNDLE_TOKEN = "css.bundle";
	public static final String JS_BUNDLE_TOKEN = "js.bundle";
	public static final String I18N_BUNDLE_TOKEN = "i18n.bundle";
	
	/* Image extensions */
	public static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg","jpeg","bmp","png","gif","svg","ico","cur","eot","ttf","woff");
	
	public static final String OS = automaticallyDetermineOperatingSystem();
	public static String automaticallyDetermineOperatingSystem() {
		String osNameProperty = System.getProperty("os.name").toLowerCase();
		String os;
		
		if(osNameProperty.startsWith("linux")) {
			os = "linux";
		}
		else if(osNameProperty.startsWith("mac")) {
			os = "mac";
		}
		else if(osNameProperty.startsWith("windows")) {
			os = "windows";
		}
		else {
			os = "unix";
		}
		
		return os;
	}
	
	
	
	
	
}
