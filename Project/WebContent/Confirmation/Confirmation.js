
function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");

    // populate the star table
    var movieTableBodyElement = jQuery("#movieList_table_body");
    for (var i = 0; i < Math.min(10, resultData.length); i++) {
        var rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["list_of_genres"] + "</th>";
        rowHTML += "<th>" + resultData[i]["list_of_stars"] + "</th>";
        rowHTML += "<th><input type='number' name='quantity' min='1' max='10' value='1' onChange='updateMovieQuantity(this.value, \""+resultData[i]["movieId"]+"\")'></th>";
        rowHTML += "<th><button type='submit' class='btn btn-primary btn-lg btn-block' onClick='RemoveFromCart(\""+resultData[i]["movieId"]+"\")' >Remove</button></th>";
        rowHTML += "</tr>"
        movieTableBodyElement.append(rowHTML);
    }
}


(function(){
    var isValid = getParameterByName("isValid");
    var header = jQuery("#Message");

    if(isValid !== "true"){
        header.append("<h1>Purchase cannot complete, Please enter a valid card.</h1>");
    }
    else {
        header.append("<h1>Thank you for your purchase!</h1>");
        var emptyCart = [];
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