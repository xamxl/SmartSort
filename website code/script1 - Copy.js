// TODO: Save whenever there is a change
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

    for (let i = 0; i < tags.length; i++) {
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
        inputWeight.name = 'weight' + (i+1);
        inputWeight.placeholder = 'Default weight: 1';
        inputWeight.className = 'form-control';
        if (i < tags.length - 4) {
            inputWeight.type = 'number';
        } else {
            var num = 1;
            if (data[inputValue.name]) {
                num = parseInt(data[inputValue.name]);
            }
            if (num < 1 || isNaN(num)) {
                num = 1;
            }
            let def = "";
            for (var j = 0; j < num; j++) {
                def += "1, "
            }
            if (num == 1) {
                inputWeight.placeholder = "Default weight: " + def.slice(0, -2);
            } else {
                inputWeight.placeholder = "Default multi-weight: " + def.slice(0, -2);
            }
            inputValue.addEventListener('input', function() {
                var num = parseInt(this.value);
                if (num < 1 || isNaN(num)) {
                    num = 1;
                }
                let def = "";
                for (var j = 0; j < num; j++) {
                    def += "1, "
                }
                if (num == 1) {
                    document.querySelector("input[name='weight" + (i + 1) + "']").placeholder = "Default weight: " + def.slice(0, -2);
                } else {
                    document.querySelector("input[name='weight" + (i + 1) + "']").placeholder = "Default multi-weight: " + def.slice(0, -2);
                }
            });
        }

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

        saveForm("page1.html").then(() => {
            // go to the next page
            window.location.href = 'page2.html';
        });
    });
};