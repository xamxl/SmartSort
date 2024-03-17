let running = false;
let fetched = false;
let sorting = false;

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
            document.getElementsByName("errorText")[0].innerHTML = "Please wait while we fetch your forms.";

            var formData = new FormData();

            formData.append("email", getCookie("email"));
            formData.append("key", getCookie("loginKey"));

            fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/getMyForms', {
                method: 'POST', 
                body: formData
            })
            .then(function(response) {
                if (!response.ok) {
                    document.getElementsByName("errorText")[0].style.color = "red";
                    document.getElementsByName("errorText")[0].innerHTML = "Fetching your forms was not successful.";
                    throw new Error('HTTP error, status = ' + response.status);
                }
                response.json().then((result) => {
                    document.getElementsByName("errorText")[0].innerHTML = "";
                    fetched = true;
                    if (result.hasOwnProperty('text')) {
                        window.location.href = "login.html?prev=myForms&mes=You_must_be_logged_in_to_view_your_forms.";
                    } else {
                        result.myForms.forEach((formName) => {
                            const container = document.querySelector('[name="formContainer"]');
                            container.innerHTML += `
                                <div class="col-md-6" id="${formName}">
                                    <div class="form-entry d-flex align-items-center justify-content-between">
                                        <h3 style="margin-right:15px;">${formName}</h3>
                                        <div>
                                            <button class="btn btn-primary btn-sm sort" style="outline: none;box-shadow: none;">Sort</button>
                                            <button class="btn btn-primary btn-sm view-data" style="outline: none;box-shadow: none;">View Data</button>
                                            <button class="btn btn-primary btn-sm copy-link" style="outline: none;box-shadow: none;">Copy Link</button>
                                            <button class="btn btn-danger btn-sm delete-btn" data-click-count="0" style="outline: none;box-shadow: none;">Delete</button>
                                        </div>
                                    </div>
                                </div>`;
                        });
                    }
                });
            })
            .catch(function(error) {
                console.error('Request failed:', error.message);
            });
        }
    });
}

