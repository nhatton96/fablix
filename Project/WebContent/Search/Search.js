function sendData(){
	var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + document.getElementById("searchtitle").value +
	"&year=" + document.getElementById("searchyear").value +
	"&director=" + document.getElementById("searchdirector").value +
	"&star=" + document.getElementById("searchstar").value + "&page=1" + "&action=SEARCHADV" + "&order=ta&ps=20";
	window.location.assign(newpage);
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

function sendDataSearchBar(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1&action=SEARCH&order=ta&ps=20";
    window.location.assign(newpage);
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