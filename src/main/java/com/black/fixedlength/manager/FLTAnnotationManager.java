package com.black.fixedlength.manager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;

import com.black.fixedlength.FLTConfig;
import com.black.fixedlength.annotation.Column;
import com.black.fixedlength.annotation.Record;

/**
 * アノテーションを操作する処理をまとめたクラス
 *
 */
public class FLTAnnotationManager {

	/**
	 * 指定されたクラスの固定長幅を計算し、返却します。
	 * 指定するクラスには＠インターフェースが実装されている必要があります。
	 *
	 * @param clazz @インターフェースが実装されいるクラス
	 * @return 固定長幅
	 */
	protected static <T> int getRecordSize(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();

		int length = 0;
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);

			if (column != null) {
				length += column.length();
			}

		}

		return length;
	}

	/**
	 * 指定されたクラスのレコード判定文字を取得します。
	 * 指定するクラスには{＠code FLT}が実装されている必要があります。
	 *
	 * @param clazz @インターフェースが実装されいるクラス
	 * @return
	 */
	protected static <T> String getRecordCodeNum(Class<T> clazz) {
		String ret = null;

		Record annotation = clazz.getAnnotation(Record.class);
		if (annotation != null) {
			ret = annotation.recordCodeNum();
		}
		return ret;
	}


	/**
	 * 指定されたクラスに指定された文字から固定長の文字を設定します。
	 * 指定されたクラスに引数無しのコンストラクタが実装されている必要があります。
	 *
	 * @param conf 固定長形式情報
	 * @param clazz @インターフェースが実装されいるクラス
	 * @param str 格納する文字列
	 * @return 指定された{@code clazz}のインスタンス
	 * @throws InstantiationException 指定されたclazzが抽象クラス、インタフェース、配列クラス、プリミティブ型、またはvoidを表す場合、クラスが引数なしのコンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws IllegalAccessException {@code clazz}が対応していない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws IndexOutOfBoundsException 指定されたclazzまたは、指定されたstrのフォーマットが一致していない場合
	 * @throws ParseException 値の型変換に失敗した場合
	 */
	protected <T> T convertToEntity(FLTConfig conf, Class<T> clazz, String str) throws InstantiationException, IllegalAccessException, IndexOutOfBoundsException, UnsupportedEncodingException, ParseException {
		T ret = (T) clazz.newInstance();

		Field[] fields = clazz.getDeclaredFields();

		int beginIndex = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);

			if (column != null) {
				int endIndex = beginIndex + column.length();
				String value = subString(conf, str, beginIndex, endIndex);
				value = conf.getTrimming() != null ? conf.getTrimming().trimming(value) : value;
				field.set(ret, convert(value, field.getType(), conf));

				beginIndex += column.length();
			}

		}
		return ret;
	}

	/**
	 * 指定されたエンティティを固定長文字列に変換し、返却します。
	 *
	 * @param conf 固定長形式情報
	 * @param entity 変換元エンティティ
	 * @return 固定長文字列
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws UnsupportedEncodingException
	 */
	protected <T> String convertToFixedlength(FLTConfig conf, T entity) throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {

		String ret = "";
		Field[] fields = entity.getClass().getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);

			if (column != null) {
				String value = convert(field.get(entity), conf);

				int length = 0;
				switch (conf.getFltType()) {
				case BYTE :
					length = column.length() - value.getBytes(conf.getCharCode()).length + value.length();
					break;

				case STRING :
					length = column.length();
					break;
				default:
					throw new IllegalArgumentException(String.format("Unknown enum tyoe %s", conf.getFltType()));
				}

				value = conf.getPaddingFormat(field.getType()).padding(value, length);
				if (value == null) {
					throw new IllegalArgumentException(String.format("Not setting PaddingFormat %s", entity.getClass()));
				}
				ret += value;
			}
		}

		return ret;
	}


	/**
	 * 指定された文字列から固定長の文字列を抜き出します。
	 *
	 * @param conf 固定長形式情報
	 * @param str 抜き出し元の文字列
	 * @param beginIndex 開始インデックス(この値を含む)
	 * @param endIndex 終了インデックス(この値を含まない)
	 * @return 指定された部分文字列
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws IndexOutOfBoundsException beginIndexが負であるか、endIndexがこのStringオブジェクトの長さより大きいか、あるいはbeginIndexがendIndexより大きい場合。
	 */
	private static String subString(FLTConfig conf, String str, int beginIndex, int endIndex) throws IndexOutOfBoundsException, UnsupportedEncodingException {
		String ret = new String();

		switch(conf.getFltType()) {
			case BYTE :
				byte[] bstr = str.getBytes(conf.getCharCode());
				byte[] retBstr = new byte[endIndex - beginIndex];
				for(int i = beginIndex; i < bstr.length; i++) {
					retBstr[beginIndex - i] = bstr[i];
				}

				ret = new String(retBstr);
				break;

			case STRING :
				ret = str.substring(beginIndex, endIndex);
				break;
			default:
				throw new IllegalArgumentException(String.format("Unknown enum tyoe %s", conf.getFltType()));
		}

		return ret;
	}

	/**
	 * 指定された文字列を指定された型のオブジェクトへ変換して返します。<p>
	 * 指定された文字列が {@code null} や空文字列の場合に、どのような値が返されるかは実装に依存します。
	 * @param <T>
	 *
	 * @param str 変換する文字列
	 * @param type 変換する型
	 * @return 変換されたオブジェクト
	 * @throws ParseException 値の型変換に失敗した場合
	 * @throws IllegalArgumentException 変換に失敗した場合
	 */
	protected Object convert(String str, Class<?> type, FLTConfig conf) throws ParseException {

		if (type == null) {
			return null;
		}

		if (type == String.class) {
			return str.toString();
		} else if (type == int.class ||  type == Integer.class) {
			return Integer.valueOf(str);
		} else if (type == double.class|| type == Double.class ) {
			return Double.valueOf(str);
		} else if (type == long.class || type == Long.class) {
			return Long.valueOf(str);
		} else if (type == float.class || type == Float.class) {
			return Float.valueOf(str);
		} else if (type == byte.class || type == Byte.class) {
			return Byte.valueOf(str);
		} else if (type == char.class || type == Character.class) {
			char[] chars = str.toCharArray();
			return Character.valueOf(chars.length == 0 ? null : chars[0] );
		} else if (type == short.class || type == Short.class) {
			return Short.valueOf(str);
		} else if (type == java.util.Date.class || type == java.sql.Date.class || type == Calendar.class || type == java.sql.Timestamp.class) {
			if (conf.getDateFormat() == null) {
				throw new IllegalArgumentException("The conversion date format is not set.");
			}
			if (type == java.util.Date.class) {
				return conf.getDateFormat().parse(str);
			} else if (type == java.sql.Date.class) {
				return new java.sql.Date(conf.getDateFormat().parse(str).getTime());
			} else if (type == Calendar.class) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(conf.getDateFormat().parse(str));
				return cal;
			} else if (type == java.sql.Timestamp.class) {
				return new java.sql.Timestamp(conf.getDateFormat().parse(str).getTime());
			}
		}

		return null;
	}

	/**
	 * 指定されたobject型からString型へ変換し返却します。
	 *
	 * @param obj 変換を行うオブジェクト
	 * @param conf 固定長形式情報
	 * @return 文字列
	 */
	protected String convert(Object obj, FLTConfig conf) {
		Class<?> type = obj.getClass();

		if (type == String.class) {
			return obj.toString();
		} else if (type == int.class ||  type == Integer.class) {
			return obj.toString();
		} else if (type == double.class|| type == Double.class ) {
			return obj.toString();
		} else if (type == long.class || type == Long.class) {
			return obj.toString();
		} else if (type == float.class || type == Float.class) {
			return obj.toString();
		} else if (type == byte.class || type == Byte.class) {
			return obj.toString();
		} else if (type == char.class || type == Character.class) {
			return obj.toString();
		} else if (type == short.class || type == Short.class) {
			return obj.toString();
		} else if (type == java.util.Date.class || type == java.sql.Date.class || type == Calendar.class || type == java.sql.Timestamp.class) {
			if (conf.getDateFormat() == null) {
				throw new IllegalArgumentException("The conversion date format is not set.");
			}
			if (type == java.util.Date.class) {
				return conf.getDateFormat().format(obj);
			} else if (type == java.sql.Date.class) {
				java.util.Date date = new java.util.Date(((java.sql.Date)obj).getTime());
				return conf.getDateFormat().format(date);
			} else if (type == Calendar.class) {
				java.util.Date date = ((Calendar)obj).getTime();
				return conf.getDateFormat().format(date);
			} else if (type == java.sql.Timestamp.class) {
				java.util.Date date = new java.util.Date(((java.sql.Timestamp)obj).getTime());
				return conf.getDateFormat().format(date);
			}
		}

		throw new IllegalArgumentException(String.format("Unknown convert type %s", type));
	}
}
