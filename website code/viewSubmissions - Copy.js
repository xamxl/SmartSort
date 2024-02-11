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
            document.getElementsByName("errorText")[0].innerHTML = "Please wait while we fetch your form.";

            var formData = new FormData();

            formData.append("email", getCookie("email"));
            formData.append("formName", getQueryParam("formName"));

            fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/getForm', {
                method: 'POST', 
                body: formData
            })
            .then(function(response) {
                if (!response.ok) {
                    document.getElementsByName("errorText")[0].style.color = "red";
                    document.getElementsByName("errorText")[0].innerHTML = "Fetching your data was not successful.";
                    throw new Error('HTTP error, status = ' + response.status);
                }
                response.json().then((result) => {
                    document.getElementsByName("errorText")[0].innerHTML = "";
                    if (result.hasOwnProperty('text')) {
                        document.getElementsByName("errorText")[0].style.color = "red";
                        document.getElementsByName("errorText")[0].innerHTML = "This form does not exist.";
                    } else {
                        // Find the table by its ID
                        let table = document.getElementById("submissionsTable");

                        // Create a row for the header
                        let headerRow = document.createElement("tr");

                        // Add 'idInstruct' as the first header
                        let instructHeader = document.createElement("th");
                        instructHeader.textContent = result.idInstruct;
                        instructHeader.scope = "col";
                        headerRow.appendChild(instructHeader);

                        // Add headers from 'texts'
                        result.texts.forEach(text => {
                            let header = document.createElement("th");
                            header.textContent = text;
                            header.scope = "col";
                            headerRow.appendChild(header);
                        });

                        // Append the header row to the table header
                        table.querySelector("thead").appendChild(headerRow);
                        document.getElementsByClassName("text-center")[0].innerHTML = "Submissions For: " + result.formName;

                        document.getElementsByName("errorText")[0].style.color = "black";
                        document.getElementsByName("errorText")[0].innerHTML = "Please wait while we fetch your submissions.";
                        
                        formData.append("key", getCookie("loginKey"));

                        fetch('https://smartsortclean-culsd3w6ha-uw.a.run.app/getSubmissions', {
                            method: 'POST', 
                            body: formData
                        })
                        .then(function(response) {
                            if (!response.ok) {
                                document.getElementsByName("errorText")[0].style.color = "red";
                                document.getElementsByName("errorText")[0].innerHTML = "Fetching your data was not successful.";
                                throw new Error('HTTP error, status = ' + response.status);
                            }
                            response.json().then((result) => {
                                if (result.text == "INVALID_LOGIN") {
                                    window.location.href = "login.html?prev=myForms&mes=You_must_be_logged_in_to_view_your_form_data.";
                                } else if (result.text == "INVALID_FORM") {
                                    document.getElementsByName("errorText")[0].style.color = "red";
                                    document.getElementsByName("errorText")[0].innerHTML = "This form does not exist.";
                                } else {
                                    // Find the table's tbody by its ID
                                    let tableBody = document.getElementById("submissionsTable").querySelector("tbody");

                                    // Clear existing rows in table body
                                    tableBody.innerHTML = "";

                                    result.forEach(submission => {
                                        let row = document.createElement("tr");
                                    
                                        Object.keys(submission).forEach(key => {
                                            let value = submission[key];
                                    
                                            if (value instanceof Array) {
                                                value.forEach(item => {
                                                    let cell = document.createElement("td");
                                                    cell.textContent = item;
                                                    row.appendChild(cell);
                                                });
                                            } else if (value instanceof Object) {
                                                let cell = document.createElement("td");
                                                cell.textContent = JSON.stringify(value);
                                                row.appendChild(cell);
                                            } else {
                                                let cell = document.createElement("td");
                                                cell.textContent = value;
                                                row.appendChild(cell);
                                            }
                                        });
                                    
                                        tableBody.appendChild(row);
                                    });

                                    document.getElementById("submissionsTable").style.display = "table";
                                    document.getElementsByClassName("text-center")[0].style.display = "block";
                                    document.getElementsByName("errorText")[0].style.display = "none";
                                }
                            });
                        })
                        .catch(function(error) {
                            console.error('Request failed:', error.message);
                        });
                        //end
                    }
                });
            })
            .catch(function(error) {
                console.error('Request failed:', error.message);
            });
        }
    });
}