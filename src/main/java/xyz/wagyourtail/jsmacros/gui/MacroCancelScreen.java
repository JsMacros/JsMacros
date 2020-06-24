package xyz.wagyourtail.jsmacros.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public class MacroCancelScreen extends Screen {
    private Screen parent;
    private HashMap<Thread, ButtonWidget> buttons = new HashMap<>();
    
    protected MacroCancelScreen(Screen parent) {
        super(new TranslatableText("jsmacros.stopmacro"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        client.keyboard.enableRepeatEvents(true);
        
        for (ArrayList<Thread> a : RunScript.threads.values()) {
            for (Thread t : a) {
                this.addButtonThread(t, a);
            }
        }
    }
    
    
//    public static Comparator<Thread> threadCompare = new Comparator<Thread>() {
//        @Override
//        public int compare(Thread t, Thread s) {
//            return t.getName().compareTo(s.getName());
//        }
//    };
    
    private void addButtonThread(Thread t, ArrayList<Thread> a) {
        buttons.put(t, this.addButton((ButtonWidget)new ButtonWidget(0, 0, 200, 20, new LiteralText(t.getName()), (buttonWidget) -> {
            t.interrupt();
            a.remove(t);
            this.client.openScreen(new MacroCancelScreen(this.parent));
        })));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, this.title.getString(), this.width / 2, 20, 0xFFFFFF);
        int x = 5;
        int y = 34;
        
        for (ButtonWidget B : buttons.values()) {
            if (x + 205 > this.width) {
                y += 25; 
                x = 5;
            }
            B.x = x;
            x += 205;
            B.y = y;
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }
}