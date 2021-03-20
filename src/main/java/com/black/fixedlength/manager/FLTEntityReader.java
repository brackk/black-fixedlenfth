package com.black.fixedlength.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.text.ParseException;

import com.black.fixedlength.FLTConfig;



public class FLTEntityReader<T> implements AutoCloseable {

	private Class<T> clazz;
	private FLTConfig conf;
	private FLTReader reader;
	private FLTAnnotationManager annotationManager;

	private String headerRecord;
	private String trailerRecord;

	private int readCount = 0;
	private boolean endOfFile = false;


	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param conf 固定長形式情報
	 * @param inputPath 読み込み先ファイルパス
	 * @param clazz データレコード格納先クラス
	 * @return 指定された{@code clazz}のインスタンス
	 * @throws FileNotFoundException ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	public FLTEntityReader(FLTConfig conf, Path inputPath, Class<T> clazz)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalArgumentException {

		this.conf = conf;
		this.clazz = clazz;
		annotationManager = new FLTAnnotationManager();

		// 指定された固定長形式情報を使用して、インスタンスを構築します。
		reader = new FLTReader(conf, inputPath, FLTAnnotationManager.getRecodeSize(clazz));
	}

	/**
	 * 1行を読み込みクラスに格納し返却します。
	 * 指定されているrecodeSizeより数値が大きい場合は範囲のデータのみ取得します。
	 * 指定されているrecodeSizeより数値が小さい場合はIOExceptionをスローします。
	 *
	 * @return 指定されたclazzのインスタンス
	 * @throws IllegalStateException 実行できる状態でない場合
	 * @throws IllegalArgumentException 指定されている設定情報が不正の場合
	 * @throws IOException 入出力でエラーが発生した場合
	 * @throws IllegalAccessException {@code clazz}が対応していない場合
	 * @throws InstantiationException 指定されたclazzが抽象クラス、インタフェース、配列クラス、プリミティブ型、またはvoidを表す場合、クラスが引数なしのコンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws ParseException
	 */
	public T read() throws IllegalStateException, IOException, InstantiationException, IllegalAccessException, ParseException {
		String str = reader.read();

		// ストリームの終わりに達している場合はnullを返却する。
		if (str == null || endOfFile) {
			endOfFile = true;
			return null;
		}

		// レコード数をインクリメント
		readCount++;

		// データ区分の判定
		String recordCordNum = FLTAnnotationManager.getRecordCodeNum(clazz);
		if (recordCordNum != null && !recordCordNum.isEmpty()
				&& !str.startsWith(recordCordNum)) {
			if (readCount == 1 && headerRecord == null) {
				headerRecord = str;

				return this.read();
			} else if (headerRecord != null && trailerRecord == null) {
				trailerRecord = str;
			}

			endOfFile = true;
			return null;
		}

		return annotationManager.convertToEntity(conf, clazz, str);
	}

	/**
	 * ヘッダレコードを返却します。
	 *
	 * ヘッダレコードが存在しない場合NULLを返却します。
	 * ※read()メソッドより先に実行された場合&1行目がヘッダレコードでなかった場合、
	 * read()メソッドでは1行目の取得はできず、2行目以降の取得になります。
	 *
	 * @param clazz ヘッダレコード格納先クラス
	 * @return 指定されたclazzのインスタンス
	 * @throws IllegalStateException 実行できる状態でない場合
	 * @throws IllegalArgumentException 指定されている設定情報が不正の場合
	 * @throws IOException 入出力でエラーが発生した場合
	 * @throws IllegalAccessException {@code clazz}が対応していない場合
	 * @throws InstantiationException 指定されたclazzが抽象クラス、インタフェース、配列クラス、プリミティブ型、またはvoidを表す場合、クラスが引数なしのコンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws ParseException
	 */
	public <T> T getHeader(Class<T> clazz) throws InstantiationException, IllegalAccessException, IndexOutOfBoundsException, IllegalStateException, IOException, ParseException {
		if ((readCount > 0 && headerRecord == null)) {
			return null;
		} else if(headerRecord == null) {
			headerRecord = reader.read();

			if (headerRecord == null) {
				endOfFile = true;
				return null;
			}
			readCount++;
		}
		if (headerRecord.startsWith(FLTAnnotationManager.getRecordCodeNum(clazz))) {
			return annotationManager.convertToEntity(conf, clazz, headerRecord);
		}
		return null;
	}

	/**
	 * トレーラレコードを返却します。
	 * トレーラレコードが存在しない場合NULLを返却します。
	 *
	 * ファイルの読み込みが全て完了していない場合、IllegalStateExceptionをスローします。
	 *
	 * @param clazz トレーラレコード格納先クラス
	 * @return 指定されたclazzのインスタンス
	 * @throws IllegalStateException 実行できる状態でない場合
	 * @throws IllegalArgumentException 指定されている設定情報が不正の場合
	 * @throws IOException 入出力でエラーが発生した場合
	 * @throws IllegalAccessException {@code clazz}が対応していない場合
	 * @throws InstantiationException 指定されたclazzが抽象クラス、インタフェース、配列クラス、プリミティブ型、またはvoidを表す場合、クラスが引数なしのコンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws ParseException
	 */
	public <T> T getTrailer(Class<T> clazz) throws InstantiationException, IllegalAccessException, IndexOutOfBoundsException, UnsupportedEncodingException, ParseException {
		if (!endOfFile) {
			throw new IllegalStateException("file not complete read.");
		} else if (trailerRecord == null || trailerRecord.isEmpty()
				|| trailerRecord.startsWith(FLTAnnotationManager.getRecordCodeNum(clazz))) {
			return null;
		}

		return annotationManager.convertToEntity(conf, clazz, trailerRecord);
	}

	/**
	 * 読み込み後の終了処理です。
	 * @throws IOException
	 *
	 */
	@Override
	public void close() throws IOException {
		reader.close();
	}

}
