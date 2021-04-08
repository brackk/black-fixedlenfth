package com.black.fixedlength.format;

public interface PaddingFormat {

	/**
	 * パディングのインタフェースです。
	 * 戻り値が既定の文字数(またはバイト数)でない場合エラーになります。
	 *
	 * @param param パディングを行う文字列
	 * @param length パディングが必要な文字数（バイト数の場合は穴埋めに必要な文字数）
	 * @return パディング済み文字列
	 */
	public String padding(String param, int length);

}
