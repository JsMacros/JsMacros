package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public abstract class StateHelper<U extends State<?, ?>> extends BaseHelper<U> {

    public StateHelper(U base) {
        super(base);
    }

    /**
     * @return a map of the state properties with its identifier and value.
     * @since 1.8.4
     */
    public Map<String, String> toMap() {
        return base.getEntries().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), entry -> Util.getValueAsString(entry.getKey(), entry.getValue())));
    }

    public <T extends Comparable<?>> StateHelper<U> with(String property, String value) {
        Optional<Property<?>> prop = base.getProperties().stream().filter(p -> p.getName().equals(property)).findFirst();
        if (prop.isEmpty()) {
            throw new IllegalArgumentException("Property " + property + " does not exist for this state");
        }
        return with(prop.get(), value);
    }

    private <T extends Comparable<T>> StateHelper<U> with(Property<T> property, String value) {
        Optional<T> arg = property.parse(value);
        if (arg.isEmpty()) {
            throw new IllegalArgumentException("Value " + value + " is not valid for the property " + property);
        }
        return create((U) base.with(property, arg.get()));
    }

    protected abstract StateHelper<U> create(U base);

}
