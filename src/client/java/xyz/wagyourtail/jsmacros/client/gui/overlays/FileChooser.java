package xyz.wagyourtail.jsmacros.client.gui.overlays;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.ConfirmOverlay;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class FileChooser extends OverlayContainer {
    private File directory;
    private Text dirname;
    private File selected;
    public File root = JsMacrosClient.clientCore.config.macroFolder;
    private final List<fileObj> files = new ArrayList<>();
    private final Consumer<File> setFile;
    private final Consumer<File> editFile;
    private int topScroll;

    public FileChooser(int x, int y, int width, int height, TextRenderer textRenderer, File directory, File selected, IOverlayParent parent, Consumer<File> setFile, Consumer<File> editFile) {
        super(x, y, width, height, textRenderer, parent);
        this.setFile = setFile;
        this.directory = directory;
        this.selected = selected;
        this.editFile = editFile;
    }

    public void setDir(File dir) {
        for (fileObj f : files) {
            this.remove(f.btn);
        }
        files.clear();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                setDir(root);
                return;
            }
        }

        this.directory = dir;
        this.dirname = Text.literal("./" + root.getAbsoluteFile().toPath().relativize(dir.getAbsoluteFile().toPath()).toString().replaceAll("\\\\", "/"));

        if (!this.directory.equals(root)) {
            addFile(this.directory.getParentFile(), "..");
        }

        List<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        files.sort(new sortFile());
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

    @Override
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addDrawableChild(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("X"), (btn) -> this.close()));
        scroll = this.addDrawableChild(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));

        this.addDrawableChild(new Button(x + w * 5 / 6 + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.select"), (btn) -> {
            if (this.selected != null && this.setFile != null) {
                this.setFile.accept(this.selected);
                this.close();
            }
        }));

        this.addDrawableChild(new Button(x + w * 4 / 6 + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("selectWorld.edit"), (btn) -> {
            if (this.selected != null) {
                editFile.accept(selected);
            }
        }));

        this.addDrawableChild(new Button(x + w * 3 / 6 + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.rename"), (btn) -> {
            if (selected != null) {
                this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, Text.translatable("jsmacros.filename"), selected.getName(), this, (str) -> {
                    File f = new File(directory, str);
                    if (selected.renameTo(f)) {
                        this.setDir(directory);
                        this.selectFile(f);
                    }
                }));
            }
        }));

        this.addDrawableChild(new Button(x + w * 2 / 6 + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("selectWorld.delete"), (btn) -> {
            if (this.selected != null && this.selected.isFile()) {
                fileObj f = null;
                for (fileObj fi : files) {
                    if (fi.file.equals(this.selected)) {
                        f = fi;
                        break;
                    }
                }
                if (f != null) {
                    confirmDelete(f);
                }
            }
        }));

        this.addDrawableChild(new Button(x + w / 6 + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.new"), (btn) -> this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, Text.translatable("jsmacros.filename"), "", this, (str) -> {
            if (str.trim().equals("")) {
                return;
            }

            // has extension
            File f = new File(directory, str);
            if (JsMacrosClient.clientCore.extensions.getExtensionForFile(f) == null) {
                str += "." + JsMacrosClient.clientCore.extensions.getHighestPriorityExtension().defaultFileExtension();
                f = new File(directory, str);
            }
            try {
                f.createNewFile();
                this.setDir(directory);
                this.selectFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }))));

        this.addDrawableChild(new Button(x + 2, y + height - 14, w / 6, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.openfolder"), (btn) -> {
            Util.getOperatingSystem().open(directory);
        }));

        this.setDir(directory);
        if (selected != null) {
            this.selectFile(selected);
        }
    }

    public void addFile(File f) {
        addFile(f, f.getName());
    }

    public void addFile(File f, String btnText) {
        fileObj file = new fileObj(f, new Button(x + 3 + (files.size() % 5 * (width - 12) / 5), topScroll + (files.size() / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, f.isDirectory() ? 0xFFFFFF00 : 0xFFFFFFFF, Text.literal(btnText), (btn) -> {
            selectFile(f);
        }));
        file.btn.visible = topScroll + (files.size() / 5 * 12) >= y + 13 && topScroll + (files.size() / 5 * 12) <= y + height - 27;
        files.add(file);
        this.addDrawableChild(file.btn);
        scroll.setScrollPages((Math.ceil(files.size() / 5D) * 12) / (double) Math.max(1, height - 39));
    }

    public void updateFilePos() {
        for (int i = 0; i < files.size(); ++i) {
            fileObj f = files.get(i);
            f.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            f.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
        }
    }

    public void confirmDelete(fileObj f) {
        this.openOverlay(new ConfirmOverlay(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, Text.translatable("jsmacros.confirmdeletefile"), this, (conf) -> {
            delete(f);
        }));
    }

    public void delete(fileObj f) {
        remove(f.btn);
        files.remove(f);
        f.file.delete();
        updateFilePos();
    }

    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (fileObj fi : files) {
            fi.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        drawContext.drawWrappedText(textRenderer, this.dirname, x + 3, y + 3, width - 14, 0xFFFFFFFF, false);

        drawContext.fill(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        drawContext.fill(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(drawContext, mouseX, mouseY, delta);

        for (ClickableWidget b : ImmutableList.copyOf(this.buttons)) {
            if (b instanceof Button && ((Button) b).hovering && ((Button) b).cantRenderAllText()) {
                // border
                int width = textRenderer.getWidth(b.getMessage());
                drawContext.fill(mouseX - 3, mouseY, mouseX + width + 3, mouseY + 1, 0x7F7F7F7F);
                drawContext.fill(mouseX + width + 2, mouseY - textRenderer.fontHeight - 3, mouseX + width + 3, mouseY, 0x7F7F7F7F);
                drawContext.fill(mouseX - 3, mouseY - textRenderer.fontHeight - 3, mouseX - 2, mouseY, 0x7F7F7F7F);
                drawContext.fill(mouseX - 3, mouseY - textRenderer.fontHeight - 4, mouseX + width + 3, mouseY - textRenderer.fontHeight - 3, 0x7F7F7F7F);

                // fill
                drawContext.fill(mouseX - 2, mouseY - textRenderer.fontHeight - 3, mouseX + width + 2, mouseY, 0xFF000000);
                drawContext.drawTextWithShadow(textRenderer, b.getMessage(), mouseX, mouseY - textRenderer.fontHeight - 1, 0xFFFFFFFF);
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
        @Override
        public int compare(File a, File b) {
            if (a.isDirectory() ^ b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            } else {
                return a.getName().compareTo(b.getName());
            }
        }

    }

}
