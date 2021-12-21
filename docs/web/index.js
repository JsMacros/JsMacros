const toggleColorMode = e => {
    // Switch to Light Mode
    if (e.currentTarget.classList.contains("light--hidden")) {
    	// Sets the custom HTML attribute
    	document.documentElement.setAttribute("color-mode", "light");

		//Sets the user's preference in local storage
		localStorage.setItem("color-mode", "light")
		return;
	}
    
    /* Switch to Dark Mode
    Sets the custom HTML attribute */
    document.documentElement.setAttribute("color-mode", "dark");

	// Sets the user's preference in local storage
	localStorage.setItem("color-mode", "dark");
};

// Get the buttons in the DOM
const toggleColorButtons = document.querySelectorAll(".color-mode__btn");

// Set up event listeners
toggleColorButtons.forEach(btn => {
    btn.addEventListener("click", toggleColorMode);
});
    

function populateClassSidebar() {
    classLists.innerHTML = "";
    for (const key of Array.from(classGroups).sort().reverse()) {
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

async function openMain(url, dontpush) {
    url = url.replace(/https?:\/\/.+?\//, "/");
    if (!dontpush) window.history.pushState({}, '', `${window.location.href.split('?')[0].replace(/#.*\??/, "")}?${url}`);
    const req = await fetch(url);
    if (req.status != 200) {
        alert(`failed to load ${req.status}: \n${req.statusText}`);
        return;
    }
    const text = await req.text();
    const parser = new DOMParser();
    const doc = parser.parseFromString(text, "text/html").getElementsByTagName("main")[0];
    const cname = doc.getAttribute("class");
    mainContent.setAttribute("class", cname);
    mainContent.innerHTML = doc.innerHTML;
    for (const a of mainContent.getElementsByTagName("a")) {
        if (!a.hasAttribute("target") && !a.getAttribute("href")?.startsWith("#")) {
            a.setAttribute("href", a.getAttribute("href").replace(/(\.\.\/)*/, `${versionSelect.value}/`));
            frameLink(a);
        }
    }
    if (cname == "searchMain") {
        updateClassGroups();
    }
    scrlTo(url);
    mainNav.parentElement.style.display = null;
}

function scrlTo(url) {
    const scroll = url.split("#")[1];
    if (scroll) {
        window.scrollTo(window.scrollX, document.getElementById(scroll).offsetTop);
        console.log(document.getElementById(scroll).offsetTop);
        console.log(scroll);
    } else {
        window.scrollTo(window.scrollX, 0);
    }
}

async function searchBox(val) {
    if (mainContent.getAttribute("class") != "searchMain") {
        await openMain("./search.html");
    }
    await searchF(val);
    for (const resultItem of document.getElementsByClassName("resultItem")) {
        frameLink(resultItem.getElementsByTagName("a")[0]);
    }
}

async function changeVersion() {
    reloadSearchMap().then(populateClassSidebar);
    openMain("general.html");
}

loadingSearchMap.then(populateClassSidebar).then(() => {
    const rawParams = window.location.search?.substring(1) || "general.html";
    const scroll = window.location.href.split("?")[1]?.split("#")[1];
    openMain(scroll ? `${rawParams}#${scroll}` : rawParams);
});

menuBtn.onclick = () => {
    if (mainNav.parentElement.style.display) {
        mainNav.parentElement.style.display = null;
    } else {
        mainNav.parentElement.style.display = "block";
    }
}

window.addEventListener("popstate", (e) => {
    if (e.state !== null) {
        const scroll = window.location.href.split("#")[1];
        openMain(window.location.search.substring(1) + (scroll ? "#" + scroll : ""), true);
    } else
        scrlTo(window.location.href)
});