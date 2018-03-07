function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");

    // populate the star table
    var movieTableBodyElement = jQuery("#movieList_table_body");
    for (var i = 0; i < resultData.length; i++) {
        var rowHTML = "";
        rowHTML += "<tr>"; // id='tableRows' class='clickable-row' data-href='"+resultData[i]["movieId"]+"'>";
        rowHTML += "<th>" + resultData[i]["movieId"] + "</th>";
        rowHTML += "<th>" + createMvLink(resultData[i]) + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["list_of_genres"] + "</th>";
        rowHTML += "<th>" + createStarLink(resultData[i]) + "</th>";
        rowHTML += "<th><button type='submit' class='btn btn-primary btn-lg btn-block' onClick='AddToCart(\""+resultData[i]["movieId"]+"\")' >ADD</button></th>";
        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }
}

function AddToCart(movieId) {
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    // add to it,
    cart.push({movieId: movieId});
    // then put it back.
    localStorage.setItem('cart', JSON.stringify(cart));

    alert(movieId + " was added to your cart");
}

function createMvLink(data){
	var mvLink = "/Project/servlet/SingleMovie?" + "movieId=" + data["movieId"];
	return "<a href=" + mvLink + ">" + data["title"] + "</a>";
}

function createStarLink(data){
	var result = "";
    for (var i = 0; i < data["list_of_stars"].length ; i++){
    	var stLink = "/Project/Star/Star.html?" + "stdi=" + data["list_of_stid"][i];
    	result += "<a href=" + stLink + ">" + data["list_of_stars"][i] + "</a>";
    }
    return result;
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/Project/api/movie",
    data: {
        ACTION: "LIST",
        Page: "1",
        PageSize: "3",
        order: "tr"
    },
    success: function(resultData){
        handleListResult(resultData);
    },
    error: function(XMLHttpRequest, textStatus, errorThrown){
        alert(textStatus);
    }
});

function sendData(){
	var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + document.getElementById("searchbar").value + "&page=1&action=SEARCH&order=ta&ps=20";
	window.location.assign(newpage);
} 

$("#Previous").click(function(e) {
    e.preventDefault();
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "/Project/api/movie",
        data: {
            ACTION: "LIST",
            Page: getParameterByName('page'),
            PageSize: "20"
        },
        success: function(result) {
            $("#movieList_table tr").remove();
            handleListResult(result);
            var pageNum = parseInt(getParameterByName('page'));
            if(pageNum > 0)
                pageNum = pageNum - 1;
            window.location.assign("Main?page="+ pageNum.toString());
        },
        error: function(result) {
            alert('error');
        }
    });
});

$("#Next").click(function(e) {
    e.preventDefault();
    var pageNum = parseInt(getParameterByName('page'));
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "/Project/api/movie",
        data: {
            ACTION: "LIST",
            Page: pageNum,
            PageSize: "20"
        },
        success: function(result) {
            $("#movieList_table tr").remove();
            handleListResult(result);
            var pageNum = parseInt(getParameterByName('page'));
            pageNum = pageNum + 1;
            window.location.assign("Main?page="+ pageNum.toString());
        },
        error: function(result) {
            alert('error');
        }
    });
});

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return "0";
    if (!results[2]) return "0";
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$('#searchbar').autocomplete(
		{
			lookup : function(query, doneCallback) {
				console.log("Initiated");
				if (query in localStorage) {
					console.log("Cached")
					doneCallback({suggestions : JSON.parse(localStorage[query])});
					console.log(localStorage[query]);
				} else {
					console.log("Ajax")
					handleLookup(query, doneCallback);
				}
			},
			onSelect : function(suggestion) {
				handleSelectSuggestion(suggestion)
			},
			triggerSelectOnValidInput: false,
			groupBy : "category",
			deferRequestBy : 300,
			minChars: 3
});

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	
	jQuery.ajax({
		method : "GET",
		url : "/Project/api/movie",
		data: {
			ACTION: "SUG",
			keyWord: query
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
	console.log("lookup ajax successful")

	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	localStorage[query] = data;
	doneCallback({suggestions : jsonData});
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	console.log("you select " + suggestion["value"])
	var link = "";
	if (suggestion["data"]["category"] === "movie")
		link = "/Project/servlet/SingleMovie?" + "movieId=" + suggestion["data"]["id"];
	else
		link = "/Project/Star/Star.html?" + "stdi=" + suggestion["data"]["id"];
	window.location.assign(link);
}