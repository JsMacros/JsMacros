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

public class PyTest {

    @Language("py")
    private final String TEST_SCRIPT = """
            import json
            order = []
            
            def a():
                order.append(1)
                event.putString("test", json.dumps(order))
            
            JavaWrapper.methodToJavaAsync(5, a).run()
            
            def b():
                order.append(2)
                event.putString("test", json.dumps(order))
            
            JavaWrapper.methodToJavaAsync(5, b).run()
            
            def c():
                order.append(3)
                event.putString("test", json.dumps(order))
            
            JavaWrapper.methodToJavaAsync(6, c).run()
            order.append(0)
            event.putString("test", json.dumps(order))
            JavaWrapper.deferCurrentTask(-2) # change priority of this thread from 5 -> 3
            """;

    @Test
    public void test() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom("test");
        EventContainer<?> ev = core.exec("py",
                TEST_SCRIPT,
                null,
                custom,
                null,
                null
        );
        ev.awaitLock(() -> {
        });
        assertEquals("[0, 3, 1, 2]", custom.getString("test"));
    }

    @Language("py")
    private final String TEST_SCRIPT_2 = """
            import json
            j = []
            
            def a():
                while len(j) < 10:
                    j.append('a')
                    Time.sleep(100)
            
            JavaWrapper.methodToJavaAsync(a).run()

            def b():
                while len(j) < 10:
                    j.append('b')
                    Time.sleep(105)

            JavaWrapper.methodToJavaAsync(b).run()
            JavaWrapper.deferCurrentTask(-1)

            while len(j) < 10:
                JavaWrapper.deferCurrentTask()

            
            j.append('c')
            event.putString("test", json.dumps(j))
            """;

    @Test
    public void test2() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom("test");
        EventContainer<?> ev = core.exec("py",
                TEST_SCRIPT_2,
                null,
                custom,
                null,
                null
        );
        EventLockWatchdog.startWatchdog(ev, IEventListener.NULL, 3000);
        ev.awaitLock(() -> {
        });
        assertEquals("[\"a\", \"b\", \"a\", \"b\", \"a\", \"b\", \"a\", \"b\", \"a\", \"b\", \"c\"]", custom.getString("test"));
    }

    @Language("py")
    private final String TEST_SCRIPT_3 = """
            import json

            start = Time.time()
            a = []
            def long():
                a.append("long started")
                Time.sleep(5000)
                a.append('long finished')
                done()
            
            def rapid():
                a.append('rapid 1')
                Time.sleep(500)
                a.append('rapid 2')
                Time.sleep(500)
                a.append('rapid 3')
                Time.sleep(500)
                a.append('rapid 4')
                done()
            
            isDone = False
            def done():
                if len(a) == 6:
                    event.putString("test", json.dumps(a))
                    event.putDouble("time", Time.time() - start)
                    isDone = True
            
            def runAsync(fn):
                JavaWrapper.methodToJavaAsync(fn).run()
            
            runAsync(long)
            runAsync(rapid)
            while not isDone:
                JavaWrapper.deferCurrentTask()
            """;

    @Test
    public void test3() throws InterruptedException {
        Core<ProfileStub, EventRegistryStub> core = CoreInstanceCreator.createCore();
        EventCustom custom = new EventCustom("test");
        EventContainer<?> ev = core.exec("py",
                TEST_SCRIPT_3,
                null,
                custom,
                null,
                null
        );
        EventLockWatchdog.startWatchdog(ev, IEventListener.NULL, 10000);
        ev.awaitLock(() -> {
        });
        System.out.println("Time: " + custom.getDouble("time"));
        assertEquals("[\"long started\", \"rapid 1\", \"rapid 2\", \"rapid 3\", \"rapid 4\", \"long finished\"]", custom.getString("test"));
        assertTrue(custom.getDouble("time") > 5000);
        assertTrue(custom.getDouble("time") < 7000);
    }

}
