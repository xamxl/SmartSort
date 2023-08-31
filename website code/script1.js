window.onload = function() {
    var container = document.getElementById('inputs-container');
    var form = document.getElementById('form-page-1');

    // Check if there's any saved data in localStorage
    var savedData = localStorage.getItem('page1Data');
    var data = savedData ? JSON.parse(savedData) : {};

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

    for (var i = 0; i < tags.length; i++) {
        var formRow = document.createElement('div');
        formRow.className = 'form-row';

        var formRow1 = document.createElement('div');
        formRow1.className = 'form-row';

        var div1 = document.createElement('div');
        div1.className = 'form-group col-md-6';

        var div11 = document.createElement('div');
        div11.className = 'form-group col-md-6';

        var labelValue = document.createElement('label');
        labelValue.textContent = tags[i] + ':';
        var inputValue = document.createElement('input');
        inputValue.type = 'number';
        inputValue.name = 'value' + (i+1);
        inputValue.className = 'form-control';
        inputValue.placeholder = '0';

        // If there's saved data, populate the input field with it
        if (data[inputValue.name]) {
            inputValue.value = data[inputValue.name];
        }

        div11.appendChild(labelValue);
        div1.appendChild(inputValue);

        var div2 = document.createElement('div');
        div2.className = 'form-group col-md-6';

        var inputWeight = document.createElement('input');
        inputWeight.type = 'number';
        inputWeight.name = 'weight' + (i+1);
        inputWeight.className = 'form-control';
        inputWeight.placeholder = 'Default weight: 1';

        // If there's saved data, populate the input field with it
        if (data[inputWeight.name]) {
            inputWeight.value = data[inputWeight.name];
        }

        div2.appendChild(inputWeight);

        formRow.appendChild(div1);
        formRow.appendChild(div2);

        div11.style.margin = 0;
        formRow1.appendChild(div11);

        container.appendChild(formRow1);
        container.appendChild(formRow);
    }

    form.addEventListener('submit', function(event) {
        event.preventDefault(); // prevent the form from being submitted normally

        var formData = new FormData(event.target);
        var data = {};

        // convert form data to JSON
        for (var pair of formData.entries()) {
            data[pair[0]] = pair[1];
        }

        // store form data in LocalStorage
        localStorage.setItem('page1Data', JSON.stringify(data));

        // go to the next page
        window.location.href = 'page2.html';
    });
};

document.querySelector('a[href="index.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});

document.querySelector('a[href="/index.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});

document.querySelector('a[href="/page1.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});

document.querySelector('a[href="/page2.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});

document.querySelector('a[href="/output.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});

document.querySelector('a[href="/clean.html"]').addEventListener('click', function(event) {
    var form = document.getElementById('form-page-1');
    var formData = new FormData(form);
    var data = {};

    // convert form data to JSON
    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    // store form data in LocalStorage
    localStorage.setItem('page1Data', JSON.stringify(data));
});