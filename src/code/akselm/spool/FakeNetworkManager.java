package code.akselm.spool;

import net.citizensnpcs.nms.v1_10_R1.network.EmptyNetworkManager;
import net.minecraft.server.v1_10_R1.EnumProtocolDirection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by axel on 8/3/2016.
 */
public class FakeNetworkManager extends EmptyNetworkManager {
    public FakeNetworkManager(EnumProtocolDirection flag) throws IOException {
        super(flag);
    }

    public SocketAddress getSocketAddress(){
        return new InetSocketAddress(0);
    }
}
