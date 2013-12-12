package org.bladerunnerjs.utility;

import java.util.Set;

import javax.validation.*;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlConfFile;


public class ConfigValidationChecker {
	
	private static Validator validator;
	
	public static <T extends YamlConfFile> void validate(T confObject) throws ConfigException 
	{
		Set<ConstraintViolation<T>> validationErrors = getValidator().validate(confObject);
		
		if(!validationErrors.isEmpty()) {
			StringBuilder failureMessage = new StringBuilder("Configuration error within '" + confObject.getUnderlyingFile().getPath() + "':");
			
			for(ConstraintViolation<T> validationError : validationErrors) {
				failureMessage.append("\n\t").append("'" + validationError.getPropertyPath() + "' " + validationError.getMessage());
			}
			
			throw new ConfigException(failureMessage.toString());
		}
	}
	
	private static Validator getValidator()
	{
		if (validator == null)
		{
			ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
			validator = validatorFactory.getValidator();
		}
		return validator;
	}
	
}
