package org.bladerunnerjs.model;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;


public class YamlJsLibConf extends AbstractYamlConfFile {
	@NotNull
	@NotEmpty
	public String libNamespace;
	
	@Override
	public void initialize() {
		libNamespace = getDefault(libNamespace, "libns");
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
