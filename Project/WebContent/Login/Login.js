/*
$(document).ready(function() {
	$('#LoginForm').submit(function() {
        $.ajax({
            type: "POST",
            url: '/Project/api/login',
            data: {
                email: $("#email").val(),
                password: $("#password").val()
            },
            success: function(response)
            {
            	//alert(response);
            	console.log("Logging in");
            	//response.preventDefault();
            	window.location.replace("Movies");
            	//return false;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                //alert("Wrong email or password");
                console.log(textStatus);
                console.log(errorThrown);
                location.reload();
             }
        });
    });
});*/

$('#LoginForm').submit(function(e) {
    e.preventDefault();
    var response = grecaptcha.getResponse();
    /*
    $.ajax({
        type: "POST",
        url: 'https://www.google.com/recaptcha/api/siteverify',
        data: {
            secret:"6LepTkcUAAAAAM17Buchaex7blIDkFKoaVeSVgSU",
            response: response,
        },
        success: function(response)
        {

        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("Wrong email or password");
            console.log(textStatus);
            console.log(errorThrown);
            location.reload();
        }
    });*/


    if(response.length != 0){
        $.ajax({
            type: "POST",
            url: '/Project/api/login',
            data: {
                type: "customer",
                email: $("#email").val(),
                password: $("#password").val()
            },
            success: function(response)
            {
                //alert(response);
                console.log("Logging in");
                //response.preventDefault();
                var newCart = [];
                localStorage.setItem('cart', JSON.stringify(newCart));
                sessionStorage.setItem("LoggedIn","true");
                window.location.assign("Main?page=0");
                //return false;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Wrong email or password");
                console.log(textStatus);
                console.log(errorThrown);
                location.reload();
            }
        });
    }
    else{
        alert("Please verify you are not a robot :)")
    }


});

/*
$("#LoginForm").on("submit",function(e) {
	   e.preventDefault(); // cancel submission
	   window.location.replace("/servlet/MovieList");
	   
	});*/

function goToMainPage() {
	//e.preventDefault(); // cancel submission
	//window.location.replace("/servlet/MovieList");	
}
