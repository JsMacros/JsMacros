package xyz.wagyourtail;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class PrioryFiFoTaskQueue<E> implements Queue<E> {
    volatile E currentTask;
    final Int2ObjectOpenHashMap<List<E>> tasks = new Int2ObjectOpenHashMap<>();
    final Set<E> taskSet = new HashSet<>();
    final Function<E, Integer> priorityFunction;

    public PrioryFiFoTaskQueue(Function<E, Integer> priorityFunction) {
        this.priorityFunction = priorityFunction;
    }

    @Override
    public synchronized int size() {
        return tasks.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return tasks.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return taskSet.contains(o);
    }

    @NotNull
    @Override
    public synchronized Iterator<E> iterator() {
        return taskSet.iterator();
    }

    @NotNull
    @Override
    public synchronized Object[] toArray() {
        return taskSet.toArray();
    }

    @NotNull
    @Override
    public synchronized <T> T[] toArray(@NotNull T[] ts) {
        return taskSet.toArray(ts);
    }

    @Override
    public synchronized boolean add(E e) {
        boolean wasEmpty = taskSet.isEmpty();
        if (taskSet.add(e)) {
            if (wasEmpty) {
                currentTask = e;
            }
            tasks.computeIfAbsent(priorityFunction.apply(e), k -> new ArrayList<>()).add(e);
            this.notifyAll();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (taskSet.remove(o)) {
            int prio = priorityFunction.apply((E) o);
            List<E> list = tasks.get(prio);
            list.remove(o);
            if (list.isEmpty()) {
                tasks.remove(prio);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean containsAll(@NotNull Collection<?> collection) {
        return taskSet.containsAll(collection);
    }

    @Override
    public synchronized boolean addAll(@NotNull Collection<? extends E> collection) {
        boolean changed = false;
        for (E e : collection) {
            changed |= add(e);
        }
        return changed;
    }

    @Override
    public synchronized boolean removeAll(@NotNull Collection<?> collection) {
        boolean changed = false;
        for (Object o : collection) {
            changed |= remove(o);
        }
        return changed;
    }

    @Override
    public synchronized boolean retainAll(@NotNull Collection<?> collection) {
        boolean changed = false;
        for (Object o : taskSet) {
            if (!collection.contains(o)) {
                changed |= remove(o);
            }
        }
        return changed;
    }

    @Override
    public synchronized void clear() {
        taskSet.clear();
        tasks.clear();
    }

    @Override
    public synchronized boolean offer(E e) {
        return add(e);
    }

    @Override
    public synchronized E remove() {
        E e = currentTask;
        if (e != null) {
            remove(e);
            currentTask = getLowestPrioItem();
        }
        return e;
    }

    @Override
    public E poll() {
        return remove();
    }

    public synchronized E pollWaiting() throws InterruptedException {
        while (taskSet.isEmpty()) {
            this.wait();
        }
        return remove();
    }

    public synchronized E pollWaiting(long timeout) throws InterruptedException {
        long timeoutLeft = timeout;
        while (taskSet.isEmpty() && timeoutLeft > 0) {
            long start = System.currentTimeMillis();
            this.wait(timeoutLeft);
            timeoutLeft -= System.currentTimeMillis() - start;
        }
        return remove();
    }

    public synchronized E peekWaiting() throws InterruptedException {
        if (taskSet.isEmpty()) {
            this.wait();
        }
        return currentTask;
    }

    public synchronized E peekWaiting(long timeout) throws InterruptedException {
        if (taskSet.isEmpty()) {
            this.wait(timeout);
        }
        return currentTask;
    }

    @Override
    public synchronized E element() {
        return currentTask;
    }

    @Override
    public synchronized E peek() {
        return currentTask;
    }

    private synchronized E getLowestPrioItem() {
        if (tasks.isEmpty()) {
            return null;
        }
        int lowestPrio = Integer.MAX_VALUE;
        for (int prio : tasks.keySet()) {
            if (prio < lowestPrio) {
                lowestPrio = prio;
            }
        }
        return tasks.get(lowestPrio).get(0);
    }

}
