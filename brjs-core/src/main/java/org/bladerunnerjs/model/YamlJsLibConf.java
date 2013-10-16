package org.bladerunnerjs.model;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.conf.AbstractYamlConfFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.model.utility.ConfigValidationChecker;
import org.bladerunnerjs.model.utility.NameValidator;


public class YamlJsLibConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String libNamespace;
	
	@Override
	public void initialize() {
		libNamespace = "libns";
	}
	
	@Override
	public void verify() throws ConfigException {
		try {
			ConfigValidationChecker.validate(this);
			NameValidator.assertValidPackageName(node, libNamespace);
		}
		catch(InvalidPackageNameException e) {
			throw new ConfigException(e);
		}
	}
}
