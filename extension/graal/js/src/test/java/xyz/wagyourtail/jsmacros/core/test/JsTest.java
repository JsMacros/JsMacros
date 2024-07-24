package xyz.wagyourtail.jsmacros.core.test;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.stubs.CoreInstanceCreator;
import xyz.wagyourtail.jsmacros.stubs.EventRegistryStub;
import xyz.wagyourtail.jsmacros.stubs.ProfileStub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsTest {

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
        EventCustom custom = new EventCustom(core, "test");
        EventContainer<?> ev = core.exec("js",
                TEST_SCRIPT,
                null,
                custom,
                null,
                null
        );
        ev.awaitLock(() -> {
        });
        assertEquals("[0,3,1,2]", custom.getString("test"));
    }

    @Language("js")
    private final String TEST_SCRIPT_2 = """
            var j = []
            JavaWrapper.methodToJavaAsync(() => {
                while (j.length < 10) {
                    j.push('a');
                    Time.sleep(100)
                }
            }).run();
            JavaWrapper.methodToJavaAsync(() => {
                while (j.length < 10) {
                    j.push('b');
                    Time.sleep(105)
                }
            }).run();
            JavaWrapper.deferCurrentTask(-1)
            while(j.length < 10) {
                JavaWrapper.deferCurrentTask()
            }
            j.push('c');
            event.putString("test", JSON.stringify(j))
            """;

    @Test
    public void test2() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom(core, "test");
        EventContainer<?> ev = core.exec("js",
                TEST_SCRIPT_2,
                null,
                custom,
                null,
                null
        );
        EventLockWatchdog.startWatchdog(ev, null, 3000);
        ev.awaitLock(() -> {
        });
        assertEquals("[\"a\",\"b\",\"a\",\"b\",\"a\",\"b\",\"a\",\"b\",\"a\",\"b\",\"c\"]", custom.getString("test"));
    }

    @Language("js")
    private final String TEST_SCRIPT_3 = """
            const start = Time.time()
            let a = []
            function long() {
                a.push("long started");
                Time.sleep(5000);
                a.push('long finished');
                done()
            }
            function rapid() {
                a.push('rapid 1')
                Time.sleep(500);
                a.push('rapid 2')
                Time.sleep(500);
                a.push('rapid 3')
                Time.sleep(500);
                a.push('rapid 4')
                done()
            }
            var isDone = false
            function done() {
                if (a.length == 6) {
                    event.putString("test", JSON.stringify(a))
                    event.putDouble("time", Time.time() - start)
                    isDone = true
                }
            }
            function runAsync(fn) {
                JavaWrapper.methodToJavaAsync(fn).run()
            }
            runAsync(long)
            runAsync(rapid)
            while (!isDone) {
                JavaWrapper.deferCurrentTask()
            }
            """;

    @Test
    public void test3() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom(core, "test");
        EventContainer<?> ev = core.exec("js",
                TEST_SCRIPT_3,
                null,
                custom,
                null,
                null
        );
        EventLockWatchdog.startWatchdog(ev, null, 10000);
        ev.awaitLock(() -> {
        });
        System.out.println("Time: " + custom.getDouble("time"));
        assertEquals("[\"long started\",\"rapid 1\",\"rapid 2\",\"rapid 3\",\"rapid 4\",\"long finished\"]", custom.getString("test"));
        assertTrue(custom.getDouble("time") > 5000);
        assertTrue(custom.getDouble("time") < 7000);
    }

}
