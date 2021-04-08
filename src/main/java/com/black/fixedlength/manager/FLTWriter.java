package com.black.fixedlength.manager;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

import com.black.fixedlength.FLTConfig;
import com.black.fixedlength.exception.FixedLengthFormatException;

public class FLTWriter implements AutoCloseable {

	private FLTConfig conf;

	private BufferedWriter writer;

	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param conf 固定長形式情報
	 * @param inputPath 書き込み先ファイルパス
	 * @throws FileNotFoundException  ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public FLTWriter(FLTConfig conf, Path outputPath) throws FileNotFoundException, UnsupportedEncodingException {
		if (conf == null || outputPath == null) {
			throw new IllegalArgumentException("Invalid argument specified.");
		}

		this.conf = conf;

		FileOutputStream input = new FileOutputStream(outputPath.toFile());
		OutputStreamWriter stream = new OutputStreamWriter(input,conf.getCharCode());
		this.writer = new BufferedWriter(stream);
	}
	/**
	 * 指定された固定長形式情報を使用して、構築するコンストラクタです。
	 *
	 * @param writer 書き込み用クラス
	 * @param conf 固定長形式情報
	 * @param inputPath 書き込み先ファイルパス
	 * @throws FileNotFoundException  ファイルが存在しないか、通常ファイルではなくディレクトリであるか、またはなんらかの理由で開くことができない場合。
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public FLTWriter(BufferedWriter writer, FLTConfig conf, Path outputPath) throws FileNotFoundException, UnsupportedEncodingException {
		if (conf == null || outputPath == null) {
			throw new IllegalArgumentException("Invalid argument specified.");
		}

		this.conf = conf;
		this.writer = writer;
	}

	/**
	 * 指定された一行を書き込みます。
	 * 指定されているrecordSizeより数値が一致しない場合はIOExceptionをスローします。
	 *
	 * @throws IOException 入出力でエラーが発生した場合
	 */
	public void write(String str, int recordSize) throws IOException {
		if (writer == null) {
			throw new IllegalStateException("it is already closed.");
		}

		switch (conf.getFltType()) {
		case BYTE :
			byte[] byt = str.getBytes(conf.getCharCode());

			if (byt.length != recordSize) {
				throw new FixedLengthFormatException("The number of bytes in the record is not met.");
			}
			break;

		case STRING :
		default:
			if (str.length() != recordSize) {
				throw new FixedLengthFormatException("The number of characters in the record is not satisfied.");
			}
			break;
		}
		writer.write(str);
		writer.newLine();
	}

	/**
	 * 書き込み後の終了処理です。
	 * @throws IOException 入出力でエラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

}
