package com.yanlong.im.utils.socket;

import com.yanlong.im.chat.tcp.SocketEndException;

import net.cb.cb.library.utils.LogUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


/**
 * a SocketChannel with TLS/SSL encryption
 *
 * @author Alexander Kout
 * @created 25. Mai 2005
 */

public class SSLSocketChannel2 {
    private static final String TAG = "SSLSocketChannel2";
    int SSL;
    ByteBuffer clientIn, clientOut, cTOs, sTOc, wbuf;
    SocketChannel sc = null;
    SSLEngineResult res;
    SSLEngine sslEngine;

    public SSLSocketChannel2() throws IOException {
        sc = SocketChannel.open();
    }

    //1.转换
    public SSLSocketChannel2(SocketChannel sc) {
        this.sc = sc;
    }

    //2.链接成功后启动 pssl=1
    public int tryTLS(int pSSL) {
        SSL = pSSL;
        if (SSL == 0)
            return 0;

        SSLContext sslContext = null;
        try {
            // create SSLContext
            sslContext = SSLContext.getInstance("TLS");
            //配置证书或者不配置
            sslContext.init(null, new TrustManager[]{new EasyX509TrustManager(null)}, null);

            // create Engine
            sslEngine = sslContext.createSSLEngine();
            // begin
            sslEngine.setUseClientMode(true);

            sslEngine.setEnableSessionCreation(true);
            SSLSession session = sslEngine.getSession();
            createBuffers(session);
            // wrap
            if (clientOut != null) {
                clientOut.clear();
                sc.write(wrap(clientOut));
                while (res.getHandshakeStatus() !=
                        SSLEngineResult.HandshakeStatus.FINISHED) {
                    if (res.getHandshakeStatus() ==
                            SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                        // unwrap
                        sTOc.clear();
                        int readindex = 0;
                        while (sc.read(sTOc) < 1) {
                            //TODO：线程睡眠200，修改于2020.5.12
                            Thread.sleep(20);
                            readindex++;
                            if (readindex > 100) {
                                return 0;
                            }

                        }

                        sTOc.flip();
                        unwrap(sTOc);
                        if (res.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.FINISHED) {
                            clientOut.clear();
                            sc.write(wrap(clientOut));
                        }
                    } else if (res.getHandshakeStatus() ==
                            SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                        // wrap
                        clientOut.clear();
                        sc.write(wrap(clientOut));
                    } else {
                        Thread.sleep(1000);
                    }
                }
                clientIn.clear();
                clientIn.flip();
                SSL = 4;

                LogUtil.getLog().i(TAG, "SSL established\n");
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            LogUtil.getLog().e(TAG, "连接LOG --SSL tryTLS的异常:" + e.toString());
            LogUtil.writeLog(TAG + "--连接LOG--" + "连接异常--" + e.getClass().getSimpleName() + "--errMsg=" + e.toString());
            SSL = 0;
            //  throw new NetworkErrorException();
        }
        return SSL;
    }

    private synchronized ByteBuffer wrap(ByteBuffer b) throws SSLException {
        cTOs.clear();
        res = sslEngine.wrap(b, cTOs);
        cTOs.flip();
        // LogUtil.getLog().i(TAG,"wrap:\n"+res.toString()+"\n");
        return cTOs;
    }

    private synchronized ByteBuffer unwrap(ByteBuffer b) throws SSLException {
        clientIn.clear();
        int pos;
        // LogUtil.getLog().i(TAG,"b.remaining "+b.remaining()+"\n");
        while (b.hasRemaining()) {
            // LogUtil.getLog().i(TAG,"b.remaining "+b.remaining()+"\n");
            res = sslEngine.unwrap(b, clientIn);
            // LogUtil.getLog().i(TAG,"unwrap:\n"+res.toString()+"\n");
            if (res.getHandshakeStatus() ==
                    SSLEngineResult.HandshakeStatus.NEED_TASK) {
// Task
                Runnable task;
                while ((task = sslEngine.getDelegatedTask()) != null) {
                    //  LogUtil.getLog().i(TAG,"task...\n");
                    task.run();
                }
                //  LogUtil.getLog().i(TAG,"task:\n"+res.toString()+"\n");
            } else if (res.getHandshakeStatus() ==
                    SSLEngineResult.HandshakeStatus.FINISHED) {
                return clientIn;
            } else if (res.getStatus() ==
                    SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                //   LogUtil.getLog().i(TAG,"underflow\n");
                //  LogUtil.getLog().i(TAG,"b.remaining "+b.remaining()+"\n");
                return clientIn;
            }
        }
        return clientIn;
    }

    private void createBuffers(SSLSession session) {

        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();

        clientIn = ByteBuffer.allocate(65536);
        clientOut = ByteBuffer.allocate(appBufferMax);
        wbuf = ByteBuffer.allocate(65536);

        cTOs = ByteBuffer.allocate(netBufferMax);
        sTOc = ByteBuffer.allocate(netBufferMax);

    }

    public synchronized int write(ByteBuffer src) throws IOException {
        if (SSL == 4) {
            return sc.write(wrap(src));
        }
        return sc.write(src);
    }

    public int read(ByteBuffer dst) throws Exception {
        //  LogUtil.getLog().i(TAG,"read\n");
        int amount = 0, limit;
        if (SSL == 4) {
// test if there was a buffer overflow in dst
            if (clientIn.hasRemaining()) {
                limit = Math.min(clientIn.remaining(), dst.remaining());
                for (int i = 0; i < limit; i++) {
                    dst.put(clientIn.get());
                    amount++;
                }
                return amount;
            }
// test if some bytes left from last read (e.g. BUFFER_UNDERFLOW)
            if (sTOc.hasRemaining()) {
                unwrap(sTOc);
                clientIn.flip();
                limit = Math.min(clientIn.limit(), dst.remaining());
                for (int i = 0; i < limit; i++) {
                    dst.put(clientIn.get());
                    amount++;
                }
                if (res.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    sTOc.clear();
                    sTOc.flip();
                    return amount;
                }
            }
            if (!sTOc.hasRemaining())
                sTOc.clear();
            else
                sTOc.compact();

            if (sc.read(sTOc) == -1) {
                LogUtil.getLog().i(TAG, "close from SSLSocketChannel2" + "\n");
                sTOc.clear();
                sTOc.flip();
                throw new SocketEndException();
            }
            sTOc.flip();
            unwrap(sTOc);
// write in dst
            clientIn.flip();
            limit = Math.min(clientIn.limit(), dst.remaining());
            for (int i = 0; i < limit; i++) {
                dst.put(clientIn.get());
                amount++;
            }
            //LogUtil.getLog().i(TAG,"dst.remaining "+dst.remaining()+"\n");
            return amount;
        }
        return sc.read(dst);
    }

    public boolean isConnected() {
        return sc.isConnected();
    }

    public void close() throws IOException {
        if (SSL == 4) {
            if (sslEngine != null) {
                sslEngine.closeOutbound();
            }
            if (clientOut != null) {
                clientOut.clear();
            }
            if (sc != null) {
                sc.write(wrap(clientOut));
                sc.close();
            }
        } else {
            if (sc != null) {
                sc.close();
            }
        }
    }

    public SelectableChannel configureBlocking(boolean b) throws IOException {
        return sc.configureBlocking(b);
    }

    public boolean connect(SocketAddress remote) throws IOException {
        return sc.connect(remote);
    }

    public boolean finishConnect() throws IOException {
        return sc.finishConnect();
    }

    public Socket socket() {
        return sc.socket();
    }

    public boolean isInboundDone() {
        return sslEngine.isInboundDone();
    }
}








