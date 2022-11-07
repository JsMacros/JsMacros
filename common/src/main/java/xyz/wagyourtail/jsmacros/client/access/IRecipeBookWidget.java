package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.recipebook.ClientRecipeBook;

public interface IRecipeBookWidget {

    RecipeBookResults jsmacros_getResults();
    
    boolean jsmacros_isSearching();
    
    void jsmacros_refreshResultList();

    ClientRecipeBook jsmacros_getRecipeBook();
    
}
