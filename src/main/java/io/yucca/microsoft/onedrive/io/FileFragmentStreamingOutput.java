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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileFragmentStreamingOutput, streams a fragment (defined by range) of a file
 * to the OutputStream.
 * 
 * @author yucca.io
 */
public class FileFragmentStreamingOutput implements StreamingOutput {

    private static final Logger LOG = LoggerFactory
        .getLogger(FileFragmentStreamingOutput.class);

    private final Range range;

    private final RandomAccessFile file;

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
        this.file = new RandomAccessFile(file.toFile(), "r");
        this.range = range;
    }

    /**
     * Stream a fragment to OutputStream
     * 
     * @param output OutputStream flushed but not closed after writing
     * @throws IOException
     */
    public void write(OutputStream output) throws IOException {
        try {
            this.file.seek(this.range.getLower());
            long pos = this.range.getLower();
            int bytes = 0;
            int buffer = 0;
            while ((bytes = file.read()) != -1) {
                output.write(bytes);
                // break out if upper range is reached
                if (pos == this.range.getUpper()) {
                    break;
                }
                pos++;
                buffer++;
            }
            LOG.debug("Bytes written: {}", buffer);
            output.flush();
        } finally {
            this.file.close();
        }
    }
}
