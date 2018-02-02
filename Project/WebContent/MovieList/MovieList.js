
function handleListResult(resultData) {
	console.log("handleListResult: populating movie table from resultData");

	// populate the star table
	var movieTableBodyElement = jQuery("#movieList_table_body");
	for (var i = 0; i < resultData.length; i++) {
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

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/Project/api/movie",
	  data: {
          ACTION: "LIST",
          Page: getParameterByName('page'),
          PageSize: "20"
      },
	  success: function(resultData){
          handleListResult(resultData);
	  },
	  error: function(XMLHttpRequest, textStatus, errorThrown){
	  	alert(textStatus);
      }
});

// for searching
//function searchAdv(){
//	jQuery.ajax({
//		  dataType: "json",
//		  method: "GET",
//		  url: "/Project/api/movie",
//		  data: {
//	          ACTION: "SEARCHADV",
//	          Page: 1,
//	          PageSize: "1", // for test, actualy would be getParameterByName
//	          title: "0",  // for test, actualy would be getParameterByName
//	          director: "0", // for test, actualy would be getParameterByName
//	          star: "Will Smith", // for test, actualy would be getParameterByName
//	          year: "0" // for test, actualy would be getParameterByName
//	      },
//		  success: function(resultData){
//	          handleListResult(resultData);
//		  },
//		  error: function(XMLHttpRequest, textStatus, errorThrown){
//		  	alert(textStatus);
//	      }
//	});
//}
//searchAdv();

jQuery(document).ready(function($) {
    $(".clickable-row").click(function(e) {
        e.preventDefault();
        var movieId = $(this).data("href");
        //alert(movieId);
        window.location.assign("SingleMovie?movieId="+movieId);
        //window.location = $(this).data("href");
    });
});

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
            window.location.assign("Movies?page="+ pageNum.toString());
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
            window.location.assign("Movies?page="+ pageNum.toString());
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
    if (!results) return '0';
    if (!results[2]) return '0';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}