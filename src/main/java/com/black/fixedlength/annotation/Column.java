package com.black.fixedlength.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 固定長文字列項目定義
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Column {

	/**
	 * 文字数/バイト数
	 * @return
	 */
	int length() default 0;

	/**
	 * 名称(この値は、動作に影響を与えるものではありません。)
	 * @return
	 */
	String name();

}
