/**
 * Copyright 2015 Rob Sessink
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
package io.yucca.microsoft.onedrive.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import javax.ws.rs.core.StreamingOutput;

/**
 * FileFragmentStreamingOutput, streams a fragment (defined by range) of a file
 * to the OutputStream.
 * <p>
 * Autocloseable is not yet implemented because this is only supported from
 * JAX-RS 2.1 and upwards. Jersey 2.2x currently uses JAX-RS 2.0.x.
 * </p>
 * 
 * @author yucca.io
 */
public class FileFragmentStreamingOutput implements StreamingOutput {

    private final Range range;

    private final RandomAccessFile raf;

    /**
     * Constructor
     * 
     * @param file File from which a fragment is streamed, the file is closed
     *            after {@link #write(OutputStream)}
     * @param range Range range to stream
     * @throws FileNotFoundException if file does not exist
     */
    public FileFragmentStreamingOutput(Path file, Range range)
        throws FileNotFoundException {
        this.raf = new RandomAccessFile(file.toFile(), "r");
        this.range = range;
    }

    /**
     * Stream a fragment to OutputStream
     * 
     * @param output OutputStream flushed but not closed after writing
     * @throws IOException
     */
    @Override
    public void write(OutputStream output) throws IOException {
        try {
            raf.seek(range.getLower());
            long pos = range.getLower();
            int bytes = 0;
            while ((bytes = raf.read()) != -1) {
                output.write(bytes);
                // break out if upper range is reached
                if (pos == range.getUpper()) {
                    break;
                }
                pos++;
            }
            output.flush();
        } finally {
            close();
        }
    }

    private void close() throws IOException {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                // do nothing; close should be idempotent
            }
        }

    }
}
