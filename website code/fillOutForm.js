let storage = "";
let running = false;

window.onload = function() {
    // Creating the formData and appending the values
    var formData = new FormData();
    formData.append("email", getQueryParam("user"));
    formData.append("formName", getQueryParam("formName"));
    
    // Get and display form
    fetch('http://localhost:8080/getForm', {
                method: 'POST', 
                body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Fetching this form was not successful.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            document.getElementsByName("errorText")[0].innerHTML = "";
            if (result.hasOwnProperty('text')) {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = "This form does not exist.";
            } else {
                document.getElementsByName("title")[0].innerHTML = result.formName + " &#x2705;";
                document.getElementsByName("formSubmit")[0].style.display = "block";
                document.getElementsByName("id")[0].style.display = "block";
                document.getElementsByName("idLabel")[0].innerHTML = result.idInstruct;
                storage = result.idInstruct;
                
                const formContents = document.getElementsByName('formContents')[0];
                
                result.inputTypes.forEach((type, index) => {
                    const div = document.createElement('div');
                    div.className = 'form-group';
                
                    const label = document.createElement('label');
                    label.textContent = result.texts[index];
                    div.appendChild(label);
                
                    let input;
                
                    switch (type) {
                        case 'm':
                            input = document.createElement('div');
                            input.className = 'form-control';
                            input.style.display = 'flex';
                            input.style.flexDirection = 'column';
                            input.style.alignItems = 'flex-start';

                            result.options[index].split(',').forEach((optionValue, i) => {
                                const wrapper = document.createElement('div');
                                wrapper.style.margin = '8px 0';
                                wrapper.style.display = 'flex';
                                wrapper.style.alignItems = 'center';

                                const radioButton = document.createElement('input');
                                radioButton.type = 'radio';
                                radioButton.id = 'option' + i;
                                radioButton.name = 'optionsGroup' + index;
                                radioButton.value = optionValue;
                                radioButton.style.marginRight = '10px';

                                const label = document.createElement('label');
                                label.htmlFor = 'option' + i;
                                label.textContent = optionValue;

                                wrapper.appendChild(radioButton);
                                wrapper.appendChild(label);

                                input.appendChild(wrapper);
                            });
                            break;
                        case 'd':
                            input = document.createElement('select');
                            input.className = 'form-control';
                            result.options[index].split(',').forEach(optionValue => {
                                const option = document.createElement('option');
                                option.value = optionValue;
                                option.textContent = optionValue;
                                input.appendChild(option);
                            });
                            break;
                        case 't':
                            input = document.createElement('input');
                            input.type = 'text';
                            input.className = 'form-control';
                            break;
                    }
                
                    div.appendChild(input);
                    formContents.appendChild(div);
                });
            }
        });
    })
    .catch(function(error) {
        console.error('Request failed:', error.message);
    });
}

document.getElementsByName('formSubmit')[0].addEventListener('click', function(event) {
    event.preventDefault();
    document.getElementsByName("errorText")[0].innerHTML = "";

    var formData = new FormData();

    const values = [];
    const formElements = document.querySelectorAll('.form-group input, .form-group select');

    formElements.forEach(el => {
        if (el.type === 'radio') {
            if (el.checked) {
                values.push(el.value);
            }
        } else if (el.tagName === 'SELECT' || el.type === 'text') {
            values.push(el.value);
        }
    });

    // Convert values array to JSON string
    const jsonString = JSON.stringify(values);

    // Append the JSON string to formData
    formData.append("submissions", jsonString);
    formData.append("email", getQueryParam("user"));
    formData.append("formName", getQueryParam("formName"));

    if (running) {
        document.getElementsByName("errorText")[0].style.color = "red";
        document.getElementsByName("errorText")[0].innerHTML = "Already submitting your form.";
        return;
    }

    // Send to the server & make sure the name has not been used before
    if (values[0] == "") {
        document.getElementsByName("idLabel")[0].innerHTML = "You must include identification : " + storage;
        document.getElementsByName("idLabel")[0].style.color = "red";
        return;
    } else {
        document.getElementsByName("idLabel")[0].innerHTML = storage;
        document.getElementsByName("idLabel")[0].style.color = "black";
    }

    running = true;
    document.getElementsByName("formSubmit")[0].value = "Submitting ...";
    fetch('http://localhost:8080/submitForm', {
                method: 'POST', 
                body: formData
    })
    .then(function(response) {
        running = false;
        document.getElementsByName("formSubmit")[0].value = "Submit";
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Submitting your form was not successful.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            document.getElementsByName("errorText")[0].innerHTML = "";
            // TODO: make different error for not filling out and not having right identifier
            if (result.hasOwnProperty('text')) {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = "Submitting your form was not successful. You identifier has already been used.";
                document.getElementsByName("idLabel")[0].innerHTML = "This identification has already been used : " + storage;
                document.getElementsByName("idLabel")[0].style.color = "red";
            } else {
                document.getElementsByName("errorText")[0].style.color = "green";
                document.getElementsByName("errorText")[0].innerHTML = "Your form has been successfully submitted.";
                document.getElementById('form-fill-Out-Form').reset();
            }
        });
    })
    .catch(function(error) {
        console.error('Request failed:', error.message);
    });
});

// Function to get the value of a query parameter by name
function getQueryParam(name, url = window.location.href) {
    name = name.replace(/[\[\]]/g, "\\$&");
    const regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)");
    const result = regex.exec(url);
    if (!result) return null;
    if (!result[2]) return '';
    return decodeURIComponent(result[2].replace(/\+/g, " "));
}