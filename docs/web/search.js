let searchSyncId = 0n;
let prevSearch = null;
let prevSearchFinished = false;
try {
    frameLink;
} catch {
    frameLink = undefined;
}

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

async function searchF(query, force = false) {
    const syncId = ++searchSyncId;
    const _style = document.getElementById("search").style ?? {};
    if (query) _style.backgroundColor = localStorage.getItem('colorMode') === "light" ? "cyan" : "darkcyan";
    else _style.backgroundColor = "";
    await new Promise(res => setTimeout(res, 80));
    if (syncId !== searchSyncId) return;

    await loadingSearchMap;
    if (syncId !== searchSyncId) return;

    query = query.toLowerCase();
    console.log(query === prevSearch);
    if (query === prevSearch && prevSearchFinished && !force) {
        _style.backgroundColor = "";
        return;
    }
    prevSearch = query;
    prevSearchFinished = false;
    searchResults.innerHTML = "";
    if (query === "") {
        prevSearchFinished = true;
        _style.backgroundColor = "";
        return;
    }

    for (const group of [...classGroups, "Method", "Field"]) {
        const groupDiv = document.createElement("div");
        groupDiv.setAttribute("id", `${group}Results`);
        searchResults.appendChild(groupDiv);
    }

    const start = Date.now();
    let time = Date.now();
    const asyncCheck = async () => {
        if (Date.now() - time > 20) {
            await new Promise(res => setTimeout(res, 1));
            if (syncId !== searchSyncId) return true;
            time = Date.now();
        }
        return false;
    }

    for (const [name, st] of searchMaps.classes) {
        for (const clazz of st) {
            if (document.getElementById(`${clazz.group}Check`).checked) {
                if (clazz.name.toLowerCase().includes(query)) {
                    appendSearchResult(name, clazz.url, clazz.group);
                }
            }
        }
        if (await asyncCheck()) return;
    }

    if (methodsCheck.checked) {
        for (const [name, method] of searchMaps.methods) {
            for (const clazz of searchMaps.classes.get(name.split("#", 1)[0])) {
                if (clazz.name === method.class) {
                    if (document.getElementById(`${clazz.group}Check`).checked &&
                        method.name.toLowerCase().includes(query)) {
                        appendSearchResult(name, method.url, "Method");
                    }
                    break;
                }
            }
            if (await asyncCheck()) return;
        }
    }
    if (fieldsCheck.checked) {
        for (const [name, field] of searchMaps.fields) {
            for (const clazz of searchMaps.classes.get(name.split("#", 1)[0])) {
                if (clazz.name === field.class) {
                    if (document.getElementById(`${clazz.group}Check`).checked &&
                        field.name.toLowerCase().includes(query)) {
                        appendSearchResult(name, field.url, "Field");
                    }
                    break;
                }
            }
            if (await asyncCheck()) return;
        }
    }

    prevSearchFinished = true;
    _style.backgroundColor = "";
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
    frameLink?.(a);
    div.appendChild(a);
    document.getElementById(`${type}Results`).appendChild(div);
}

let loadingSearchMap = reloadSearchMap();
