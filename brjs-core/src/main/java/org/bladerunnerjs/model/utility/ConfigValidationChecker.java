package org.bladerunnerjs.model.utility;

import java.util.Set;

import javax.validation.*;

import org.bladerunnerjs.model.conf.YamlConfFile;
import org.bladerunnerjs.model.exception.ConfigException;


public class ConfigValidationChecker {
	public static <T extends YamlConfFile> void validate(T confObject) throws ConfigException {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<T>> validationErrors = validator.validate(confObject);
		
		if(!validationErrors.isEmpty()) {
			StringBuilder failureMessage = new StringBuilder("Configuration error within '" + confObject.getUnderlyingFile().getPath() + "':");
			
			for(ConstraintViolation<T> validationError : validationErrors) {
				failureMessage.append("\n\t").append("'" + validationError.getPropertyPath() + "' " + validationError.getMessage());
			}
			
			throw new ConfigException(failureMessage.toString());
		}
	}
}
