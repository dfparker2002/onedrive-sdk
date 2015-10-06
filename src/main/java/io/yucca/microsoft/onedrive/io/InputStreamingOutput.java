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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.StreamingOutput;

/**
 * InputStreamingOutput, streams the content from the InputStream to
 * OutputStream without loading complete content in RAM.
 * <p>
 * Autocloseable is not yet implemented because this is only supported from
 * JAX-RS 2.1 and upwards. Jersey 2.2x currently uses JAX-RS 2.0.x.
 * </p>
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
public class InputStreamingOutput implements StreamingOutput {

    private final InputStream input;

    /**
     * InputStreamingOutput
     * 
     * @param input InputStream, stream to read from, closed after writing
     */
    public InputStreamingOutput(InputStream input) {
        this.input = input;
    }

    /**
     * Streams the content from the InputStream, so complete content is not
     * loaded into RAM. The stream is flushed every 4KB. The InputStream is
     * closed after writing, the OutputStream must be closed by the caller
     * <p>
     * bucket based on http://www.javapractices.com/topic/TopicAction.do?Id=246
     * </p>
     * 
     * @param output OutputStream flushed but not closed after writing
     * @throws IOException
     */
    @Override
    public void write(OutputStream output) throws IOException {
        try {
            // buffer size set to 4KB
            byte[] bucket = new byte[4 * 1024];
            int bytesRead = 0;
            while ((bytesRead = input.read(bucket)) != -1) {
                output.write(bucket, 0, bytesRead);
                output.flush();
            }
            output.flush();
        } finally {
            close();
        }
    }

    private void close() throws IOException {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                // do nothing; close should be idempotent
            }
        }
    }
}
