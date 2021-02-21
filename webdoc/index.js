
function populateClassSidebar() {
    classLists.innerHTML = "";
    for (const key of classGroups) {
        const h4 = document.createElement("h4");
        h4.innerHTML = key;
        classLists.appendChild(h4);
        const div = document.createElement("div");
        div.setAttribute("id", `${key}List`);
        classLists.appendChild(div);
    }
    for (const [name, clazz] of searchMaps.classes) {
        const a = document.createElement("a");
        a.setAttribute("href", `${versionSelect.value}/${clazz.url.replace(/(#|$)/, ".html$1")}`);
        frameLink(a);
        a.innerHTML = name;
        document.getElementById(`${clazz.group}List`).appendChild(a);
    }
}

function frameLink(a) {
    a.setAttribute("onclick", "openMain(this.href); return false;");
}

async function openMain(url) {
    window.history.replaceState({}, '', `${window.location.href.split('?')[0]}?${url}`);
    const req = await fetch(url);
    if (req.status != 200) {
        alert(`failed to load ${req.status}: \n${req.statusText}`);
    }
    const text = await req.text();
    const parser = new DOMParser();
    const doc = parser.parseFromString(text, "text/html").getElementsByTagName("main")[0];
    mainContent.setAttribute("class", doc.getAttribute("class"));
    mainContent.innerHTML = doc.innerHTML;
    for (const a of mainContent.getElementsByTagName("a")) {
        if (!a.hasAttribute("target") && !a.getAttribute("href")?.startsWith("#")) {
            a.setAttribute("href", a.getAttribute("href").replace(/(\.\.\/)*/, `${versionSelect.value}/`));
            frameLink(a);
        }
    }
}

async function searchBox(val) {
    if (mainContent.getAttribute("class") != "searchMain") {
        await openMain("./search.html");
        updateClassGroups();
    }
    await searchF(val);
    for (const resultItem of document.getElementsByClassName("resultItem")) {
        frameLink(resultItem.getElementsByTagName("a")[0]);
    }
}

loadingSearchMap.then(populateClassSidebar).then(() => {
    const rawParams = window.location.search?.substring(1) || "general.html";
    openMain(rawParams);
});