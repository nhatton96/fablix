function sendData(){
    /*
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchtitle").value +
        "&year=" + document.getElementById("searchyear").value +
        "&director=" + document.getElementById("searchdirector").value +
        "&star=" + document.getElementById("searchstar").value + "&page=1" + "&action=SEARCHADV" + "&order=ta";
    window.location.assign(newpage);*/
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta";
    window.location.assign(newpage);
}