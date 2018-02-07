function handleListResult(resultData) {
	console.log("handleListResult: populating movie table from resultData");

	// populate the star table
	var movieTableBodyElement = jQuery("#movieList_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<th>" + resultData[i]["title"] + "</th>";
		rowHTML += "<th>" + resultData[i]["year"] + "</th>";
		rowHTML += "<th>" + resultData[i]["director"] + "</th>";
		rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
		rowHTML += "<th>" + resultData[i]["list_of_genres"] + "</th>";
		rowHTML += "<th>" + create(resultData[i]) + "</th>";
        rowHTML += "<th><button type='submit' class='btn btn-primary btn-lg btn-block' onClick='AddToCart(\""+resultData[i]["movieId"]+"\")' >ADD</button></th>";
		rowHTML += "</tr>";
		movieTableBodyElement.append(rowHTML);
	}
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

function AddToCart(movieId) {
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    // add to it,
    cart.push({movieId: movieId});
    // then put it back.
    localStorage.setItem('cart', JSON.stringify(cart));

    alert(movieId + " was added to your cart");
}

function create(data){
	var result = "";
    for (var i = 0; i < data["list_of_stars"].length ; i++){
    	var stLink = "/Project/Star/Star.html?" + "stdi=" + data["list_of_stid"][i];
    	result += "<a href=" + stLink + ">" + data["list_of_stars"][i] + "</a>";
    }
    return result;
}

// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/Project/api/movie",
    data: {
        ACTION: "SINGLE",
        MovieId: getParameterByName('movieId')
    },
    success: function(resultData){
        handleListResult(resultData);
    },
    error: function(XMLHttpRequest, textStatus, errorThrown){
        alert(textStatus);
    }
});


function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}