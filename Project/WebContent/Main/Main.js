function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");

    // populate the star table
    var movieTableBodyElement = jQuery("#movieList_table_body");
    for (var i = 0; i < resultData.length; i++) {
        var rowHTML = "";
        rowHTML += "<tr>"; // id='tableRows' class='clickable-row' data-href='"+resultData[i]["movieId"]+"'>";
        rowHTML += "<th>" + resultData[i]["Id"] + "</th>";
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
        Page: "1", // getParameterByName('page'),
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
	"?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta";
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