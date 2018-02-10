
(function(){
    var isValid = getParameterByName("isValid");
    var header = jQuery("#Message");

    if(isValid !== "true"){
        header.append("<h1>Purchase cannot complete, Please enter a valid card.</h1>");
    }
    else {
        header.append("<h1>Thank you for your purchase!</h1>");
        localStorage.setItem('cart', JSON.stringify(emptyCart));
    }


})();

function GoHome() {
    window.location.assign("Main");
}


function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta" + "&ps=20";
    window.location.assign(newpage);

}
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));

}