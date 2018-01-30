
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
        rowHTML += "</tr>"
        movieTableBodyElement.append(rowHTML);
    }
}

// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/Project/api/movie",
    data: {
        ACTION: "SINGLE",
        Star: getParameterByName('starId')
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

