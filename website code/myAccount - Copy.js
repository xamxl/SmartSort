let runningDeleteAccount = false;

window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";
    
    isLoggedIn().then(output => {
        if (output) {
            document.getElementById("form-deleteAccount").style.display = "block";
            document.getElementsByName("errorText")[0].innerHTML = "";
        } else {
            document.getElementsByName("errorText")[0].style.display = "none";
            document.getElementsByName("errorText")[0].innerHTML = "";
            document.getElementsByName("aLink")[0].style.display = "block";
        }
    });
}

document.getElementById('form-deleteAccount').addEventListener('submit', function(event) {
    event.preventDefault();

    var formData = new FormData(event.target);
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    if (data.email == "") {
        document.querySelector("label[name='emailLabel']").style.color = "red";
        document.querySelector("label[name='emailLabel']").textContent = "Confirm your email to delete your account: Field Required";
        return;
    } else {
        document.querySelector("label[name='emailLabel']").style.color = "black";
        document.querySelector("label[name='emailLabel']").textContent = "Confirm your email to delete your account:";
    }

    if (runningDeleteAccount) {
        return;
    }

    document.getElementsByName("deleteAccountTag")[0].value = "Deleting your account ...";

    document.getElementsByName("errorText1")[0].style.display = "none";

    runningDeleteAccount = true;

    formData.append("key", getCookie("loginKey"));

    fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/deleteAccount', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        console.log("responded");
        response.json().then((result) => {
            runningDeleteAccount = false;
            if (result.text != "INVALID") {
                document.getElementsByName("errorText")[0].style.color = "green";
                document.getElementsByName("errorText")[0].innerHTML = "Your account has been deleted.";
                document.getElementById("form-deleteAccount").style.display = "none";
                document.cookie = "loginKey=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
                document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            } else {
                document.getElementsByName("errorText1")[0].style.color = "red";
                document.getElementsByName("errorText1")[0].innerHTML = "Your entered email did not match.";
                document.getElementsByName("deleteAccountTag")[0].value = "Delete Your Account";
                document.getElementsByName("errorText1")[0].style.display = "block";
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("deleteAccountTag")[0].style.display = "Delete Your Account";
        runningDeleteAccount = false;
        console.error('Request failed:', error.message);
    });

});