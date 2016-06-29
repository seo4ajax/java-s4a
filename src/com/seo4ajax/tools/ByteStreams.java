/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seo4ajax.tools;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteStreams {

	public static long copy(InputStream from, OutputStream to) throws IOException {
		byte[] buf = new byte[4096];
		long total = 0L;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}

	public static InputStream limit(InputStream in, long limit) {
		return new LimitedInputStream(in, limit);
	}

	public static int read(InputStream in, byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException("len is negative");
		}
		int total = 0;
		while (total < len) {
			int result = in.read(b, off + total, len - total);
			if (result == -1) {
				break;
			}
			total += result;
		}
		return total;
	}

	private static final class LimitedInputStream extends FilterInputStream {
		private long left;
		private long mark = -1L;

		LimitedInputStream(InputStream in, long limit) {
			super(in);
			this.left = limit;
		}

		public int available() throws IOException {
			return (int) Math.min(this.in.available(), this.left);
		}

		public synchronized void mark(int readLimit) {
			this.in.mark(readLimit);
			this.mark = this.left;
		}

		public int read() throws IOException {
			if (this.left == 0L) {
				return -1;
			}

			int result = this.in.read();
			if (result != -1) {
				this.left -= 1L;
			}
			return result;
		}

		public int read(byte[] b, int off, int len) throws IOException {
			if (this.left == 0L) {
				return -1;
			}

			len = (int) Math.min(len, this.left);
			int result = this.in.read(b, off, len);
			if (result != -1) {
				this.left -= result;
			}
			return result;
		}

		public synchronized void reset() throws IOException {
			if (!(this.in.markSupported())) {
				throw new IOException("Mark not supported");
			}
			if (this.mark == -1L) {
				throw new IOException("Mark not set");
			}

			this.in.reset();
			this.left = this.mark;
		}

		public long skip(long n) throws IOException {
			n = Math.min(n, this.left);
			long skipped = this.in.skip(n);
			this.left -= skipped;
			return skipped;
		}
	}
}