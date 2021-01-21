package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.client.font.*;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {
    
    @Shadow @Final private static Glyph SPACE;
    
    @Shadow @Final private List<Font> fonts;
    
    @Shadow @Final private Int2ObjectMap<IntList> charactersByWidth;
    
    @Shadow protected abstract void closeFonts();
    
    @Shadow protected abstract void closeGlyphAtlases();
    
    @Shadow @Final private Int2ObjectMap<GlyphRenderer> glyphRendererCache;
    @Shadow @Final private Int2ObjectMap<Glyph> glyphCache;
    @Shadow private GlyphRenderer blankGlyphRenderer;
    
    
    @Shadow private GlyphRenderer whiteRectangleGlyphRenderer;
    
    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);
    
    @Shadow @Final private static EmptyGlyphRenderer EMPTY_GLYPH_RENDERER;
    
    /**
     * @author Wagyourtail
     */
    @Overwrite
    public void setFonts(List<Font> fonts) {
        this.closeFonts();
        this.closeGlyphAtlases();
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
        this.whiteRectangleGlyphRenderer = this.getGlyphRenderer(WhiteRectangleGlyph.INSTANCE);
        IntSet intSet = new IntOpenHashSet();
    
        for (Font font : fonts) {
            intSet.addAll(font.getProvidedGlyphs());
        }
    
        Set<Font> set = Sets.newHashSet();
        intSet.forEach((IntConsumer) (i) -> {
    
            for (Font font : fonts) {
                Glyph glyph = i == 32 && !(font instanceof TrueTypeFont) ? SPACE : font.getGlyph(i);
                if (glyph != null) {
                    set.add(font);
                    if (glyph != BlankGlyph.INSTANCE) {
                        (this.charactersByWidth.computeIfAbsent(MathHelper.ceil((glyph).getAdvance(false)), (ix) -> new IntArrayList())).add(i);
                    }
                    break;
                }
            }
        
        });
        Stream<Font> var10000 = fonts.stream();
        var10000 = var10000.filter(set::contains);
        List<Font> var10001 = this.fonts;
        var10000.forEach(var10001::add);
    }
    
    /**
     * @author Wagyourtail
     */
    @Overwrite
    public Glyph getGlyph(int i) {
        return this.glyphCache.computeIfAbsent(i, (ix) -> {
            Glyph g = this.getRenderableGlyph(ix);
            if (g == null) return SPACE;
            return g;
        });
    }
    
    /**
     * @author Wagyourtail
     */
    @Overwrite
    private RenderableGlyph getRenderableGlyph(int i) {
        Iterator<Font> var2 = this.fonts.iterator();
        
        RenderableGlyph renderableGlyph;
        do {
            if (!var2.hasNext()) {
                return BlankGlyph.INSTANCE;
            }
            Font font = var2.next();
            if (i == 32 && !(font instanceof TrueTypeFont)) return null;
            renderableGlyph = font.getGlyph(i);
        } while(renderableGlyph == null);
        
        return renderableGlyph;
    }
    
    /**
     * @author Wagyourtail
     */
    @Overwrite
    public GlyphRenderer getGlyphRenderer(int i) {
        return this.glyphRendererCache.computeIfAbsent(i, (ix) -> ix == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph(ix)));
    }
}