// Event delegation for delete buttons
document.querySelector('[name="formContainer"]').addEventListener('click', function(event) {
    if (sorting) {
        document.getElementsByName("errorText")[0].innerHTML = "Already fetching your data.";
        document.getElementsByName("errorText")[0].style.color = "red";
        return;
    }
    const btn = event.target;
    if (event.target.classList.contains('delete-btn')) {
        let clickCount = parseInt(btn.getAttribute('data-click-count'));
        if (clickCount === 0) {
            // First click
            btn.setAttribute('data-click-count', '1');
            btn.classList.add('clicked-once'); // Add a class to change the visual appearance
            btn.textContent = 'Confirm Delete';
        } else {
            if (running) {
                document.getElementsByName("errorText")[0].innerHTML = "Already deleting one of your forms.";
                document.getElementsByName("errorText")[0].style.color = "red";
                return;
            }
            btn.textContent = 'Deleting ...';
            btn.classList.add('deleting');
            running = true;
            // Delete & if bad response or try again then error
            var formData = new FormData();

            formData.append("email", getCookie("email"));
            formData.append("key", getCookie("loginKey"));
            formData.append("formName", btn.closest('.form-entry').querySelector('h3').textContent);

            fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/deleteForm', {
                method: 'POST', 
                body: formData
            })
            .then(function(response) {
                if (!response.ok) {
                    document.getElementsByName("errorText")[0].style.color = "red";
                    document.getElementsByName("errorText")[0].innerHTML = "Deleting your form was not successful.";
                    throw new Error('HTTP error, status = ' + response.status);
                }
                response.json().then((result) => {
                    running = false;
                    document.getElementsByName("errorText")[0].innerHTML = "";
                    if (result.hasOwnProperty('text')) {
                        window.location.href = "login.html?prev=myForms&mes=You_must_be_logged_in_to_delete_a_form.";
                    } else {
                        document.getElementsByName("errorText")[0].style.color = "green";
                        document.getElementsByName("errorText")[0].innerHTML = "Your form has been deleted.";
                        document.getElementById(btn.closest('.form-entry').querySelector('h3').textContent).style.display = "none";
                    }
                });
            })
            .catch(function(error) {
                running = false;
                console.error('Request failed:', error.message);
            });
        }
    } else if (event.target.classList.contains('copy-link')) {
        const userEmail = encodeURIComponent(getCookie("email"));
        const formName = encodeURIComponent(btn.closest('.form-entry').querySelector('h3').textContent);
        const text = `https://smartsort.site//fillOutForm.html?user=${userEmail}&formName=${formName}`;
        copyToClipboard(text);
        btn.textContent = "Link Copied";
    } else if (event.target.classList.contains('view-data')) {
        const formName = encodeURIComponent(btn.closest('.form-entry').querySelector('h3').textContent);
        const text = `https://smartsort.site/viewSubmissions.html?formName=${formName}`;
        window.open(text, '_blank');
    } else if (event.target.classList.contains('sort')) {
        btn.textContent = 'Fetching ...';
        sorting = true;
        
        // Fetch data and put individual data in json array of arrays in local storage + set the other local storage values
        var formData = new FormData();
        formData.append("email", getCookie("email"));
        formData.append("formName", btn.closest('.form-entry').querySelector('h3').textContent);
        
        fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/getForm', {
                method: 'POST', 
                body: formData
        })
        .then(function(response) {
            if (!response.ok) {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = "Fetching your data was not successful.";
                sorting = false;
                btn.textContent = 'Sort';
                throw new Error('HTTP error, status = ' + response.status);
            }
            response.json().then((result) => {
                document.getElementsByName("errorText")[0].innerHTML = "";
                if (result.hasOwnProperty('text')) {
                    document.getElementsByName("errorText")[0].style.color = "red";
                    document.getElementsByName("errorText")[0].innerHTML = "This form does not exist.";
                    sorting = false;
                    btn.textContent = 'Sort';
                } else {
                    let tags = [
                        "Ranked choices for desired locations",
                        "Ranked choices for not desired locations",
                        "Unranked choices for desired locations",
                        "Unranked choices for not desired locations",
                        "Ranked choices for individuals to be with",
                        "Ranked choices for individuals not to be with",
                        "Unranked choices for individuals to be with",
                        "Unranked choices for individuals not to be with",
                        "Attributes to balance",
                        "Attributes to split by",
                        "Attributes to not isolate",
                        "Attributes to isolate"];

                    let countMap = {};
                    let savedSortTypes = result.sortTypes;
                    let savedHeaders = result.texts;

                    result.sortTypes.forEach(type => {
                        countMap[type] = (countMap[type] || 0) + 1;
                    });
                    
                    const values = new Map();
                    Object.keys(countMap).forEach((key, index) => {
                        if (tags.includes(key)) {
                            values.set("value" + (tags.indexOf(key) + 1), countMap[key]);
                        }
                    });
                    
                    const obj = Object.fromEntries(values);
                    localStorage.setItem('page1Data', JSON.stringify(obj));

                    formData.append("key", getCookie("loginKey"));

                    fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/getSubmissions', {
                        method: 'POST', 
                        body: formData
                    })
                    .then(function(response) {
                        if (!response.ok) {
                            document.getElementsByName("errorText")[0].style.color = "red";
                            document.getElementsByName("errorText")[0].innerHTML = "Fetching your data was not successful.";
                            sorting = false;
                            btn.textContent = 'Sort';
                            throw new Error('HTTP error, status = ' + response.status);
                        }
                        response.json().then((result) => {
                            if (result.text == "INVALID_LOGIN") {
                                window.location.href = "login.html?prev=myForms&mes=You_must_be_logged_in_to_use_your_form_data.";
                            } else if (result.text == "INVALID_FORM") {
                                document.getElementsByName("errorText")[0].style.color = "red";
                                document.getElementsByName("errorText")[0].innerHTML = "This form does not exist.";
                                sorting = false;
                                btn.textContent = 'Sort';
                            } else {
                                // data here
                                let arrayOfArrays = result.map(item => item.data);
                                // savedSortTypes contains the titles
                                // tags contains the right order

                                let ordered = [["Name"]];
                                savedHeaders.forEach(header => {
                                    ordered[0].push(header);
                                });

                                arrayOfArrays.forEach((identifier) => {
                                    ordered.push([identifier[0]]);
                                });

                                tags.forEach((type) => {
                                    for (let i = 0; i < savedSortTypes.length; i++) {
                                        if (type == savedSortTypes[i]) {
                                            for (let v = 1; v < ordered.length; v++) {
                                                ordered[v].push(arrayOfArrays[v-1][i+1]);
                                            }
                                        }
                                    }
                                });

                                ordered = JSON.stringify(ordered);
                                let toStore = {"usingForm":"true", "file1":ordered, "file1Name":btn.closest('.form-entry').querySelector('h3').textContent};
                                localStorage.setItem('page2Data', JSON.stringify(toStore));
                                
                                window.location.href = "page2.html";
                            }
                        });
                    })
                    .catch(function(error) {
                        console.error('Request failed:', error.message);
                    });
                }
            });
        })
        .catch(function(error) {
            console.error('Request failed:', error.message);
        });
    }
});

// Reset the delete button when clicking outside
document.addEventListener('click', function(event) {
    const deleteButtons = document.querySelectorAll('.delete-btn.clicked-once');
    if (fetched && ! sorting) {
        document.getElementsByName("errorText")[0].innerHTML = "";
    }
    deleteButtons.forEach(btn => {
        if (btn.textContent != 'Deleting ...' && event.target != btn) {
            resetDeleteButton(btn);
        }
    });
    const copyLinkButtons = document.querySelectorAll('.copy-link');
    copyLinkButtons.forEach(btn => {
        if (event.target != btn) {
            btn.textContent = "Copy Link";
        }
    });
});

function resetDeleteButton(btn) {
    btn.setAttribute('data-click-count', '0');
    btn.classList.remove('clicked-once');
    btn.textContent = 'Delete';
}

function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {}).catch(err => {});
  }