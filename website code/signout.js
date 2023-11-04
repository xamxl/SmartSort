let running = false;

window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";
    
    isLoggedIn().then(output => {
        if (output) {
            document.getElementsByName("signoutTag")[0].style.display = "block";
            document.getElementsByName("errorText")[0].innerHTML = "";
        } else {
            document.getElementsByName("errorText")[0].style.display = "none";
            document.getElementsByName("errorText")[0].innerHTML = "";
            document.getElementsByName("aLink")[0].style.display = "block";
            document.cookie = "loginKey=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        }
    });
}

document.getElementById('form-signout').addEventListener('submit', function(event) {
    event.preventDefault();

    if (running) {
        return;
    }

    document.getElementsByName("signoutTag")[0].value = "Signing you out ...";

    running = true;

    const formData = new FormData();
    formData.append("email", getCookie("email"));
    formData.append("key", getCookie("loginKey"));

    fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/signout', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        running = false;
        document.getElementsByName("errorText")[0].style.color = "green";
        document.getElementsByName("errorText")[0].innerHTML = "You have been signed out.";
        document.getElementsByName("signoutTag")[0].style.display = "none";
    })
    .catch(function(error) {
        document.getElementsByName("errorText")[0].style.color = "green";
        document.getElementsByName("errorText")[0].innerHTML = "You have been signed out.";
        document.getElementsByName("signoutTag")[0].style.display = "none";
        running = false;
        console.error('Request failed:', error.message);
    });

    document.cookie = "loginKey=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
});