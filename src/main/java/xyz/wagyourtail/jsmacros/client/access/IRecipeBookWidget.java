package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;

public interface IRecipeBookWidget {

    RecipeBookResults jsmacros_getResults();
    
    boolean jsmacros_isSearching();
    
    void jsmacros_refreshResultList();
}
