function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");
    document.getElementById("name").innerHTML =  resultData[0]["name"];
    document.getElementById("by").innerHTML =  resultData[0]["birthYear"];
    var res = "";
    for (var i = 0; i < resultData[0]["movieNames"].length ; i++){
    	res += createLink(resultData[0]["movieNames"][i],resultData[0]["movieId"][i])
    }
    document.getElementById("mv").innerHTML =  res;
    /*
    for (var i = 0; i < resultData.length; i++) {
        var rowHTML = "";
        rowHTML += "<tr>"; // id='tableRows' class='clickable-row' data-href='"+resultData[i]["movieId"]+"'>";
        rowHTML += "<th>" + resultData[0]["name"] + "</th>";
        rowHTML += "<th>" + resultData[0]["birthYear"] + "</th>";

        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }*/
}

function createLink(name,id){
	var mvLink = "/Project/servlet/SingleMovie?" + "movieId=" + id;
	return "<a href=" + mvLink + ">" + name + "</a>";
}

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1&action=SEARCH&order=ta&ps=20";
    window.location.assign(newpage);
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/Project/api/star",
    data: {
        starId: getParameterByName("stdi")
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