let prevSearch = null;

/**
 * @type {{
 *  classes: Map<string, Set<{ group: string; name: string; url: string; }>>;
 *  fields:  Map<string, Set<{ class: string; name: string; url: string; }>>;
 *  methods: Map<string, Set<{ class: string; name: string; url: string; }>>;
 * }}
 */
const searchMaps = {
    classes: new Map(),
    fields:  new Map(),
    methods: new Map()
};
let reloadSyncId = 0n;
/** @type {Set<string>} */
let classGroups = new Set();
let loaded = false;

async function reloadSearchMap() {
    const syncId = ++reloadSyncId;
    for (const val of Object.values(searchMaps)) val.clear();
    classGroups.clear();

    const res = await fetch(`${versionSelect.value}/search-list`);
    if (syncId !== reloadSyncId) return;
    if (res.status !== 200) {
        alert(`fetch error ${res.status}\n${res.statusText}`);
        return;
    }
    const text = (await res.text()).split("\n");
    if (syncId !== reloadSyncId) return;

    let lastClass = '\x00';
    for (const line of text) if (line) {
        const parts = line.split("\t");
        switch (parts[0]) {
            case "C": {
                const key = lastClass = parts[4] ?? parts[1];
                let set = searchMaps.classes.get(key);
                if (!set) searchMaps.classes.set(key, set = new Set());
                const group = parts[3] ?? "Class";
                set.add({ group, name: parts[1], url: parts[2] });
                classGroups.add(group);
                break;
            }
            case "M":
            case "F": {
                const split = parts[1].split("#", 2);
                const stuff = { class: split[0], name: split[1], url: parts[2] };
                let cname = stuff.class;
                if (!searchMaps.classes.has(cname)) block: {
                    for (const clazz of searchMaps.classes.get(lastClass)) {
                        if (clazz.name === cname) {
                            cname = lastClass;
                            break block;
                        }
                    }
                    for (const [name, st] of searchMaps.classes) {
                        for (const clazz of st) {
                            if (clazz.name === cname) {
                                cname = name;
                                break block;
                            }
                        }
                    }
                }
                searchMaps[parts[0] === "M" ? "methods" : "fields"].set(`${cname}#${stuff.name}`, stuff);
                break;
            }
            default:
                alert(`unsupported line: ${line}`);
        }
    }
    sortMap(searchMaps.classes);
    sortMap(searchMaps.methods);
    sortMap(searchMaps.fields);
    if (classGroups.delete("Class")) classGroups.add("Class"); // keep it the last one
}

/**
 * @template T
 * @param {Map<string, T>} map
 */
function sortMap(map) {
    const clone = new Map(map);
    const keys = [...clone.keys()].sort();
    map.clear();
    for (const key of keys) {
        map.set(key, clone.get(key));
    }
    return map;
}

function updateClassGroups() {
    classGroupChecks.innerHTML = '';
    for (const name of classGroups) {
        const div = document.createElement("div");
        const label = document.createElement("label");
        label.setAttribute("for", `${name}Check`);
        label.innerHTML = name;
        div.appendChild(label);
        const input = document.createElement("input");
        input.setAttribute("type", "checkbox");
        input.setAttribute("id", `${name}Check`);
        input.setAttribute("class", "SearchCheck");
        input.setAttribute("name", `${name}Check`);
        input.setAttribute("checked", null);
        input.setAttribute("onclick", "searchF(search.value, true)")
        div.appendChild(input);

        classGroupChecks.appendChild(div);

    }

}

async function searchF(val, override = false) {
    val = val.toLowerCase();
    if (!loaded) {
        loaded = true;
        await reloadSearchMap();
    }
    console.log(val === prevSearch);
    if (val === prevSearch && !override) return;
    prevSearch = val;
    searchResults.innerHTML = "";
    for (const group of classGroups) {
        const groupDiv = document.createElement("div");
        groupDiv.setAttribute("id", `${group}Results`);
        searchResults.appendChild(groupDiv);
    }
    const methodDiv = document.createElement("div");
    methodDiv.setAttribute("id", "MethodResults");
    searchResults.appendChild(methodDiv);
    const fieldDiv = document.createElement("div");
    fieldDiv.setAttribute("id", "FieldResults");
    searchResults.appendChild(fieldDiv);
    for (const [name, st] of searchMaps.classes) {
        for (const clazz of st) {
            if (document.getElementById(`${clazz.group}Check`).checked) {
                if (clazz.name.toLowerCase().includes(val)) {
                    appendSearchResult(name, clazz.url, clazz.group);
                }
            }
        }
    }
    if (methodsCheck.checked) {
        for (const [name, method] of searchMaps.methods) {
            for (const clazz of searchMaps.classes.get(`${name.split("#")[0]}`)) {
                if (clazz.name === method.class) {
                    if (document.getElementById(`${clazz.group}Check`).checked &&
                        method.name.toLowerCase().includes(val)) {
                        appendSearchResult(name, method.url, "Method");
                    }
                    break;
                }
            }
        }
    }
    if (fieldsCheck.checked) {
        for (const [name, field] of searchMaps.fields) {
            for (const clazz of searchMaps.classes.get(`${name.split("#")[0]}`)) {
                if (clazz.name === field.class) {
                    if (document.getElementById(`${clazz.group}Check`).checked &&
                        field.name.toLowerCase().includes(val)) {
                        appendSearchResult(name, field.url, "Field");
                    }
                    break;
                }
            }
        }
    }
}

function appendSearchResult(name, url, type) {
    const div = document.createElement("div");
    div.setAttribute("class", "resultItem");
    const flags = document.createElement("div");
    flags.setAttribute("class", "flags");
    const typed = document.createElement("div");
    typed.setAttribute("class", `${type}Flag flag`);
    typed.innerHTML = type[0] ?? "C";
    flags.appendChild(typed);
    div.appendChild(flags);
    const a = document.createElement("a");
    a.setAttribute("href", `${versionSelect.value}/${url.replace(/(#|$)/, ".html$1")}`)
    a.innerHTML = name;
    div.appendChild(a);
    document.getElementById(`${type}Results`).appendChild(div);
}

let loadingSearchMap = reloadSearchMap();
