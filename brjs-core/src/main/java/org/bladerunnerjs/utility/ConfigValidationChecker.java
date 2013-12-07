package org.bladerunnerjs.utility;

import java.util.Set;

import javax.validation.*;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlConfFile;


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
