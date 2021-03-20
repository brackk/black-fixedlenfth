package com.black.fixedlength.manager;

import com.black.fixedlength.format.TrimmingFormat;

/**
 *
 *
 */
public interface FLTLoader {

	/**
	 *
	 * @return
	 */
	public <T> FLTLoader trimming(TrimmingFormat format);

	public <T> FLTLoader filter();
}
