package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;    //changed to bidi//
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
private int connectionId;
    public BlockingConnectionHandler(
            Socket sock, MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol, Connections<T> connections, int connectionId) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connectionId= connectionId;
        protocol.start(connectionId, connections);
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {

                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    // (I think) protocol will process the message and than call Connections.send()
                    // inside the process() method.
                    // that's why "T process()" was changed to "void process"
                    //we get the protocol when it is started already,  actually not sure
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    public void send(T message) {

        try {
            out = new BufferedOutputStream(sock.getOutputStream());
            if (message != null) {
                synchronized (this) {
                    out.write(encdec.encode(message));
                    out.flush();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

