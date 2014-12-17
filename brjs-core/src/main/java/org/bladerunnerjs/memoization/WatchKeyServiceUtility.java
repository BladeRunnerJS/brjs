package org.bladerunnerjs.memoization;

import java.lang.reflect.Field;
import java.nio.file.WatchEvent.Modifier;


public class WatchKeyServiceUtility
{

	public static Modifier getModifierEnum(String className, String fieldName) {
		try {
			Class<?> c = Class.forName(className);
			Field f = c.getField(fieldName);
			return (Modifier) f.get(c);
		} catch (Exception e) {
			return null;
		}
	}
	
}
