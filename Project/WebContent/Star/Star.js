function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");
    document.getElementById("name").innerHTML =  resultData[0]["name"];
    document.getElementById("by").innerHTML =  resultData[0]["birthYear"];
    var res = "";
    for (var i = 0; i < resultData[0]["movieNames"].length ; i++){
    	res += createLink(resultData[0]["movieNames"][i],resultData[0]["movieId"][i])
    }
    document.getElementById("mv").innerHTML =  res;
}

function createLink(name,id){
	var mvLink = "/Project/servlet/SingleMovie?" + "movieId=" + id;
	return "<a href=" + mvLink + ">" + name + "</a>";
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