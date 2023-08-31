window.onload = function() {
    // Get the current URL
    var url = new URL(window.location.href);

    // Check if 'ac=true' is at the end of the URL
    if (url.search.endsWith('ac=true')) {
        // If so, remove the 'ac' parameter
        url.searchParams.delete('ac');

        document.getElementsByName("errorText")[0].style.color = "green";
        document.getElementsByName("errorText")[0].innerHTML = "Account created.";

        // Redirect to the new URL without 'ac=true'
        window.history.replaceState({}, document.title, url.toString());
    }
}

document.getElementById('form-login').addEventListener('submit', function(event) {
    event.preventDefault();

    var formData = new FormData(event.target);
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    if (data.email == "") {
        document.querySelector("label[name='emailLabel']").style.color = "red";
        document.querySelector("label[name='emailLabel']").textContent = "Email address: Field Required";
    } else {
        document.querySelector("label[name='emailLabel']").style.color = "black";
        document.querySelector("label[name='emailLabel']").textContent = "Email address:";
    }
    if (data.password == "") {
        document.querySelector("label[name='passwordLabel']").style.color = "red";
        document.querySelector("label[name='passwordLabel']").textContent = "Password: Field Required";
    } else {
        document.querySelector("label[name='passwordLabel']").style.color = "black";
        document.querySelector("label[name='passwordLabel']").textContent = "Password:";
    }

    // Login with server

});