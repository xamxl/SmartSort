let dropdownBefore = '<select name="';
let dropdown = '" id="field-type" class="form-control mb-2">';

let running = false;

let elements = [];

document.getElementById('form-form-creator').addEventListener('submit', function(e){
    e.preventDefault();

    var formData = new FormData(e.target);
    formData.append('inputTypes', JSON.stringify(elements));
    var data = { sortTypes: [], options: [], texts: [] };
    
    formData.forEach(function(value, key){
        if (key.startsWith('d')) {
            data.sortTypes.push(value);
        } else if (key.startsWith('o')) {
            data.options.push(value);
        } else if (key.startsWith('t')) {
            data.texts.push(value);
        } else {
            data[key] = value;
        }
    });

    if (data.formName == "") {
        document.querySelector("label[name='formNameLabel']").style.color = "red";
        document.querySelector("label[name='formNameLabel']").textContent = "Form name: Field Required";
        return;
    } else {
        document.querySelector("label[name='formNameLabel']").style.color = "black";
        document.querySelector("label[name='formNameLabel']").textContent = "Form name";
    }
    
    if (!getCookie("loginKey")) {
        window.location.href = "login.html?prev=formCreator&mes=You_must_be_logged_in_to_create_a_form.";
        return;
    }

    var formData1 = new FormData();
    Object.keys(data).forEach(key => {
        if (Array.isArray(data[key])) {
            // Convert array to JSON string
            formData1.append(key, JSON.stringify(data[key]));
        } else {
            // Append non-array values directly
            formData1.append(key, data[key]);
        }
    });

    formData1.append("email", getCookie("email"));
    formData1.append("key", getCookie("loginKey"));

    document.getElementsByName("create")[0].value = "Working ...";

    if (running) {
        document.getElementsByName("errorText")[0].innerHTML = "Already creating your form.";
        return;
    }

    // TODO: Save results so that login does not remove progress

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;

    fetch('http://localhost:8080/createForm', {
        method: 'POST', 
        body: formData1
    })
    .then(function(response) {
        document.getElementsByName("create")[0].value = "Create Form >";
        running = false;
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Creating your form was not successful. Please check inputs.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            if (result.text == "INVALID") {
                window.location.href = "login.html?prev=formCreator&mes=You_must_be_logged_in_to_create_a_form.";
            } else if (result.text == "Form created.") {
                document.getElementsByName("errorText")[0].style.color = "green";
                document.getElementsByName("errorText")[0].innerHTML = result.text;
                document.getElementById('fields-container').innerHTML = "";
                elements = [];
                document.querySelector("input[name='formName']").value = "";
                document.querySelector("input[name='idInstruct']").value = "";
            } else {
                document.getElementsByName("errorText")[0].style.color = "red";
                document.getElementsByName("errorText")[0].innerHTML = result.text;
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("create")[0].value = "Create Form >";
        running = false;
        console.error('Request failed:', error.message);
    });
});

window.onload = function() {
    let types = [
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
        "Attributes to isolate",
        "Attributes to group by"];
    for (let type of types) {
        dropdown += "<option>" + type + "</option>";
    }
    dropdown += "</select>";
};

function addField(fieldType) {
    var fieldHtml = '';
    switch(fieldType) {
        case 'text':
            elements.push("t");
            fieldHtml = `
                <div class="form-group">
                    <label>Text field:</label>
                    <input name="t` + elements.length + `" placeholder="Describe your text input field ..." type="text" class="form-control mb-2">`
                + dropdownBefore + "d" + elements.length + dropdown +
                `</div>`;
            break;
        case 'multiple':
            elements.push("m");
            fieldHtml = `
                <div class="form-group">
                    <label>Multiple choice:</label>
                    <input name="t` + elements.length + `" placeholder="Describe your multiple choice field ..." type="text" class="form-control mb-2">
                    <input name="o` + elements.length + `" placeholder="option 1,option 2,option 3, ..." type="text" class="form-control mb-2">`
                + dropdownBefore + "d" + elements.length + dropdown +
                `</div>`;
            break;
        case 'dropdown':
            elements.push("d");
            fieldHtml = `
                <div class="form-group">
                    <label>Dropdown:</label>
                    <input name="t` + elements.length + `" placeholder="Describe your dropdown field ..." type="text" class="form-control mb-2">
                    <input name="o` + elements.length + `" placeholder="option 1,option 2,option 3, ..." type="text" class="form-control mb-2">`
                + dropdownBefore + "d" + elements.length + dropdown +
                `</div>`;
            break;
    }
    document.getElementById('fields-container').insertAdjacentHTML('beforeend', fieldHtml);
}
