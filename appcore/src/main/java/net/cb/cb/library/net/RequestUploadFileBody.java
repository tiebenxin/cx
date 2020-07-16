package net.cb.cb.library.net;

import io.reactivex.annotations.Nullable;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by LL130386 on 2017/12/22.
 */

public class RequestUploadFileBody extends RequestBody {

    private RequestBody mRequestBody;
    private FileUploadObserver<ResponseBody> fileUploadObserver;
    private BufferedSink bufferedSink;

    public RequestUploadFileBody(RequestBody body,
        FileUploadObserver<ResponseBody> fileUploadObserver) {
        this.mRequestBody = body;
        this.fileUploadObserver = fileUploadObserver;
    }


    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);
        //写入
        mRequestBody.writeTo(bufferedSink);

        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0L;
        private long contentLength = 0L;


        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (contentLength == 0) {
                contentLength = contentLength();
            }
            bytesWritten += byteCount;
            if (fileUploadObserver != null) {
                fileUploadObserver.onProgressChange(bytesWritten, contentLength);
            }
        }
    }
}
