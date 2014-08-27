EmailValidatorTest = TestCase("EmailValidatorTest");

EmailValidatorTest.prototype.setUp = function() {
	this.oEmailValidator = new br.presenter.validator.EmailValidator();
}

EmailValidatorTest.prototype.test_validEmailAddressesPass = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oEmailValidator.validate("john.smith@bladerunnerjs.org", {}, oValidationResult);
	assertTrue("a1", oValidationResult.isValid());
};

EmailValidatorTest.prototype.test_noAtSymbolFails = function()
{
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.oEmailValidator.validate("john.smithbladerunnerjs.org", {}, oValidationResult);
	assertFalse("1a", oValidationResult.isValid());
};

EmailValidatorTest.prototype.test_table = function() {
	// Taken from http://www.pgregg.com/projects/php/code/showvalidemail.php
	var testCases = {
			'name.lastname@domain.com':	true,
			'.@':	false,
			'a@b':	false,
			'@bar.com':	false,
			'@@bar.com':	false,
			'a@bar.com':	true,
			'aaa.com':	false,
			'aaa@.com':	false,
			'aaa@.123':	false,
			'aaa@[123.123.123.123]':	true,
			'aaa@[123.123.123.123]a':	false,
			'aaa@[123.123.123.333]':	false,
			'a@bar.com.':	false,
			'a@bar':	false,
			'a-b@bar.com':	true,
			'+@b.c':	false,
			'+@b.com':	true,
			'a@-b.com':	false,
			'a@b-.com':	false,
			'-@..com':	false,
			'-@a..com':	false,
			'a@b.co-foo.uk':	true,
			'"hello my name is"@stutter.com':	true,
			'"Test \"Fail\" Ing"@example.com':	true,
			'valid@special.museum':	true,
			'invalid@special.museum-':	false,
			'shaitan@my-domain.thisisminekthx':	false,
			'test@...........com':	false,
			'foobar@192.168.0.1':	false,
			'"Abc\@def"@example.com':	true,
			'"Fred Bloggs"@example.com':	true,
			'"Joe\Blow"@example.com':	true,
			'"Abc@def"@example.com':	true,
			'customer/department=shipping@example.com':	true,
			'$A12345@example.com':	true,
			'!def!xyz%abc@example.com':	true,
			'_somename@example.com':	true,
			'Test \ Folding \ Whitespace@example.com':	true,
			/* Practically no email client supports comments.  If this is the only
			 * one we incorrectly reject, it's probably OK.
			 */
			'HM2Kinsists@(that comments are allowed)this.is.ok':	true,
			'user%uucp!path@somehost.edu': true
	}
	var falsePositives = [];
	var falseNegatives = [];
	var oValidationResult = new br.presenter.validator.ValidationResult();
	for (email in testCases) {
		if (testCases[email] == true) {
			this.oEmailValidator.validate(email, {}, oValidationResult);
			if (oValidationResult.isValid() == false) {
				falseNegatives.push(email);
			}
		} else {
			this.oEmailValidator.validate(email, {}, oValidationResult);
			if (oValidationResult.isValid() == true) {
				falsePositives.push(email);
			}
		}
	}
	if (falseNegatives.length > 0) {
		//jstestdriver.console.log("False negatives : "+falseNegatives.join(", "));
	}
	if (falsePositives.length > 0) {
		//jstestdriver.console.log("False positives : "+falsePositives.join(", "));
	}

	// False negatives (you aren't allowed to enter a real email address)
	// are more serious than false positives.
	assertTrue(falseNegatives.join(", "), falseNegatives.length == 0);
	assertTrue(falsePositives.join(", "), falsePositives.length < 13);
};
