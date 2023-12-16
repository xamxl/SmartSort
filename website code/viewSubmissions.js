window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";

    isLoggedIn().then(output => {
        if (! output) {
            document.getElementsByName("errorText")[0].innerHTML = "";
            document.getElementsByName("aLink")[0].style.display = "block";
            document.cookie = "loginKey=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        } else {
            document.getElementsByName("errorText")[0].style.color = "black";
            document.getElementsByName("errorText")[0].innerHTML = "Please wait while we fetch your data.";

            var formData = new FormData();

            formData.append("email", getCookie("email"));
            formData.append("key", getCookie("loginKey"));
            formData.append("formName", getQueryParam("formName"));
        }
    });
}