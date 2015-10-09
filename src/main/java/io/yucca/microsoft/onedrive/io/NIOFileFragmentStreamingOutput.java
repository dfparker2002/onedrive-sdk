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
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
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
public class NIOFileFragmentStreamingOutput implements StreamingOutput {

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
    public NIOFileFragmentStreamingOutput(Path file, Range range)
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
        try (SeekableByteChannel in = raf.getChannel();
            WritableByteChannel out = Channels.newChannel(output)) {
            // allocate a buffer equal to range
            ByteBuffer buf = ByteBuffer
                .allocate(new Long(range.getLength()).intValue());
            // set position in file
            in.position(range.getLower());
            int read = 0;
            do {
                read = in.read(buf);
                // prepare the buffer to be drained
                buf.flip();
                // write to the channel, may block
                out.write(buf);
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buf.compact();
            } while (read < range.getLength() && read != -1);
            // EOF will leave buffer in fill state
            buf.flip();
            // make sure the buffer is fully drained.
            while (buf.hasRemaining()) {
                out.write(buf);
            }
            output.flush();
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
