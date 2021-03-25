package com.black.fixedlength.format;
/**
 * 読み込み時のデフォルトで指定されるトリミングフォーマット
 *
 */
public class DefaultTrimmingFormatter implements TrimmingFormat {

	@Override
	public String trimming(String param) {
		if (param != null && !param.isEmpty()) {
			return param.replaceAll("( )+$", "");
		}
		return param;
	}

}
