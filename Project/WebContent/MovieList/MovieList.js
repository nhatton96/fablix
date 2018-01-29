
function handleListResult(resultData) {
	console.log("handleListResult: populating movie table from resultData");

	// populate the star table
	var movieTableBodyElement = jQuery("#movieList_table_body");
	for (var i = 0; i < Math.min(10, resultData.length); i++) {
		var rowHTML = "";
		rowHTML += "<tr class='clickable-row' data-href='"+resultData[i]["movieId"]+"'>";
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
          ACTION: "LIST",
          Page: "1",
          PageSize: "20"
      },
	  success: function(resultData){
          handleListResult(resultData);
	  },
	  error: function(XMLHttpRequest, textStatus, errorThrown){
	  	alert(textStatus);
      }
});

jQuery(document).ready(function($) {
    $(".clickable-row").click(function() {
        var movieId = $(this).data("href");
        //alert(movieId);
        window.location.replace("SingleMovie?movieId="+movieId);
        //window.location = $(this).data("href");
    });
});