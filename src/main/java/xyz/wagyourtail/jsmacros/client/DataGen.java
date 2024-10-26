package xyz.wagyourtail.jsmacros.client;

import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.ParticleHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.UniversalBlockStateHelper;

import java.io.IOException;

public class DataGen {

    public static void main(String[] args) throws IOException {
        System.out.println("PacketByteBufferHelper: \n\n");
        PacketByteBufferHelper.main(new String[0]);
        System.out.println("\n\n");
        System.out.println("UniversalBlockStateHelper: \n\n");
        UniversalBlockStateHelper.main(new String[0]);
        System.out.println("\n\n");
        System.out.println("ParticleHelper: \n\n");
        ParticleHelper.main(new String[0]);
        System.out.println("\n\n");
        System.out.println();
    }

}
