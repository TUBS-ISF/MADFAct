package de.tu_bs.isf.madfa.benchmark;

import java.util.HashMap;
import java.util.Map;

public class RuntimeMap {
	private static Map<String, Long> map = new HashMap<String, Long>();
	
	public static void insertInto(String methodName, long number) {
		long oldNumber;
		if (map.containsKey(methodName)) {
			oldNumber = map.get(methodName);
			map.put(methodName, oldNumber + number);
		} else {
			map.put(methodName, number);
		}
	}
	
	public static void print() {
		for (String string : map.keySet()) {
			long number = map.get(string);
			if (string.contains("Runtime")) {
				System.out.println(string + ": " + number/1000000 + "ms");
			} else {
				System.out.println(string + ": " + number);
			}
			
		}
	}
}
