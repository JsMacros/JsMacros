package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;

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

    @Shadow private ClientRecipeBook recipeBook;

    @Override
    public RecipeBookResults jsmacros_getResults() {
        return recipesArea;
    }
    
    @Override
    public boolean jsmacros_isSearching() {
        return searching;
    }
    
    @Override
    public void jsmacros_refreshResultList() {
        refreshResults(false);
    }

    @Override
    public ClientRecipeBook jsmacros_getRecipeBook() {
        return recipeBook;
    }
}