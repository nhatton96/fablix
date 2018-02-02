function sendData(){
	var newpage = "/Project/MovieList/MovieList.html" + 
	"?title=" + document.getElementById("searchtitle").value +
	"&year=" + document.getElementById("searchyear").value +
	"&director=" + document.getElementById("searchdirector").value +
	"&star=" + document.getElementById("searchstar").value + "&page=1";
	window.location.assign(newpage);
}