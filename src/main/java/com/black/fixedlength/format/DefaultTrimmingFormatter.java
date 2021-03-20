package com.black.fixedlength.format;

public class DefaultTrimmingFormatter implements TrimmingFormat {

	@Override
	public String trimming(String param) {
		if (param != null && !param.isEmpty()) {
			return param.replaceAll("( )+$", "");
		}
		return param;
	}

}
