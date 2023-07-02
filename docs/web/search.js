let prevSearch = null;

const searchMaps = {classes: new Map(), fields: new Map(), methods: new Map()}
let classGroups = new Set();
let loaded = false;

async function reloadSearchMap() {
    for (const val of Object.values(searchMaps)) val.clear();
    classGroups.clear();
    const res = await fetch(`${versionSelect.value}/search-list`);
    if (res.status == 200) {
        const text = (await res.text()).split("\n");
        for (const line of text) {
            if (line == "") continue;
            const parts = line.split("\t");
            switch (parts[0]) {
                case "C":
                    if (!searchMaps.classes.has(parts[4] ?? parts[1]))
                        searchMaps.classes.set(parts[4] ?? parts[1], new Set());
                    searchMaps.classes.get(parts[4] ?? parts[1]).add({
                        name: parts[1],
                        url: parts[2],
                        group: parts[3] ?? "Class"
                    });
                    if (parts[3] && parts[3] !== "Class") classGroups.add(parts[3]);
                    break;
                case "M": {
                    let methodStuff = {class: parts[1].split("#")[0], name: parts[1].split("#")[1], url: parts[2]}
                    let cname = methodStuff.class;
                    if (!searchMaps.classes.has(cname)) {
                        for (const [name, st] of searchMaps.classes) {
                            for (const clazz of st) {
                                if (clazz.name === cname) {
                                    cname = name;
                                    break;
                                }
                            }
                        }
                    }
                    searchMaps.methods.set(`${cname}#${methodStuff.name}`, methodStuff);
                    break;
                }
                case "F": {
                    let fieldStuff = {class: parts[1].split("#")[0], name: parts[1].split("#")[1], url: parts[2]}
                    let cname = fieldStuff.class;
                    if (!searchMaps.classes.has(cname)) {
                        for (const [name, clazz] of searchMaps.classes) {
                            for (const [name, st] of searchMaps.classes) {
                                for (const clazz of st) {
                                    if (clazz.name === cname) {
                                        cname = name;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    searchMaps.fields.set(`${cname}#${fieldStuff.name}`, fieldStuff);
                    break;
                }
                default:
                    alert(`unsupported line: ${line}`)
            }
        }
    } else {
        alert(`error ${res.status}\n${res.statusText}`);
    }
    searchMaps.classes = new Map([...searchMaps.classes.entries()].sort());
    searchMaps.methods = new Map([...searchMaps.methods.entries()].sort());
    searchMaps.fields = new Map([...searchMaps.fields.entries()].sort());
    classGroups.add("Class");
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

const loadingSearchMap = reloadSearchMap();
