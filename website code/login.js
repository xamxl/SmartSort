let running = false;
let redirect = "";

window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";

    isLoggedIn().then(output => {
        if (output) {
            document.getElementsByName("errorText")[0].style.color = "green";
            document.getElementsByName("errorText")[0].innerHTML = "You are already logged in!";
        } else {
            document.getElementsByName("emailInput")[0].style.display = "block";
            document.getElementsByName("passwordInput")[0].style.display = "block";
            document.getElementsByName("loginTag")[0].style.display = "block";
            document.getElementsByName("cALink")[0].style.display = "block";
            document.getElementsByName("errorText")[0].innerHTML = "";
        }

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
        } else if (url.search.endsWith('prev=page2')) {
            redirect = "page2.html";
            url.searchParams.delete('prev');

            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "You must be logged in to run a sort.";
            
            window.history.replaceState({}, document.title, url.toString());
        }
    });
}

function getCookie(name) {
    const value = document.cookie.split('; ').find(row => row.startsWith(name + '='));
    return value ? value.split('=')[1] : false;
}

async function isLoggedIn() {
    if (!getCookie("loginKey")) {
        return false;
    }

    const formData = new FormData();
    formData.append("email", getCookie("email"));
    formData.append("key", getCookie("loginKey"));

    try {
        const response = await fetch('http://localhost:8080/verifyLogin', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            return false;
        }

        const result = await response.json();
        return result.text == "VALID";
        
    } catch (error) {
        return false;
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

    if (running) {
        return;
    }

    document.getElementsByName("loginTag")[0].value = "Logging you in ...";

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;

    fetch('http://localhost:8080/login', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Login failed. Please check your username and password.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            running = false;
            document.getElementsByName("loginTag")[0].value = "Login";
            if (result.text == "INVALID") {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = "Login failed. Please check your username and password.";
            } else {
                var expires = new Date();
                expires.setTime(expires.getTime() + (24 * 60 * 60 * 1000));
                document.cookie = "loginKey=" + result.text + "; expires=" + expires.toUTCString() + "; path=/;";
                document.cookie = "email=" + data.email + "; expires=" + expires.toUTCString() + "; path=/;";
                if (redirect != "") {
                    window.location.href = redirect;
                }
                document.getElementsByName("errorText")[0].style.color = "green";
                document.getElementsByName("errorText")[0].innerHTML = "Login successful! Welcome " + data.email + ".";
                document.getElementsByName("loginTag")[0].style.display = "none";
                document.getElementsByName("emailInput")[0].style.display = "none";
                document.getElementsByName("passwordInput")[0].style.display = "none";
                document.getElementsByName("cALink")[0].style.display = "none";
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("loginTag")[0].value = "Login";
        running = false;
        console.error('Request failed:', error.message);
    });

});