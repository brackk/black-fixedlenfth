package com.black.fixedlength.format;

public class DefaultZeroPaddingFormatter implements PaddingFormat {

	@Override
	public String padding(String param, int length) {
		return String.format("%" + length + "s", param).replace(" ", "0");
	}

}
