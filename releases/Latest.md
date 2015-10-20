## BladeRunnerJS @tagVersion@

BladeRunnerJS @tagVersion@ *** release description here ***

# Improvements to missing i18n translation handling

The handling of missing i18n translations has been changed to throw an error if tokens are missing in some circumstances. The new behaviour is described in the sections below. Note: the behaviour in development and production is very similar with the exception of the string returned when using the fallback locale translation.

## Running via the BRJS development server

**Tokens in HTML templates :** The translator will attempt to find a replacement for the 'active' locale and fall back to using the 'default' locale translations if none is found. If a translation is found by using the default locale the string `???&nbsp;token.name&nbsp;???` will be returned to bring attention to the developer that the locale translation is missing but a fallback locale is available. If no fallback translation is found an exception is thrown.

**Tokens accessed via JavaScript `i18n()` :** The translator will attempt to find a replacement for the 'active' locale and fall back to using the 'default' locale translations if none is found. If a translation is found by using the default locale the string `???&nbsp;token.name&nbsp;???` will be returned and a warning logged to the browser's console. If no fallback translation is found an exception is thrown.

## When building apps

**Tokens in HTML templates :** The translator will attempt to find a replacement for the 'active' locale and fall back to using the 'default' locale translations if none is found. If no fallback translation is found an exception is thrown.

**Tokens accessed via JavaScript `i18n()` :** The translator will attempt to find a replacement for the 'active' locale and fall back to using the 'default' locale translations if none is found. If a translation is found by using the default locale will be used and a a warning logged to the browser's console. If no fallback translation is found an exception is thrown.

