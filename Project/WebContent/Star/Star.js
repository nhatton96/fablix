function handleListResult(resultData) {
    console.log("handleListResult: populating movie table from resultData");
    document.getElementById("name").innerHTML =  resultData[0]["name"];
    document.getElementById("by").innerHTML =  resultData[0]["birthYear"];
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/Project/api/star",
    data: {
        starId: "nm0000226"
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

