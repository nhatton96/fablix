
(function(){
    localStorage.setItem('cart', JSON.stringify(emptyCart));
})();

function GoHome() {
    window.location.assign("Main");
}