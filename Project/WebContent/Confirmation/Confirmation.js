
(function(){
    localStorage.setItem('cart', JSON.stringify(emptyCart));
})();

function GoHome() {
    window.location.assign("Main");
}

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta" + "&ps=20";
    window.location.assign(newpage);
}