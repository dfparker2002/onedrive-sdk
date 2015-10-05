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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.ws.rs.core.StreamingOutput;

/**
 * NIOInputStreamingOutput, streams the content from the InputStream to
 * OutputStream using Java NIO
 * 
 * <pre>
 * TODO create a FileInputStreamOutput based on, because InputStream does not support length.
 * https://stackoverflow.com/questions/14410344/jersey-rest-support-resume-media-streaming
 * https://stackoverflow.com/questions/11584791/jersey-client-upload-progress
 * https://stackoverflow.com/questions/3474911/changing-the-index-positioning-in-inputstream
 * https://plus.google.com/+rkalla/posts/EvtUW2x3Qc8
 * </pre>
 * 
 * @author yucca.io
 */
public class NIOInputStreamingOutput implements StreamingOutput, Closeable {

    private final InputStream input;

    /**
     * InputStreamingOutput
     * 
     * @param input InputStream, stream to read from, closed after writing
     */
    public NIOInputStreamingOutput(InputStream input) {
        this.input = input;
    }

    /**
     * Streams the content from the InputStream. The InputStream is closed after
     * writing, the OutputStream must be closed by the caller
     * <p>
     * bucket based on http://www.javapractices.com/topic/TopicAction.do?Id=246
     * </p>
     * 
     * @param output OutputStream flushed but not closed after writing
     * @throws IOException
     */
    @Override
    public void write(OutputStream output) throws IOException {
        ReadableByteChannel in = Channels.newChannel(input);
        WritableByteChannel out = Channels.newChannel(output);
        ByteBuffer buf = ByteBuffer.allocateDirect(64 * 1024);
        while ((in.read(buf)) != -1) {
            // prepare the buffer to be drained
            buf.flip();
            // write to the channel, may block
            out.write(buf);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buf.compact();
        }
        // EOF will leave buffer in fill state
        buf.flip();
        // make sure the buffer is fully drained.
        while (buf.hasRemaining()) {
            out.write(buf);
        }
        output.flush();
    }

    @Override
    public void close() throws IOException {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                // do nothing; close should be idempotent
            }
        }
    }
}
