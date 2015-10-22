## BladeRunnerJS @tagVersion@

BladeRunnerJS @tagVersion@ *** release description here ***

### Improvements to missing i18n translation handling

The handling of missing i18n translations has been changed to throw an error if tokens are missing in some circumstances. This is a breaking change as any apps or tests that didn't load in the i18n bundle but used classes that acceseed i18n properties may generate errors. The new behaviour is described in the sections below. Note: the behaviour in development and production is very similar with the exception of the string returned when using the fallback locale translation.

#### Running via the BRJS development server

**Tokens in HTML templates :** The translator will attempt to find a replacement for the 'active' locale, if no translation is found it will fall back to using the 'default' locale. If a translation is found by using the default locale then the string `???&nbsp;token.name&nbsp;???` will be returned to bring attention to the developer that the locale translation is missing but a fallback locale is available. If no fallback translation is found then an exception is thrown.

**Tokens accessed via JavaScript `i18n()` :** The translator will attempt to find a replacement for the 'active' locale, if no translation is found it will fall back to using the 'default' locale. If a translation is found by using the default locale then the string `???&nbsp;token.name&nbsp;???` will be returned and a warning logged to the browser's console. If no fallback translation is found then an exception is thrown.

#### When building apps

**Tokens in HTML templates :** The translator will attempt to find a replacement for the 'active' locale, if a translation is not found it will fall back to using the 'default' locale. If no fallback translation is found then an exception is thrown.

**Tokens accessed via JavaScript `i18n()` :** The translator will attempt to find a replacement for the 'active' locale, if a translation is not found the it will fall back to using the 'default' locale. If a translation is found by using the default locale, that translation will be used and a a warning will be logged to the browser's console. If no fallback translation is found an exception is thrown.

#### Backwards Compatability

Due to the errors that are now thrown if a fallback translation can't be found this change will break backwards compatability for some tests and apps. All apps should include `<@i18n.bundle@/>` above the `<@js.bundle ...@/>` tag in each Aspect's `index.html`. Tests should include the i18n bundle by including `- bundles/i18n/i18n.bundle` above `bundles/js/js.bundle` in the load section of `jsTestDriver.conf`.