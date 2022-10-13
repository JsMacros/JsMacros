package xyz.wagyourtail.jsmacros.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.fabric.client.api.classes.CommandBuilderFabric;
import xyz.wagyourtail.jsmacros.fabric.client.api.classes.TextBuilderFabric;
import xyz.wagyourtail.jsmacros.fabric.client.gui.editor.hilighting.impl.TextStyleCompilerFabric;
import xyz.wagyourtail.jsmacros.fabric.client.api.classes.CommandManagerFabric;

public class JsMacrosFabric implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JsMacros.onInitializeClient();
    }

    @Override
    public void onInitialize() {
        JsMacros.onInitialize();

        // initialize loader-specific stuff
        CommandManager.instance = new CommandManagerFabric();
        TextBuilder.getTextBuilder = TextBuilderFabric::new;
        TextStyleCompiler.getTextStyleCompiler = TextStyleCompilerFabric::new;
        ClientTickEvents.END_CLIENT_TICK.register(TickBasedEvents::onTick);
        KeyBindingHelper.registerKeyBinding(JsMacros.keyBinding);
    }

}
