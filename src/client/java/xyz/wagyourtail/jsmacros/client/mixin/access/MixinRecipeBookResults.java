package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookResults;

import java.util.List;

@Mixin(RecipeBookResults.class)
public class MixinRecipeBookResults implements IRecipeBookResults {

    @Shadow
    private List<RecipeResultCollection> resultCollections;

    @Override
    public List<RecipeResultCollection> jsmacros_getResultCollections() {
        return resultCollections;
    }

}
