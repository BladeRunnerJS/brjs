package org.bladerunnerjs.testing.specutility.logging;

import java.lang.annotation.*;


/* adapted from https://github.com/mike-ensor/expected-failure */

/**
 * Initially this is just a marker annotation to be used by a JUnit4 Test case
 * in conjunction with ExpectedTestFailure @Rule to indicate that a test is
 * supposed to be failing
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface ExpectedFailure
{

	// TODO: enhance by adding specific information about what type of failure expected
	//Class<? extends Throwable> assertType() default Throwable.class;

	/**
	 * Text based reason for marking test as ExpectedFailure
	 * 
	 * @return String
	 */
	String value() default "";
}
