package com.black.fixedlength;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.black.fixedlength.format.DefaultBlankPaddingFormatter;
import com.black.fixedlength.format.DefaultTrimmingFormatter;
import com.black.fixedlength.format.DefaultZeroPaddingFormatter;
import com.black.fixedlength.format.PaddingFormat;
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
	private DateFormat dateFormat;

	/**
	 * 空行を無視するかどうかです。
	 */
	private boolean ignoreblankLines;

	/**
	 * 読み込み時に指定されたトリミングを行います。
	 * デフォルトでは、末尾ブランクを削除します。
	 */
	private TrimmingFormat trimming;

	/**
	 * 書き込み時に指定された型からの変換時にパディングを行います。
	 * デフォルトでは下記が設定されています。
	 *
	 * Integer	頭0埋め
	 * Double	頭0埋め
	 * Long		頭0埋め
	 * Float	頭0埋め
	 * Byte		末尾ブランク埋め
	 * Character末尾ブランク埋め
	 * Date		末尾ブランク埋め
	 * String	末尾ブランク埋め
	 *
	 */
	private Map<Class<?>, PaddingFormat> paddingMap = new HashMap<>();

	public FLTConfig() {
		paddingMap.put(int.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(Integer.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(double.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(Double.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(long.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(Long.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(float.class, new DefaultZeroPaddingFormatter());
		paddingMap.put(Float.class, new DefaultZeroPaddingFormatter());

		paddingMap.put(byte.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(Byte.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(char.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(Character.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(java.util.Date.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(java.sql.Date.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(Calendar.class, new DefaultBlankPaddingFormatter());
		paddingMap.put(java.sql.Timestamp.class, new DefaultBlankPaddingFormatter());

		setTrimming(new DefaultTrimmingFormatter());
	}

	public FLTConfig(String charCode) {
		this();
		this.charCode = charCode;
	}

	public FLTConfig(boolean append) {
		this();
		this.append = append;
	}

	public FLTConfig(String charCode, boolean append) {
		this();
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

	public PaddingFormat getPaddingFormat(Class<?> type){
		PaddingFormat ret = null;
		if (paddingMap.containsKey(type)) {
			ret = paddingMap.get(type);
		}
		return ret;
	}

	public void setPadding(Class<?> clazz, PaddingFormat padding) {
		paddingMap.put(clazz, padding);
	}




}
