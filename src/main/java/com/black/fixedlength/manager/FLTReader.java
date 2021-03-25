package com.black.fixedlength.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

import com.black.fixedlength.FLTConfig;
import com.black.fixedlength.exception.FixedLengthFormatException;

/**
 * 固定長ファイルを読み込む処理を提供します。
 *
 */
public class FLTReader implements AutoCloseable {
	private FLTConfig conf;
	private int recodeSize;

	private BufferedReader reader;

	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param conf 固定長形式情報
	 * @param inputPath 読み込み先ファイルパス
	 * @param recodeSize レコードサイズ
	 * @throws FileNotFoundException  ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public FLTReader(FLTConfig conf, Path inputPath, int recodeSize) throws FileNotFoundException, UnsupportedEncodingException, IllegalArgumentException {
		if (conf == null || inputPath == null || recodeSize == 0) {
			throw new IllegalArgumentException("Invalid argument specified.");
		}

		this.conf = conf;

		FileInputStream input = new FileInputStream(inputPath.toFile());
		InputStreamReader stream = new InputStreamReader(input,conf.getCharCode());
		this.reader = new BufferedReader(stream);
	}
	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param reader 読み込み用クラス
	 * @param conf 固定長形式情報
	 * @param inputPath 読み込み先ファイルパス
	 * @throws FileNotFoundException  ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public FLTReader(BufferedReader reader, FLTConfig conf, Path inputPath, int recodeSize) throws FileNotFoundException, UnsupportedEncodingException, IllegalArgumentException {
		if (conf == null || inputPath == null || recodeSize == 0) {
			throw new IllegalArgumentException("Invalid argument specified.");
		}

		this.conf = conf;
		this.reader = reader;
	}


	/**
	 * 1行を読み込み返却します。
	 * 指定されているrecodeSizeより数値が大きい場合は範囲のデータのみ取得します。
	 * 指定されているrecodeSizeより数値が小さい場合はIOExceptionをスローします。
	 *
	 * @return 行の内容を含む文字列、ただし行の終端文字は含めない。ストリームの終わりに達している場合はnull
	 * @throws IOException 入出力でエラーが発生した場合
	 */
	public String read() throws IOException {
		if (reader == null) {
			throw new IllegalStateException("it is already closed.");
		}

		// 戻り値
		String ret = null;

		// 1行読み込む
		String str = reader.readLine();

		// ストリームの終わりに達している場合はnullを返却する。
		if (str == null) {
			return null;
		} else if (str.isEmpty() && conf.isIgnoreblankLines()) {
			// 空行を読み飛ばす設定をしていた場合、値が取得できるまで読み込む
			while((str = reader.readLine()) != null) {
				if (!str.isEmpty()) {
					break;
				}
			}
			if (str == null || str.isEmpty()) {
				return null;
			}
		}

		switch (conf.getFltType()) {
		case BYTE :
			byte[] byt = str.getBytes(conf.getCharCode());

			if (byt.length < recodeSize) {
				throw new FixedLengthFormatException("The number of bytes in the record is not met.");
			}
			ret = new String(str.getBytes(conf.getCharCode()), 0, recodeSize, conf.getCharCode());
			break;

		case STRING :
		default:
			if (str.length() < recodeSize) {
				throw new FixedLengthFormatException("The number of characters in the record is not satisfied.");
			}
			ret = str.substring(0, recodeSize);
			break;
		}

		return ret;
	}

	/**
	 * 読み込み後の終了処理です。
	 * @throws IOException 入出力でエラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
			reader = null;
		}
	}

}
