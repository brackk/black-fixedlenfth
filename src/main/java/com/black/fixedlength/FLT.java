package com.black.fixedlength;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.black.fixedlength.manager.FLTEntityReader;


public class FLT {

	/**
	 * 固定長ファイルを出力します。
	 *
	 * @param conf 固定長形式情報
	 * @param outputPath 出力先ファイルパス
	 */
	public static void save(FLTConfig conf, Path outputPath) {

	}


	/**
	 * 固定長ファイルを読み込みます。
	 *
	 * @param conf 固定長形式情報
	 * @param clazz 格納先クラス
	 * @return 指定された{@code clazz}のインスタンスのList
	 * @throws IllegalArgumentException パラメータが不正な場合
	 * @throws IllegalAccessException 指定されたclazzが対応していない場合
	 * @throws InstantiationException 指定されたclazzが抽象クラス、インタフェース、配列クラス、プリミティブ型、またはvoidを表す場合、クラスが引数なしのコンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws ParseException
	 */
	public static <T> List<T> load(FLTConfig conf,Path inputPath, Class<T> clazz)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, IllegalStateException, IOException, ParseException {
		List<T> ret = new ArrayList<T>();

		try (FLTEntityReader<T> entityReader = new FLTEntityReader<>(conf, inputPath, clazz);) {
			T line = null;
			while ((line = entityReader.read()) != null) {

				ret.add(line);
			}
		}

		return ret;
	}
}
