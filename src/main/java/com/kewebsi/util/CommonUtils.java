package com.kewebsi.util;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.MalformedClientDataException;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {

	private static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	public static final String WIRENULL = "<WIRENULL>";

	public static boolean hasInfo(String str) {
		if (str != null && str.length() > 0 && str.trim().length() > 0) {
			return true;
		}
		return false;
	}

	public static boolean hasNoInfo(String str) {
		return ! hasInfo(str);
	}

	public static boolean hasData(Collection collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public static String boolToStr(boolean in) {
		return in ? "y" : "n";
	}

	public static String bufferedReaderToString(BufferedReader reader) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			LOG.error("Unexpected exception cought: ", e);
		}
		return jb.toString();
	}

	public static boolean equals(String a, String b) {
		if (a == null) {
			if (b == null) {
				return true;
			} else {
				return false;
			}
		} else {
			if (b == null) {
				return false;
			} else {
				if (a.equals(b)) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public static boolean contains(String[] arr, String str) {
		for (String strRun : arr) {
			if (equals(strRun, str)) {
				return true;
			}
		}
		return false;
	}

	public static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	public static String escape(String s) {
		if (s == null) {
			return "";
		} else {
			return StringEscapeUtils.escapeHtml4(s);
		}
	}

	public static String modelToHtml(String s) {
		if (s == null) {
			return "";
		} else {
			String s2 = StringEscapeUtils.escapeHtml4(s);
			s2 = s2.replaceAll("\n", "<br>");
			return s2;
		}
	}
	
	public static String modelToHtml(Enum<?> en) {
		if (en == null) {
			return "";
		} else {
			String s = en.name();
			s = StringEscapeUtils.escapeHtml4(s);
			s = s.replaceAll("\n", "<br>");
			return s;
		}
	}
	

	public static final String modelToHtml(int i) {
		return String.valueOf(i);
	}
	
	public static final String modelToHtml(Long l) {
		if (l == null) {
			return "";
		} else {
			return String.valueOf(l);
		}
	}
	
	public static final String modelToHtml(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		return sdf.format(date);
	}

	public static String replaceCrByBr(String s) {
		if (s == null) {
			return s;
		} else {
			return s.replaceAll("\n", "<br>");
		}
	}

//	public static String modelToGui(String s) {
//		if (s==null) {
//			return "";
//		} 
//		
//	}

	/**
	 * Checks whether the name of the member variables of the given class are
	 * contained in the given enum. Useful for handling member variables as symbols.
	 * 
	 * @param <T>    Helper to handle enums.
	 * @param <C>    Helper to handle the class.
	 * @param mySelf The given class whose member variables are checked
	 * @param en     The Enum to check against.
	 * @return The first member variable which is not found in the given Enum (by
	 *         name). Returns null, if all member variables are found.
	 */
	public static <T extends Enum<T>, C> String checkMembersAreInEnum(Class<C> mySelf, Class<T> en) {
		Field[] fields = mySelf.getDeclaredFields();
		for (Field fieldRun : fields) {
			if (fieldRun.getType() == String.class) {
				String fieldName = fieldRun.getName();
				if (EnumUtils.getEnumFromString(en, fieldName) == null) {
					return fieldName;
				}
			}
		}
		return null;
	}

	public static <C> String checkMembersAgainstStrings(Class<C> mySelf, String[] str) {
		Field[] fields = mySelf.getDeclaredFields();
		for (Field fieldRun : fields) {
			if (fieldRun.getType() == String.class) {
				String fieldName = fieldRun.getName();
				if (!contains(str, fieldName)) {
					return fieldName;
				}
			}
		}
		return null;
	}

	public static int parseInt(String s, int defaultOnError) {
		int result;
		try {
			result = Integer.parseInt(s);
		} catch (Exception e) {
			LOG.warn("Illegeal integer %s encountered. Defaulted to %d", s, defaultOnError);
			result = defaultOnError;
		}
		return result;
	}

	public static int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new MalformedClientDataException(e.getMessage());
		}
	}


	public static <K, V> K getKey(Map<K, V> map, V value) {
	    for (Entry<K, V> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}	
	
	public static ArrayList<String> arrayToArrayList(String [] array) {
		ArrayList<String> result = new ArrayList<>(array.length);
		Collections.addAll(result, array);
		return result;
	}
	
	
	public static String[] arrayListToArrayList(ArrayList<String> input) {
		String[] result = new String[input.size()];
		result = input.toArray(result);
		return result;
	}


	public static String fillWithLeadingZeros(int upToLength, String in) {
		int nbrOfZeros = in.length()-upToLength;

		String zeros = "";
		if (nbrOfZeros > 0) {
			zeros += "0";
		}

		String result = zeros + in;
		return result;
	}


	public static String twoDigits(int in) {
		if (in < 10) {
			return "0" + in;
		} else {
			return String.valueOf(in);
		}
	}

	public static final String[] arr(String... in) {
		return in;
	}



	public static void addAttributes(HashMap<String, String> attrs, String... keyValue) {
		if (keyValue != null) {
			if (keyValue.length % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<keyValue.length) {
				String key = keyValue[i++];
				String value = keyValue[i++];
				if (CommonUtils.hasInfo(key)) {
					attrs.put(key, value);
				}
			}
		}
	}


	
}
