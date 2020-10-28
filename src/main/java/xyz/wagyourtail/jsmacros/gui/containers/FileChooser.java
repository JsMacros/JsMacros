package xyz.wagyourtail.jsmacros.gui.containers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public class FileChooser extends OverlayContainer {
    private File directory;
    private Text dirname;
    private File selected;
    private List<fileObj> files = new ArrayList<>();
    private Consumer<File> setFile;
    private Consumer<Element> setFocused;
    private int topScroll;

    public FileChooser(int x, int y, int width, int height, TextRenderer textRenderer, File directory, File selected, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close,  Consumer<Element> setFocused, Consumer<File> setFile) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.setFile = setFile;
        this.directory = directory;
        this.selected = selected;
        this.setFocused = setFocused;
    }

    public void setDir(File dir) {
        for (fileObj f : files) {
            this.removeButton(f.btn);
        }
        files.clear();
        this.directory = dir;
        this.dirname = new LiteralText("." + dir.getAbsolutePath().substring(JsMacros.config.macroFolder.getAbsolutePath().length()).replaceAll("\\\\", "/"));

        if (!this.directory.equals(JsMacros.config.macroFolder)) {
            addFile(this.directory.getParentFile(), "..");
        }

        List<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        Collections.sort(files, new sortFile());
        for (File f : files) {
            addFile(f);
        }
    }

    public void selectFile(File f) {
        if (f.isDirectory()) {
            this.setDir(f);
        } else {
            this.selected = f;
        }
        for (fileObj fi : files) {
            if (f.equals(fi.file)) {
                fi.btn.setColor(0x7FFFFFFF);
            } else {
                fi.btn.setColor(0);
            }
        }
    }

    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            this.close();
        }));
        scroll = (Scrollbar) this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));

        this.addButton(new Button(x + w * 5 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.select"), (btn) -> {
            if (this.selected != null && this.setFile != null) {
                this.setFile.accept(this.selected);
                this.close();
            }
        }));

        this.addButton(new Button(x + w * 4 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("selectWorld.edit"), (btn) -> {
            if (this.selected != null) Util.getOperatingSystem().open(this.selected);
        }));

        this.addButton(new Button(x + w * 3 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.rename"), (btn) -> {
            if (selected != null) {
                this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.filename"), selected.getName(), addButton, removeButton, this::closeOverlay, setFocused, (str) -> {
                    File f = new File(directory, str);
                    if (selected.renameTo(f)) {
                        this.setDir(directory);
                        this.selectFile(f);
                    }
                }));
            }
        }));

        this.addButton(new Button(x + w * 2 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("selectWorld.delete"), (btn) -> {
            if (this.selected != null && this.selected.isFile()) {
                fileObj f = null;
                for (fileObj fi : files) {
                    if (fi.file.equals(this.selected)) {
                        f = fi;
                        break;
                    }
                }
                if (f != null) confirmDelete(f);
            }
        }));

        this.addButton(new Button(x + w * 1 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.new"), (btn) -> {
            this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.filename"), "", addButton, removeButton, this::closeOverlay, setFocused, (str) -> {
                if (str.trim().equals("")) return;
                boolean edit = true;
                for (ILanguage language : RunScript.languages) {
                    if (str.endsWith(language.extension())) {
                        edit = false;
                        break;
                    }
                }
                if (edit) {
                    str += RunScript.defaultLang.extension();
                }
                File f = new File(directory, str);
                try {
                    f.createNewFile();
                    this.setDir(directory);
                    this.selectFile(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }));

        this.addButton(new Button(x + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.openfolder"), (btn) -> {
            Util.getOperatingSystem().open(directory);
        }));

        this.setDir(directory);
        if (selected != null) this.selectFile(selected);
    }

    public void addFile(File f) {
        addFile(f, f.getName());
    }

    public void addFile(File f, String btnText) {
        fileObj file = new fileObj(f, new Button(x + 3 + (files.size() % 5 * (width - 12) / 5), topScroll + (files.size() / 5 * 12), (width - 12) / 5, 12, 0, 0, 0x7FFFFFFF, f.isDirectory() ? 0xFFFF00 : 0xFFFFFF, new LiteralText(btnText), (btn) -> {
            selectFile(f);
        }));
        if (topScroll + (files.size() / 5 * 12) < y + 13 || topScroll + (files.size() / 5 * 12) > y + height - 27) file.btn.visible = false;
        else file.btn.visible = true;
        files.add(file);
        this.addButton(file.btn);
        scroll.setScrollPages((Math.ceil(files.size() / 5D) * 12) /(double) Math.max(1, height - 39));
    }

    public void updateFilePos() {
        for (int i = 0; i < files.size(); ++i) {
            fileObj f = files.get(i);
            if (topScroll + (i / 5 * 12) < y + 13 || topScroll + (i / 5 * 12) > y + height - 27) f.btn.visible = false;
            else f.btn.visible = true;
            f.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
        }
    }

    public void confirmDelete(fileObj f) {
        this.openOverlay(new ConfirmOverlay(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.confirmdeletefile"), addButton, removeButton, this::closeOverlay, (conf) -> {
            delete(f);
        }));
    }

    public void delete(fileObj f) {
        removeButton(f.btn);
        files.remove(f);
        f.file.delete();
        updateFilePos();
    }

    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (fileObj fi : files) {
            if (topScroll + (i / 5 * 12) < y + 13 || topScroll + (i / 5 * 12) > y + height - 27) fi.btn.visible = false;
            else fi.btn.visible = true;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }

    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        renderBackground(matricies);

        textRenderer.drawTrimmed(this.dirname, x + 3, y + 3, width - 14, 0xFFFFFF);

        fill(matricies, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(matricies, x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(matricies, mouseX, mouseY, delta);
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            if (((Button) b).hovering && !((Button) b).canRenderAllText()) {
                // border
                int width = textRenderer.getWidth(b.getMessage());
                fill(matricies, mouseX-3, mouseY, mouseX+width+3, mouseY+1, 0x7F7F7F7F);
                fill(matricies, mouseX+width+2, mouseY-textRenderer.fontHeight - 3, mouseX+width+3, mouseY, 0x7F7F7F7F);
                fill(matricies, mouseX-3, mouseY-textRenderer.fontHeight - 3, mouseX-2, mouseY, 0x7F7F7F7F);
                fill(matricies, mouseX-3, mouseY-textRenderer.fontHeight - 4, mouseX+width+3, mouseY-textRenderer.fontHeight - 3, 0x7F7F7F7F);
                
                // fill
                fill(matricies, mouseX-2, mouseY-textRenderer.fontHeight - 3, mouseX+width+2, mouseY, 0xFF000000);
                drawTextWithShadow(matricies, textRenderer, b.getMessage(), mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
            }
        }
    }

    public static class fileObj {
        public File file;
        public Button btn;

        public fileObj(File file, Button btn) {
            this.file = file;
            this.btn = btn;
        }
    }

    public static class sortFile implements Comparator<File> {
        public int compare(File a, File b) {
            if (a.isDirectory() ^ b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            } else {
                return a.getName().compareTo(b.getName());
            }
        }
    }
}
