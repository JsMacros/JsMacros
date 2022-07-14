package xyz.wagyourtail.jsmacros.core.test;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.stubs.CoreInstanceCreator;
import xyz.wagyourtail.jsmacros.stubs.EventRegistryStub;
import xyz.wagyourtail.jsmacros.stubs.ProfileStub;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreTest {

    @Language("js")
    private final String TEST_SCRIPT = """
        var order = []
        JavaWrapper.methodToJavaAsync(5, () => {
            order.push(1);
            event.putString("test", JSON.stringify(order));
        }).run();
        JavaWrapper.methodToJavaAsync(5, () => {
            order.push(2);
            event.putString("test", JSON.stringify(order));
        }).run();
        JavaWrapper.methodToJavaAsync(6, () => {
            order.push(3);
            event.putString("test", JSON.stringify(order));
        }).run();
        order.push(0);
        event.putString("test", JSON.stringify(order));
        JavaWrapper.deferCurrentTask(-2) // change priority of this thread from 5 -> 3
        """;

    @Test
    public void test() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom("test");
        EventContainer<?> ev = core.exec("js",
            TEST_SCRIPT,
            null,
            custom,
            null,
            null
        );
        ev.awaitLock(() -> {});
        assertEquals("[0,3,1,2]", custom.getString("test"));
    }
}
