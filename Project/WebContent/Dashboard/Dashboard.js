(function(){
    var loggedIn = sessionStorage.getItem("EmployeeLoggedIn");
    if(loggedIn !== "true")
        window.location.assign("_dashboard");
})();

function sendMovie() {
	jQuery.ajax({
		dataType : "json",
		method : "GET",
		url : "/Project/api/add",
		data : {
			action : "movie",
			title : document.getElementById("addtitle").value,
			director : document.getElementById("adddirector").value,
			star : document.getElementById("addstar").value,
			year : document.getElementById("addyear").value,
			genre : document.getElementById("addgenre").value
		},
		success : function(response) {
			alert("Succeed");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(textStatus);
			console.log(textStatus);
			console.log(errorThrown);
			// location.reload();
		}
	});
}

function sendStar() {
	jQuery.ajax({
		dataType : "json",
		method : "GET",
		url : "/Project/api/add",
		data : {
			action : "star",
			star : document.getElementById("starname").value,
			year : document.getElementById("staryear").value
		},
		success : function(response) {
			alert("Succeed");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(textStatus);
			console.log(textStatus);
			console.log(errorThrown);
			// location.reload();
		}
	});
}
function handleListResult(resultData) {
	var movieTableBodyElement = jQuery("#movieList_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<th>" + resultData[i]["name"] + "</th>";
		rowHTML += "<th>" + resultData[i]["att"][0] + "</th>";
		rowHTML += "<th>" + resultData[i]["atttype"][0] + "</th>";
		rowHTML += "</tr>";
		movieTableBodyElement.append(rowHTML);
		for (var z = 1; z < resultData[i]["att"].length; ++z) {
			var rowHTML = "";
			rowHTML += "<tr>";
			rowHTML += "<th> </th>";
			rowHTML += "<th>" + resultData[i]["att"][z] + "</th>";
			rowHTML += "<th>" + resultData[i]["atttype"][z] + "</th>";
			rowHTML += "</tr>";
			movieTableBodyElement.append(rowHTML);
		}
	}
}

jQuery.ajax({
	dataType : "json",
	method : "GET",
	url : "/Project/api/add",
	data : {
		action : "table"
	},
	success : function(resultData) {
		handleListResult(resultData);
	},
	error : function(XMLHttpRequest, textStatus, errorThrown) {
		alert(textStatus);
	}
});