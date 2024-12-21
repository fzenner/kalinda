package com.kewebsi.util;

import java.util.ArrayList;
import java.util.EnumSet;

public class EnumUtils {

	/**
	 *
	 * @param c The enum class against we will check.
	 * @param string The string to find in the enum class.
	 * @param <T>
	 * @return Then corresponding enum. null, if the string is not present in the enum.
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
		if (c != null && string != null) {
			try {
				return Enum.valueOf(c, string);
			} catch (IllegalArgumentException ex) {
				// LOG.warn(String.format("No enum on class %s found for string %s.",
				// c.getName(), string));
			}
		}
		return null;
	}
	
	public static  <T extends Enum<T>> boolean isInEnum(Class<T>  en, String enumName) {
		if (enumName == null) {
			return false;
		}
		try {
			Enum.valueOf(en, enumName);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
		
	}

	public static <T extends Enum<T>> String[] enumToStringArray(Class<T> c) {
	
		EnumSet<T> enumSet = EnumSet.allOf(c);
		ArrayList<String> result = new ArrayList<String>(enumSet.size());
		for (Enum<T> enumRun : enumSet) {
			result.add(enumRun.name());
		}
		String[] resultArr = new String[result.size()];
		return result.toArray(resultArr);
	}


	public static <T extends Enum<T>> ArrayList<String> enumToStringArrayList(Class<T> c) {

		EnumSet<T> enumSet = EnumSet.allOf(c);
		ArrayList<String> result = new ArrayList<String>(enumSet.size());
		for (Enum<T> enumRun : enumSet) {
			result.add(enumRun.name());
		}
		return result;
	}

	public static  Enum<?> getEnumFromArrByString(Enum<?>[] enums, String s) {
		for (Enum<?> enumRun : enums) {
			if (enumRun.name().equals(s)) {
				return enumRun;
			}
		}
		return null;
	}

	public static Enum<?> getEnumFromString(Enum<?> en, String enumName) {
		@SuppressWarnings("unchecked")
		Enum<?> result = Enum.valueOf(en.getClass(), enumName);
		return result;
	}



}
