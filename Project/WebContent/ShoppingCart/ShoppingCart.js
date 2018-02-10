
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

function RemoveFromCart(movieId) {
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    var newCart = [];
    cart.forEach(function (value) {
        if(value.movieId !== movieId)
            newCart.push({movieId: value.movieId})
    })

    //cart.push({movieId: movieId});
    // then put it back.
    localStorage.setItem('cart', JSON.stringify(newCart));
    alert(movieId + " was removed from your cart");
    window.location.assign("ShoppingCart");
}

function updateMovieQuantity(amount, movieId){
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    var newCart = [];
    cart.forEach(function (value) {
        if(value.movieId === movieId)
            for (i = 0; i < parseInt(amount); i++) {
                newCart.push({movieId: value.movieId})
            }
        newCart.push({movieId: value.movieId})
    })

    localStorage.setItem('cart', JSON.stringify(newCart));
    //window.location.assign("ShoppingCart");
}

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1&action=SEARCH&order=ta&ps=20";
    window.location.assign(newpage);
}

function GoToCustomerInfo() {
    window.location.assign("CustomerInfo");
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

// makes the HTTP GET request and registers on success callback function handleStarResult
(function(){
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    if(cart.length > 0){
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "/Project/api/movie",
            data: {
                ACTION: "SEARCHLIST",
                Page: 1,
                PageSize: "20",
                Cart: JSON.stringify(cart)
            },
            success: function(resultData){
                handleListResult(resultData);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                alert(textStatus);
            }
        });
    }
})();



function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
