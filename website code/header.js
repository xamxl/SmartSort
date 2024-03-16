fetch('header.html')
  .then(response => response.text())
  .then(data => {
    document.getElementById('header').innerHTML = data;
    ["/index.html", "/page1.html", "/page2.html", "/output.html", "/clean.html", "/formCreator.html", "/login.html", "/create-account.html", "/signout.html", "/myAccount.html", "/myForms.html"].forEach((page) => {
      document.querySelectorAll(`a[href="${page}"]`).forEach((linkElement) => {
        linkElement.addEventListener('click', async function(event) {
          event.preventDefault();
          await saveForm(window.location.pathname.split("/").pop());
          window.location.href = linkElement.href;
        });
      });
    });

    if (/Android.+Mobile|webOS|iPhone|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) && window.location.href.endsWith("index.html")) {
      document.getElementById("otherOptions").style.display = "none";
      document.getElementById("navImage").width = "70";
      document.getElementById("navImage").height = "70";
    }
  });