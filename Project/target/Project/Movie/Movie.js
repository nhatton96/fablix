function handleListResult(resultData) {
	console.log("handleListResult: populating movie table from resultData");

	// populate the star table
	var movieTableBodyElement = jQuery("#movieList_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movieId"] + "</th>";
		rowHTML += "<th>" + resultData[i]["title"] + "</th>";
		rowHTML += "<th>" + resultData[i]["year"] + "</th>";
		rowHTML += "<th>" + resultData[i]["director"] + "</th>";
		rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
		rowHTML += "<th>" + createGenreLink(resultData[i]) + "</th>";
		rowHTML += "<th>" + create(resultData[i]) + "</th>";
        rowHTML += "<th><button type='submit' class='btn btn-primary btn-lg btn-block' onClick='AddToCart(\""+resultData[i]["movieId"]+"\")' >ADD</button></th>";
		rowHTML += "</tr>";
		movieTableBodyElement.append(rowHTML);
	}
}

//(function(){
//    var loggedIn = sessionStorage.getItem("LoggedIn");
//    if(loggedIn !== "true")
//        window.location.assign("Login");
//})();

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1&action=SEARCH&order=ta&ps=20";
    window.location.assign(newpage);
}

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
function createGenreLink(data){
	var result = "";
    for (var i = 0; i < data["list_of_genres"].length ; i++){
    	var genreLink = "/Project/MovieList/MovieList.html?genre=" + data["list_of_genres"][i] + "&page=1&action=SEARCHGENRE&order=ta&ps=20";
    	result += "<a href=" + genreLink + ">" + data["list_of_genres"][i] + "</a>";
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

$('#searchbar').autocomplete({
	lookup : function(query, doneCallback) {
		console.log("Initiated");
		if (query in localStorage) {
			console.log("Cached")
			var data = localStorage[query];
			var jsonData = JSON.parse(data)
			if (jsonData[0]) {
				doneCallback({
					suggestions : jsonData
				});
				console.log(data);
			}
			else {
				console.log("result from cache is an empty list");
			}
		} else {
			console.log("Ajax")
			handleLookup(query, doneCallback);
		}
	},
	onSelect : function(suggestion) {
		handleSelectSuggestion(suggestion)
	},
	triggerSelectOnValidInput : false,
	groupBy : "category",
	deferRequestBy : 300,
	minChars : 3
});

function handleLookup(query, doneCallback) {

	jQuery.ajax({
		method : "GET",
		url : "/Project/api/movie",
		data : {
			ACTION : "SUG",
			keyWord : query
		},
		success : function(data) {
			handleLookupAjaxSuccess(data, query, doneCallback)
		},
		error : function(errorData) {
			console.log("lookup ajax error");
			console.log(errorData);
		}
	});
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	
	localStorage[query] = data;
	var jsonData = JSON.parse(data);
	if (jsonData[0]) {
		console.log("lookup ajax successful")
		console.log(data)
		doneCallback({			
			suggestions : jsonData
		});
	}
	else {
		console.log("find nothing");
	}
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	console.log("you select " + suggestion["value"])
	var link = "";
	if (suggestion["data"]["category"] === "movie")
		link = "/Project/servlet/SingleMovie?" + "movieId="
				+ suggestion["data"]["id"];
	else
		link = "/Project/Star/Star.html?" + "stdi=" + suggestion["data"]["id"];
	window.location.assign(link);
}