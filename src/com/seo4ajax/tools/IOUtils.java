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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		copy(inputStream, outputStream, true);
	}

	public static void copy(InputStream inputStream, OutputStream outputStream, boolean closeInputStream) throws IOException {
		try {
			ByteStreams.copy(inputStream, outputStream);

			if (closeInputStream)
				inputStream.close();
		} finally {
			if (closeInputStream)
				inputStream.close();
		}
	}

}