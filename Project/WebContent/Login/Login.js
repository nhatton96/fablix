
$(document).ready(function() {
	$('#LoginForm').submit(function() {
		console.log("In post method");
        $.ajax({
            type: "POST",
            url: '/Project/api/login',
            data: {
                email: $("#email").val(),
                password: $("#password").val()
            },
            success: function(data)
            {
            	onsole.log("Logging in");
            	windows.location("/servlet/MovieList");	
            }
        });
    });
});


function goToMainPage() {
	console.log("Logging in");
	windows.location("/servlet/MovieList");	
}
