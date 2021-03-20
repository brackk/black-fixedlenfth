package com.black.fixedlength;

import java.text.DateFormat;

import com.black.fixedlength.format.DefaultTrimmingFormatter;
import com.black.fixedlength.format.TrimmingFormat;
import com.black.fixedlength.type.FLTType;


/**
 * 固定長形式情報を提供します。
 *
 */
public class FLTConfig {
	/**
	 * 文字コード(初期値：Shift_JIS)
	 */
	private String charCode = "Shift_JIS";

	/**
	 * 固定長の読み取り形式情報8(初期値：文字数)
	 */
	private FLTType fltType = FLTType.STRING;

	/**
	 * ファイル作成 上書き:FALSE / 追記:TRUE
	 */
	private boolean append = false;

	/**
	 * 日付書式情報
	 */
	private DateFormat dateFormat = null;

	/**
	 * 空行を無視するかどうかです。
	 */
	private boolean ignoreblankLines;

	/**
	 * 読み込み時に指定されたトリミングを行います。
	 * デフォルトでは、末尾ブランクを削除します。
	 */
	private TrimmingFormat trimming = new DefaultTrimmingFormatter();


	public FLTConfig() {

	}

	public FLTConfig(String charCode) {
		this.charCode = charCode;
	}

	public FLTConfig(boolean append) {
		this.append = append;
	}

	public FLTConfig(String charCode, boolean append) {
		this.charCode = charCode;
		this.append = append;
	}


	public String getCharCode() {
		return charCode;
	}
	public void setCharCode(String charCode) {
		this.charCode = charCode;
	}
	public FLTType getFltType() {
		return fltType;
	}
	public void setFltType(FLTType fltType) {
		this.fltType = fltType;
	}
	public boolean isAppend() {
		return append;
	}
	public void setAppend(boolean append) {
		this.append = append;
	}
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isIgnoreblankLines() {
		return ignoreblankLines;
	}

	public void setIgnoreblankLines(boolean ignoreblankLines) {
		this.ignoreblankLines = ignoreblankLines;
	}

	public TrimmingFormat getTrimming() {
		return trimming;
	}

	public void setTrimming(TrimmingFormat trimming) {
		this.trimming = trimming;
	}




}
