package xyz.wagyourtail.jsmacros.client.api.classes.filter.compare;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;

public class CharCompareFilter implements IFilter<Character> {

    private final char compareTo;

    public CharCompareFilter(char compareTo) {
        this.compareTo = compareTo;
    }

    @Override
    public Boolean apply(Character character) {
        return character == compareTo;
    }
}
