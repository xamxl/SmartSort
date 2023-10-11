let dropdownBefore = '<select name="';
let dropdown = '" id="field-type" class="form-control mb-2">';

let elements = [];

document.getElementById('form-form-creator').addEventListener('submit', function(e){
    e.preventDefault();
    var formData = new FormData(e.target);
    formData.append('elements', JSON.stringify(elements));
    var data = {};
    formData.forEach(function(value, key){
        data[key] = value;
    });
    
    console.log(data);
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
        "Attributes to isolate"];
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
