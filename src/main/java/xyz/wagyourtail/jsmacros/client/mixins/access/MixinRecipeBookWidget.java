package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookWidget;

@Mixin(RecipeBookWidget.class)
public class MixinRecipeBookWidget implements IRecipeBookWidget {
    @Shadow
    @Final
    private RecipeBookResults recipesArea;
    
    
    @Override
    public RecipeBookResults getResults() {
        return recipesArea;
    }
    
}
