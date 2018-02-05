var action = getParameterByName("action");
var page = getParameterByName("page");
var title = "0";
var director ="0";
var star = "0";
var year = "0";
var genre = "0"
if (action === "SEARCHADV"){
	title = getParameterByName("title");
    director = getParameterByName("director");
    star = getParameterByName("star");
    year = getParameterByName("year");
    searchAdv(action,page,title,director,star,year);
}
else if (action === "SEARCHGENRE"){
		genre = getParameterByName("genre");
		searchGenre(action,page,genre);
}
else if (action === "SEARCH" || action === "SEARCHTITLE"){
	title = getParameterByName("title");
	search(action,page,title);
}

function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");

    // populate the star table
    var movieTableBodyElement = jQuery("#movieList_table_body");
    for (var i = 0; i < resultData.length; i++) {
        var rowHTML = "";
        rowHTML += "<tr>"; // id='tableRows' class='clickable-row' data-href='"+resultData[i]["movieId"]+"'>";
        rowHTML += "<th>" + createMvLink(resultData[i]) + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["list_of_genres"] + "</th>";
        rowHTML += "<th>" + createStarLink(resultData[i]) + "</th>";
        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }
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

function searchAdv(action,page,title,director,star,year){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: "20", 
	          title: title,
	          director: director, 
	          star: star, 
	          year: year
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
}
function searchGenre(action,page,genre){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: "20", 
	          genre: genre
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
}

function search(action,page,title){
	jQuery.ajax({
		  dataType: "json",
		  method: "GET",
		  url: "/Project/api/movie",
		  data: {
	          ACTION: action,
	          Page: page,
	          PageSize: "20", 
	          title: title
	      },
		  success: function(resultData){
	          handleListResult(resultData);
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown){
		  	alert(textStatus);
	      }
	});
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
	"&page=" + pageNum + "&action=" + action;
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
	"&page=" + pageNum + "&action=" + action;
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