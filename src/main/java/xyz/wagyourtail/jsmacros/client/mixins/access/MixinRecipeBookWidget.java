package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookWidget;

@Mixin(RecipeBookWidget.class)
public abstract class MixinRecipeBookWidget implements IRecipeBookWidget {
    @Shadow
    @Final
    private RecipeBookResults recipesArea;
    
    @Shadow private boolean searching;
    
    @Shadow protected abstract void refreshResults(boolean resetCurrentPage);
    
    @Override
    public RecipeBookResults getResults() {
        return recipesArea;
    }
    
    @Override
    public boolean isSearching() {
        return searching;
    }
    
    @Override
    public void refreshResultList() {
        refreshResults(false);
    }
}
