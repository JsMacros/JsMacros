package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.network.ClientPlayerEntity;

public class ClientPlayerEntityHelper extends PlayerEntityHelper {

	public ClientPlayerEntityHelper(ClientPlayerEntity e) {
		super(e);
	}

	public ClientPlayerEntity getRaw() {
        return (ClientPlayerEntity) e;
    }
}
