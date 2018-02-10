function sendData(){
    var newpage = "/Project/MovieList/MovieList.html" +
    "?title=" + document.getElementById("searchbar").value + "&page=1" + "&action=SEARCH" + "&order=ta" + "&ps=20";
    window.location.assign(newpage);
}

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();

<<<<<<< HEAD
function sendData2(){
	window.location.assign("Confirmation");
=======
function sendData(){
    var firstName = document.getElementById("firstName").value;
    var lastName = document.getElementById("lastName").value;
    var cardNumber = document.getElementById("cardNumber").value;
    var cardExpiration = document.getElementById("cardExpiration").value;
    var address = document.getElementById("address").value;
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    jQuery.ajax({
        dataType: "json",
        method: "Get",
        url: "/Project/api/movie",
        data: {
            ACTION: "cred",
            FirstName: firstName,
            LastName: lastName,
            Address: address,
            CardNumber: cardNumber,
            CardExpiration: cardExpiration,
            Cart: JSON.stringify(cart)
        },
        success: function(resultData){
            if(resultData[0] === "yes")
                window.location.assign("Confirmation?isValid=true");
            else
                window.location.assign("Confirmation?isValid=false");
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(textStatus);
        }
    });

>>>>>>> ad476d57d2a4af9b870c0464784239c049e188a4
}