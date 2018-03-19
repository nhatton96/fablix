var action = getParameterByName("action");
var page = getParameterByName("page");
var title = "0";
var director ="0";
var star = "0";
var year = "0";
var genre = "0";
var order = getParameterByName("order");
var pagesize = getParameterByName("ps");
document.getElementById("pagesize").innerHTML = pagesize;
if (action === "SEARCHADV"){
	title = getParameterByName("title");
    director = getParameterByName("director");
    star = getParameterByName("star");
    year = getParameterByName("year");
    searchAdv(action,page,title,director,star,year,order,pagesize);
}
else if (action === "SEARCHGENRE"){
		genre = getParameterByName("genre");
		searchGenre(action,page,genre,order,pagesize);
}
else if (action === "SEARCH" || action === "SEARCHTITLE"){
	title = getParameterByName("title");
	search(action,page,title,order,pagesize);
}
else if (action === "LIST"){
    title = getParameterByName("title");
    getList(action,page,title,order,pagesize);
}

// sample
//var cartList = {
//	    'cart': [{'movieId': "tt0424773"},
//	    	{'movieId': "tt0349955"},
//	    	{'movieId': "tt0395642"}]
//	};
//
//jQuery.ajax({
//		  dataType: "json",
//		  method: "GET",
//		  url: "/Project/api/movie",
//		  data: {
//	          ACTION: "SEARCHLIST",
//	          Page: 1,
//	          PageSize: "20", 
//	          cartList: JSON.stringify(cartList)
//	      },
//		  success: function(resultData){
//	          handleListResult(resultData);
//		  },
//		  error: function(XMLHttpRequest, textStatus, errorThrown){
//		  	alert(textStatus);
//	      }
//	});

function sortBy(neworder){
    var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + title +
	"&year=" + year +
	"&director=" + director +
	"&star=" + star + 
	"&genre=" + genre +
	"&page=1" + "&action=" + action + "&order=" + neworder + "&ps=" + pagesize;
	 window.location.assign(newpage);
}

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

//(function(){
//    var loggedIn = sessionStorage.getItem("LoggedIn");
//    if(loggedIn !== "true")
//        window.location.assign("Login");
//})();

function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
        "?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta" + "&ps=20";
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

function searchAdv(action,page,title,director,star,year,order,pagesize){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: pagesize, 
	          title: title,
	          director: director, 
	          star: star, 
	          year: year,
	          order: order
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
}
function searchGenre(action,page,genre,order,pagesize){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: pagesize, 
	          genre: genre,
	          order: order
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
}

function search(action,page,title,order,pagesize){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: pagesize, 
	          title: title,
	          order: order
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
}

function getList(action,page,title,order,pagesize){
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "/Project/api/movie",
        data: {
            ACTION: action,
            Page: page,
            PageSize: pagesize,
            title: title,
            order: order
        },
        success: function(resultData){
            handleListResult(resultData);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(textStatus);
        }
    });
}
function resize(nps){
	var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + title +
	"&year=" + year +
	"&director=" + director +
	"&star=" + star + 
	"&genre=" + genre +
	"&page=" + page + "&action=" + action + "&order=" + order + "&ps=" + nps;
	 window.location.assign(newpage);
}

$("#Previous").click(function(e) {
    e.preventDefault();

    var pageNum = parseInt(page);
    if (pageNum > 1)
    pageNum -= 1;
    var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + title +
	"&year=" + year +
	"&director=" + director +
	"&star=" + star + 
	"&genre=" + genre +
	"&page=" + pageNum + "&action=" + action + "&order=" + order + "&ps=" + pagesize;
	 window.location.assign(newpage);
});

$("#Next").click(function(e) {
    e.preventDefault();
    
    var pageNum = parseInt(page);
    pageNum += 1;
    var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + title +
	"&year=" + year +
	"&director=" + director +
	"&star=" + star + 
	"&genre=" + genre +
	"&page=" + pageNum + "&action=" + action  + "&order=" + order + "&ps=" + pagesize;
	window.location.assign(newpage);
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