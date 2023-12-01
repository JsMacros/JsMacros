package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Sorting;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;

import static net.minecraft.client.gui.DrawableHelper.fill;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.translatable;

public class MacroListTopbar extends MultiElementContainer<MacroScreen> {
    public ScriptTrigger.TriggerType deftype;
    private Button type;

    public MacroListTopbar(MacroScreen parent, int x, int y, int width, int height, TextRenderer textRenderer, ScriptTrigger.TriggerType deftype) {
        super(x, y, width, height, textRenderer, parent);
        this.deftype = deftype;
        init();
    }

    @Override
    public void init() {
        super.init();

        int w = width - 12;

        addDrawableChild(new Button(x + 1, y + 1, w / 12 - 1, height - 3, textRenderer, Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.Enabled ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, translatable("jsmacros.enabledstatus"), (btn) -> {
            Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.Enabled;
            parent.reload();
        }));

        type = addDrawableChild(new Button(x + w / 12 + 1, y + 1, (w / 4) - (w / 12) - 1, height - 3, textRenderer, Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.TriggerName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, translatable(deftype == ScriptTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"), (btn) -> {
            Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.TriggerName;
            parent.reload();
        }));

        addDrawableChild(new Button(x + (w / 4) + 1, y + 1, w * 3 / 4 - 3 - 30, height - 3, textRenderer, Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod == Sorting.MacroSortMethod.FileName ? 0x3FFFFFFF : 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, translatable("jsmacros.file"), (btn) -> {
            Core.getInstance().config.getOptions(ClientConfigV2.class).sortMethod = Sorting.MacroSortMethod.FileName;
            parent.reload();
        }));

        addDrawableChild(new Button(x + w - 32, y + 1, 30, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, translatable("jsmacros.run"), (btn) -> {
            parent.runFile();
        }));

        addDrawableChild(new Button(x + w - 1, y + 1, 11, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, literal("+"), (btn) -> {
            ScriptTrigger macro = new ScriptTrigger(deftype, "", Core.getInstance().config.macroFolder, false, false);
            Core.getInstance().eventRegistry.addScriptTrigger(macro);
            parent.addMacro(macro);
        }));
    }

    public void updateType(ScriptTrigger.TriggerType type) {
        this.deftype = type;
        this.type.setMessage(translatable(deftype == ScriptTrigger.TriggerType.EVENT ? "jsmacros.events" : "jsmacros.keys"));
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        fill(drawContext, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(drawContext, x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        fill(drawContext, x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        fill(drawContext, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(drawContext, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;

        fill(drawContext, x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        fill(drawContext, x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        fill(drawContext, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }

}
