package com.black.fixedlength.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 固定長文字列レコード定義
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Record {

	/**
	 * ヘッダ、データ、トレーラレコードかの判定に使用します。(前方一致)
	 *
	 * 指定されていない場合、判定を行いません。
	 * @return
	 */
	String recordCodeNum() default "";
}
