package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Queues;

public class consumerFunctions extends Functions {
    public static LinkedBlockingQueue<Thread> queue = Queues.newLinkedBlockingQueue();

    public consumerFunctions(String libName) {
        super(libName);
    }


    public consumerFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public Consumer<Object> toConsumer(Consumer<Object> c) {
        return new Consumer<Object>() {
            @Override
            public void accept(Object arg0) {
                queue.add(Thread.currentThread());
                Thread t = queue.peek();
                while (t != Thread.currentThread()) try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c.accept(arg0);
                queue.poll();
            }
        };
    }
    
    public BiConsumer<Object, Object> toBiConsumer(BiConsumer<Object, Object> c) {
        return new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object arg0, Object arg1) {
                queue.add(Thread.currentThread());
                Thread t = queue.peek();
                while (t != Thread.currentThread()) try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c.accept(arg0, arg1);
                queue.poll();
            }
        };
    }
}
