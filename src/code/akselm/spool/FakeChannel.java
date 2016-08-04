package code.akselm.spool;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FakeChannel extends AbstractChannel {
    private final ChannelConfig config = new DefaultChannelConfig(this);
    private EventLoop loop;

    public FakeChannel(Channel parent, EventLoop loop) {
        super(parent);
        this.loop = loop;
    }


    public ChannelConfig config() {
        this.config.setAutoRead(true);
        return this.config;
    }

    public EventLoop eventLoop(){
        return loop;
    }
    public Unsafe unsafe(){
        return new Unsafe(){

            @Override
            public SocketAddress localAddress() {
                return null;
            }

            @Override
            public SocketAddress remoteAddress() {
                return new InetSocketAddress(0);
            }

            @Override
            public void register(EventLoop eventLoop, ChannelPromise channelPromise) {
                //
            }

            @Override
            public void bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
//
            }

            @Override
            public void connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
//
            }

            @Override
            public void disconnect(ChannelPromise channelPromise) {
//
            }

            @Override
            public void close(ChannelPromise channelPromise) {
//
            }

            @Override
            public void closeForcibly() {
//
            }

            @Override
            public void deregister(ChannelPromise channelPromise) {
//
            }

            @Override
            public void beginRead() {
//
            }

            @Override
            public void write(Object o, ChannelPromise channelPromise) {
//
            }

            @Override
            public void flush() {
//
            }

            @Override
            public ChannelPromise voidPromise() {
                return null;
            }

            @Override
            public ChannelOutboundBuffer outboundBuffer() {
                return null;
            }
        };
    }

    protected void doBeginRead() throws Exception {
    }

    protected void doBind(SocketAddress arg0) throws Exception {
    }

    protected void doClose() throws Exception {
    }

    protected void doDisconnect() throws Exception {
    }

    protected void doWrite(ChannelOutboundBuffer arg0) throws Exception {
    }

    public boolean isActive() {
        return false;
    }

    protected boolean isCompatible(EventLoop arg0) {
        return true;
    }

    public boolean isOpen() {
        return true;
    }

    protected SocketAddress localAddress0() {
        return null;
    }

    public ChannelMetadata metadata() {
        return null;
    }

    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    protected SocketAddress remoteAddress0() {
        return new InetSocketAddress(0);
    }
}
