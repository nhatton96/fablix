

(function(){
    var loggedIn = sessionStorage.getItem("LoggedIn");
    if(loggedIn !== "true")
        window.location.assign("Login");
})();



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
            Page:1,
            PageSize:20,
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
}