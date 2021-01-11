package xyz.wagyourtail;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Is this even faster than just iterating through a LinkedHashSet / HashSet at this point?
 * @author Wagyourtail
 */
 @SuppressWarnings("unused")
public class StringTrie implements Collection<String> {
    private Map<String, StringTrie> children = new HashMap<>();
    private final Set<String> leafs = new HashSet<>();
    private int keyLength;
    private StringTrie parent;
    private String key;
    
    private StringTrie(String initialLeaf, StringTrie parent, String key) {
        this.keyLength = initialLeaf.length();
        leafs.add(initialLeaf);
        this.parent = parent;
        this.key = key;
    }
    
    private StringTrie(int keyLength, String childKey, StringTrie initialChild, StringTrie parent, String key) {
        this.keyLength = keyLength;
        this.children.put(childKey, initialChild);
        this.parent = parent;
        this.key = key;
    }
    public StringTrie() {
        this.parent = null;
    }
    
    @Override
    public int size() {
        Optional<Integer> childrenSize = children.values().stream().map(StringTrie::size).reduce(Integer::sum);
        return leafs.size() + childrenSize.orElse(0);
    }
    
    @Override
    public boolean isEmpty() {
        return leafs.size() == 0 && children.size() == 0;
    }
    
    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            if (((String) o).length() <= keyLength) {
                return leafs.contains(o);
            } else {
                StringTrie child = children.get(((String) o).substring(0, keyLength));
                if (child != null) {
                    return child.contains(((String) o).substring(keyLength));
                }
            }
        }
        return false;
    }
    
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return getAll().iterator();
    }
    
    @NotNull
    @Override
    public String[] toArray() {
        return getAll().toArray(new String[0]);
    }
    
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return getAll().toArray(a);
    }
    
    public synchronized boolean add(String s) {
        if (children.size() == 0 && leafs.size() == 0) {
            this.keyLength = s.length();
            this.leafs.add(s);
            return true;
        }
        if (s.length() <= keyLength || children.size() == 0) {
            for (int i = Math.min(s.length(), keyLength); i > 0; --i) {
                for (String key : leafs) {
                    if (key.length() >= i && key.substring(0, i).equals(s.substring(0, i))) {
                        rekey(i);
                        return children.get(s.substring(0, keyLength)).add(s.substring(keyLength));
                    }
                }
            }
            keyLength = Math.max(s.length(), keyLength);
            return leafs.add(s);
        } else {
            String newKey = s.substring(0, keyLength);
            if (children.containsKey(newKey)) {
                return children.get(newKey).add(s.substring(keyLength));
            } else {
                Set<String> keys = children.keySet();
                for (int i = keyLength-1; i > 0; --i) {
                    for (String key : keys) {
                        if (key.substring(0, i).equals(newKey.substring(0, i))) {
                            rekey(i);
                            return children.get(s.substring(0, keyLength)).add(s.substring(keyLength));
                        }
                    }
                    for (String key : leafs) {
                        if (key.length() >= i && key.substring(0, i).equals(newKey.substring(0, i))) {
                            rekey(i);
                            return children.get(s.substring(0, keyLength)).add(s.substring(keyLength));
                        }
                    }
                }
                children.put(newKey, new StringTrie(s.substring(keyLength), this, newKey));
                return true;
            }
        }
    }
    
    private void removeChild(String childKey) {
        children.remove(childKey);
        if (leafs.size() == 0 && children.size() == 0 && parent != null) {
            parent.removeChild(key);
        }
    }
    
    /**
     * this can make the StringTrie sparse, this can cause extra steps in lookup that are no longer needed,
     * at some point it would be best to rebase the StringTrie with {@code new StringTrie().addAll(current.getAll())}
     *
     * @param o
     *
     * @return
     */
    @Override
    public boolean remove(Object o) {
        if (o instanceof String) {
            if (((String) o).length() <= keyLength) {
                if (leafs.remove(o)) {
                    if (leafs.size() == 0 && children.size() == 0 && parent != null) {
                        parent.removeChild(key);
                    }
                    return true;
                }
            } else {
                StringTrie trie = children.get(((String) o).substring(0, keyLength));
                if (trie != null) {
                    return trie.remove(((String) o).substring(keyLength));
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        final boolean[] a = {true};
        c.forEach(e -> a[0] = a[0] && contains(e));
        return a[0];
    }
    
    public boolean containsAll(String ...o) {
        final boolean[] a = {true};
        Arrays.stream(o).forEach(e -> a[0] = a[0] && contains(e));
        return a[0];
    }
    
    @Override
    public boolean addAll(@NotNull Collection<? extends String> c) {
        final boolean[] a = {false};
        c.forEach(e -> a[0] = add(e) || a[0]);
        return a[0];
    }
    
    public boolean addAll(String ...o) {
        final boolean[] a = {false};
        Arrays.stream(o).forEach(e -> a[0] = add(e) || a[0]);
        return a[0];
    }
    
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        final boolean[] a = {false};
        c.forEach(e -> a[0] = remove(e) || a[0]);
        return a[0];
    }
    
    public boolean removeAll(String ...o) {
        final boolean[] a = {false};
        Arrays.stream(o).forEach(e -> a[0] = remove(e) || a[0]);
        return a[0];
    }
    
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Set<String> content = getAll();
        content.removeAll(c);
        return removeAll(content);
    }
    
    public boolean retainAll(String ...o) {
        Set<String> content = getAll();
        content.removeAll(Arrays.asList(o));
        return removeAll(content);
    }
    
    @Override
    public void clear() {
        leafs.clear();
        children.clear();
    }
    
    public Set<String> getAllWithPrefix(String prefix) {
        if (prefix.length() > keyLength) {
            String start = prefix.substring(0, keyLength);
            String rest = prefix.substring(keyLength);
            StringTrie next = children.get(start);
            if (next != null) {
                return next.getAllWithPrefix(rest).stream().map(e -> start + e).collect(Collectors.toSet());
            } else {
                return new HashSet<>();
            }
        } else if (prefix.length() > 0) {
            for (String leaf : leafs) {
                if (leaf.startsWith(prefix)) {
                    return Sets.newHashSet(leaf);
                }
            }
            for (String key : children.keySet()) {
                if (key.startsWith(prefix)) {
                    return children.get(key).getAll().stream().map(e -> prefix + e).collect(Collectors.toSet());
                }
            }
            return new HashSet<>();
        } else {
            return getAll();
        }
    }
    
    public Set<String> getAll() {
        Set<String> results = new HashSet<>(leafs);
        children.forEach((k,v) -> results.addAll(v.getAll().stream().map(e -> k + e).collect(Collectors.toSet())));
        return results;
    }
    
    private void rekey(int newKeyLength) {
        int innerKeyLength = keyLength - newKeyLength;
        Map<String, StringTrie> newMap = new HashMap<>();
        for (String key : children.keySet()) {
            String newKey = key.substring(0, newKeyLength);
            String childKey = key.substring(newKeyLength);
            StringTrie innerChild = children.get(key);
            if (innerChild.children.size() == 0 && innerChild.leafs.size() == 1) {
                newMap.put(newKey, new StringTrie(childKey + innerChild.leafs.toArray()[0], this, newKey));
            } else {
                innerChild.parent = newMap.put(newKey, new StringTrie(innerKeyLength, childKey, innerChild, this, newKey));
                innerChild.key = childKey;
            }
        }
        leafs.removeIf(leaf -> {
            if (leaf.length() > newKeyLength) {
                String newKey = leaf.substring(0, newKeyLength);
                newMap.put(newKey, new StringTrie(leaf.substring(newKeyLength), this, newKey));
                return true;
            }
            return false;
        });
        keyLength = newKeyLength;
        children = newMap;
    }
    
    private String jsonChildrenString() {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, StringTrie> entry : children.entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().toString()).append(", ");
        }
        builder.setLength(Math.max(1, builder.length()-2));
        builder.append("}");
        return builder.toString();
    }
    
    private String jsonLeafString() {
        StringBuilder builder = new StringBuilder("[");
        for (String leaf : leafs) {
            builder.append("\"").append(leaf).append("\", ");
        }
        builder.setLength(Math.max(1, builder.length()-2));
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * @return json representation, mainly for debugging.
     */
    public String toString() {
        return String.format("{\"leafs\":%s, \"children\":%s}", jsonLeafString(), jsonChildrenString());
    }
}
