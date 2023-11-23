window.onload = function() {
    document.getElementsByName("errorText")[0].style.color = "black";
    document.getElementsByName("errorText")[0].innerHTML = "Please wait while we check if you are logged in.";
    
    isLoggedIn().then(output => {
        if (! output) {
            document.getElementsByName("errorText")[0].style.display = "none";
            document.getElementsByName("errorText")[0].innerHTML = "";
            document.getElementsByName("aLink")[0].style.display = "block";
            document.cookie = "loginKey=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        } else {
            document.getElementsByName("errorText")[0].style.color = "black";
            document.getElementsByName("errorText")[0].innerHTML = "Please wait while we fetch your forms.";

            var formData = new FormData();

            formData.append("email", getCookie("email"));
            formData.append("key", getCookie("loginKey"));

            fetch('http://localhost:8080/getMyForms', {
                method: 'POST', 
                body: formData
            })
            .then(function(response) {
                if (!response.ok) {
                    document.getElementsByName("errorText")[0].color = "red";
                    document.getElementsByName("errorText")[0].innerHTML = "Fetching your forms was not successful.";
                    throw new Error('HTTP error, status = ' + response.status);
                }
                response.json().then((result) => {
                    document.getElementsByName("errorText")[0].style.display = "none";
                    if (result.hasOwnProperty('text')) {
                        window.location.href = "login.html?prev=myForms&mes=You_must_be_logged_in_to_view_your_forms.";
                    } else {
                        result.myForms.forEach((formName) => {
                            const container = document.querySelector('[name="formContainer"]');
                            container.innerHTML += `
                                <div class="col-md-6">
                                    <div class="form-entry d-flex align-items-center justify-content-between">
                                        <h3>${formName}</h3>
                                        <div>
                                            <button class="btn btn-primary btn-sm">Copy Link</button>
                                            <button class="btn btn-danger btn-sm delete-btn" data-click-count="0">Delete</button>
                                        </div>
                                    </div>
                                </div>`;
                        });
                    }
                });
            })
            .catch(function(error) {
                document.getElementsByName("runTag")[0].value = "Run >";
                running = false;
                console.error('Request failed:', error.message);
            });
        }
    });
}

// Event delegation for delete buttons
document.querySelector('[name="formContainer"]').addEventListener('click', function(event) {
    if (event.target.classList.contains('delete-btn')) {
        const btn = event.target;
        let clickCount = parseInt(btn.getAttribute('data-click-count'));

        if (clickCount === 0) {
            // First click
            btn.setAttribute('data-click-count', '1');
            btn.classList.add('clicked-once'); // Add a class to change the visual appearance
            btn.textContent = 'Confirm Delete';
        } else {
            // Second click
            //btn.closest('.form-entry').querySelector('h3').textContent)
        }
    }
});

// Reset the delete button when clicking outside
document.addEventListener('click', function(event) {
    if (!event.target.classList.contains('delete-btn')) {
        const deleteButtons = document.querySelectorAll('.delete-btn.clicked-once');
        deleteButtons.forEach(btn => resetDeleteButton(btn));
    }
});

function resetDeleteButton(btn) {
    btn.setAttribute('data-click-count', '0');
    btn.classList.remove('clicked-once');
    btn.textContent = 'Delete';
}