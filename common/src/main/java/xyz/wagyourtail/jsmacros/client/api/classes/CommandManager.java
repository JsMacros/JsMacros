package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @since 1.7.0
 */
public abstract class CommandManager {

    public static CommandManager instance;

    private TextFieldWidget text = new TextFieldWidget(-1, null, 0, 0, 0, 0);
    private MethodWrapper<List<String>, Object, Object, ?> callback;
    public PathNodeMaker pathNodeMaker = new PathNodeMaker(text, false) {

        @Override
        public void method_12183() {
            field_13325 = false;
            super.method_12183();
        }

        @Nullable
        @Override
        public BlockPos method_12186() {
            return null;
        }

        @Override
        public void method_12185(String... strings) {
            if (callback != null) {
                callback.accept(Arrays.asList(strings));
            }
        }
    };

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @since 1.7.0
     * @return list of commands
     */
    public List<String> getValidCommands() {
        return ImmutableList.of();
    }

    /**
     * @param name
     * @since 1.7.0
     * @return
     */
    public abstract CommandBuilder createCommandBuilder(String name);

    /**
     * @param command
     *
     * @return
     * @since 1.7.0
     * @throws IllegalAccessException
     */
    public abstract CommandNodeHelper unregisterCommand(String command) throws IllegalAccessException;

    /**
     * warning: this method is hacky
     * @since 1.7.0
     * @param node
     */
    public abstract void reRegisterCommand(CommandNodeHelper node);

    /**
     * @since 1.8.2
     * @param commandPart
     */
    public void getArgumentAutocompleteOptions(String commandPart, MethodWrapper<List<String>, Object, Object, ?> callback) {
        assert mc.player != null;
        text.setText(commandPart);
        this.callback = callback;
        pathNodeMaker.method_12183();
    }
}
