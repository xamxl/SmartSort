let running = false;

window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";

    isLoggedIn().then(output => {
        if (output) {
            document.getElementsByName("errorText")[0].style.color = "green";
            document.getElementsByName("errorText")[0].innerHTML = "You are already logged in!";
        } else {
            document.getElementsByName("emailInput")[0].style.display = "block";
            document.getElementsByName("password1Input")[0].style.display = "block";
            document.getElementsByName("password2Input")[0].style.display = "block";
            document.getElementsByName("createAccountTag")[0].style.display = "block";
            document.getElementsByName("lILink")[0].style.display = "block";
            document.getElementsByName("errorText")[0].innerHTML = "";
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

document.getElementById('form-create-account').addEventListener('submit', function(event) {
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
    if (data.confirmPassword == "") {
        document.querySelector("label[name='confirmPasswordLabel']").style.color = "red";
        document.querySelector("label[name='confirmPasswordLabel']").textContent = "Confirm Password: Field Required";
    } else {
        document.querySelector("label[name='confirmPasswordLabel']").style.color = "black";
        document.querySelector("label[name='confirmPasswordLabel']").textContent = "Confirm Password:";
    }

    if (data.email == "" || data.password == "" || data.confirmPassword == "") {
        return;
    }

    if (running) {
        return;
    }

    document.getElementsByName("createAccountTag")[0].value = "Creating your account ...";

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;

    fetch('http://localhost:8080/createAccount', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Creating your account was not successful.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            document.getElementsByName("createAccountTag")[0].value = "Create Account";
            var parsed = result;
            running = false;
            if (parsed.text == "Account created.") {
                window.location.href = "login.html?ac=true";
            } else {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = parsed.text;
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("createAccountTag")[0].value = "Create Account";
        running = false;
        console.error('Request failed:', error.message);
    });

});