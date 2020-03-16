package xyz.wagyourtail.jsmacros.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
        minecraft.keyboard.enableRepeatEvents(true);
        
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
        buttons.put(t, this.addButton((ButtonWidget)new ButtonWidget(0, 0, 200, 20, t.getName(), (buttonWidget) -> {
            t.interrupt();
            a.remove(t);
            this.minecraft.openScreen(new MacroCancelScreen(this.parent));
        })));
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
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
        
        super.render(mouseX, mouseY, delta);
    }
    
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
}