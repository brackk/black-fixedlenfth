package com.black.fixedlength.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

import com.black.fixedlength.FLTConfig;

public class FLTEntityWriter implements AutoCloseable {
	private FLTConfig conf;
	private FLTWriter writer;
	private FLTAnnotationManager annotationManager;


	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param conf 固定長形式情報
	 * @param inputPath 読み込み先ファイルパス
	 * @param clazz データレコード格納先クラス
	 * @return 指定された{@code clazz}のインスタンス
	 * @throws FileNotFoundException ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public FLTEntityWriter(FLTConfig conf, Path outputPath)
			throws FileNotFoundException, UnsupportedEncodingException {

		this.conf = conf;
		annotationManager = new FLTAnnotationManager();

		// 指定された固定長形式情報を使用して、インスタンスを構築します。
		writer = new FLTWriter(conf, outputPath);
	}

	/**
	 * 指定されたエンティティをファイルに固定長として書き込みます。
	 *
	 * @param entity 固定長として出力するエンティティクラス
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public <T> void write(T entity) throws IOException, IllegalArgumentException, IllegalAccessException {
		if (entity == null) {
			throw new IllegalArgumentException("Invalid argument specified.");
		}

		String fixedlength = annotationManager.convertToFixedlength(conf, entity);
		writer.write(fixedlength, FLTAnnotationManager.getRecordSize(entity.getClass()));
	}

	/**
	 * 書き込み後の終了処理です。
	 * @throws IOException
	 *
	 */
	@Override
	public void close() throws IOException {
		writer.close();
	}
}
