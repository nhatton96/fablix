
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
            	window.location.replace("MovieList");	
            	//return false;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("Wrong email or password");
                console.log(textStatus);
                console.log(errorThrown);
                location.reload();
             }
        });
    });
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
