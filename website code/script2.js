let running = false;
let file1UsingSaved = false;
let file2UsingSaved = false;

window.onload = function() {
    var page2Data = JSON.parse(localStorage.getItem('page2Data'));
    var page1Data = JSON.parse(localStorage.getItem('page1Data'));

    if (page2Data) {
        document.querySelector('input[name="number1"]').value = page2Data.number1;
        document.querySelector('input[name="number2"]').value = page2Data.number2;
        document.querySelector('input[name="number3"]').value = page2Data.number3;
        document.querySelector('input[name="string"]').value = page2Data.string;
    }

    if (page2Data && page2Data.file1Name != "" && page2Data.file1Name != undefined) {
        document.querySelector('div[name="file1buttondiv"]').style.display = "flex";
        document.querySelector('button[name="file2button"]').style.margin = "0px";
        if (! page2Data.hasOwnProperty("usingForm")) {
            document.querySelector('button[name="file1button"]').innerHTML = "Select New File";
            document.querySelector('label[name="file1buttonlabel"]').innerHTML = "Using saved file: " + page2Data.file1Name;
        } else {
            document.querySelector('button[name="file1button"]').innerHTML = "Select File";
            document.querySelector('label[name="file1buttonlabel"]').innerHTML = "Using saved data: " + page2Data.file1Name;
        }
        file1UsingSaved = true;
    } else {
        document.querySelector('input[name="file1"]').style.display = "block";
        document.querySelector('label[name="file1label"]').style.display = "block";
    }
    if (page2Data && page2Data.file2Name != "" && page2Data.file2Name != undefined) {
        document.querySelector('div[name="file2buttondiv"]').style.display = "flex";
        document.querySelector('label[name="file2buttonlabel"]').innerHTML = "Using saved file: " + page2Data.file2Name;
        file2UsingSaved = true;
    } else {
        document.querySelector('input[name="file2"]').style.display = "block";
        document.querySelector('label[name="file2label"]').style.display = "block";
    }

    var intArray = [];
    var counter = 1;
    for (const key in page1Data) { 
        if (counter % 2 != 0) {
            if (page1Data[key] == "") {
                intArray.push(0);
            } else {
                intArray.push(parseInt(page1Data[key], 10));
            }
        }
        counter++;
    };

    let tags = [
        "rankedChoice",
        "rankedNotChoice",
        "choice",
        "notChoice",
        "rankedItem",
        "rankedNotItem",
        "item",
        "notItem",
        "attributeToBalance",
        "attributeToSplitBy",
        "attributeToNotIsolate",
        "attributeToIsolate"];

    var itemsFormat = "Individuals Format: name, ";
    for (var j = 0; j < tags.length; j++) {
        for (var i = 0; i < intArray[j]; i++) {
            itemsFormat += tags[j] + (i + 1) + ", ";
        }
    }
    
    document.querySelector("p[name='file1p']").textContent = itemsFormat.substring(0,itemsFormat.length-2);
    document.querySelector("p[name='file1p']").style.color = "grey";
    document.querySelector("p[name='file2p']").textContent = "Locations Format: locationName, maximumNumber, preferredMinimumNumber";
    document.querySelector("p[name='file2p']").style.color = "grey";

};

// Function to read a file and return a Promise that resolves with the JSON array of arrays.
function readFileAsXlsx(file) {
    if(file == "USING_SAVED") {
        return file;
    }

    return new Promise((resolve, reject) => {
        let reader = new FileReader();
    
        reader.onload = function(event) {
            let data = new Uint8Array(event.target.result);
            let workbook = XLSX.read(data, {type: 'array'});
            // Convert the first worksheet to CSV text.
            let csvStr = XLSX.utils.sheet_to_csv(workbook.Sheets[workbook.SheetNames[0]]);
            // Parse CSV text with Papa Parse.
            let arrayStr = Papa.parse(csvStr, {header: false, dynamicTyping: true}).data;
            resolve(JSON.stringify(arrayStr));
        };
    
        reader.onerror = function(event) {
            document.getElementsByName("runRandomTag")[0].value = "Sort Randomly >";
            running = false;
            reject(new Error("File could not be read: " + event.target.error));
        };
    
        reader.readAsArrayBuffer(file);
    });
}

document.getElementsByName('runRandomTag')[0].addEventListener('click', function(event) {
    event.preventDefault();

    var formData = new FormData(document.getElementById('form-page-2'));
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    if (data.file1.name == ""  && ! file1UsingSaved) {
        document.querySelector("label[name='file1label']").style.color = "red";
        document.querySelector("label[name='file1label']").textContent = "Individuals xlsx file: Field Required";
    } else {
        document.querySelector("label[name='file1label']").style.color = "black";
        document.querySelector("label[name='file1label']").textContent = "Individuals xlsx file:";
    }
    if (data.file2.name == ""  && ! file2UsingSaved) {
        document.querySelector("label[name='file2label']").style.color = "red";
        document.querySelector("label[name='file2label']").textContent = "Locations xlsx file: Field Required";
    } else {
        document.querySelector("label[name='file2label']").style.color = "black";
        document.querySelector("label[name='file2label']").textContent = "Locations xlsx file:";
    }

    if ((data.file1.name == "" && !file1UsingSaved) || (data.file2.name == "" && !file2UsingSaved)) {
        return;
    }

    saveForm("page2.html").then(() => {

    console.log(!getCookie("loginKey"));
    if (!getCookie("loginKey")) {
        window.location.href = "login.html?prev=page2&mes=You_must_be_logged_in_to_run_a_sort.";
        return;
    }

    formData.append("email", getCookie("email"));
    formData.append("key", getCookie("loginKey"));

    document.getElementsByName("runRandomTag")[0].value = "Running ...";

    var page1Data = JSON.parse(localStorage.getItem('page1Data'));
    for (var key in page1Data) {
        formData.append(key, page1Data[key]);
    }

    let file1;
    let file2;
    if (!file1UsingSaved) {
        file1 = formData.get('file1');
    } else {
        file1 = "USING_SAVED";
    }
    if (!file2UsingSaved) {
        file2 = formData.get('file2');
    } else {
        file2 = "USING_SAVED";
    }

    // Read the files and replace them in the formData with their text representation.
    Promise.all([readFileAsXlsx(file1), readFileAsXlsx(file2)]).then(([text1, text2]) => {
        if (text1 == "USING_SAVED") {
            var page2Data = JSON.parse(localStorage.getItem('page2Data'));
            formData.set('file1', page2Data.file1);
        } else {
            formData.set('file1', text1);
        }
        if (text2 == "USING_SAVED") {
            var page2Data = JSON.parse(localStorage.getItem('page2Data'));
            formData.set('file2', page2Data.file2);
        } else {
            formData.set('file2', text2);
        }
    

    if (running) {
        document.getElementsByName("errorText")[0].innerHTML = "Already running.";
        return;
    }

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;

    fetch('http://localhost:8080/upload?type=random', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].innerHTML = "Sort was not successful. Please check inputs. Make sure that all files are correctly formatted and that all numeric inputs are non negative integers.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            if (result.hasOwnProperty('text')) {
                window.location.href = "login.html?prev=page2&mes=You_must_be_logged_in_to_run_a_sort.";
            } else {
                window.location.href = "output.html";
                localStorage.setItem('sortResult', JSON.stringify(result));
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("runRandomTag")[0].value = "Sort Randomly >";
        running = false;
        console.error('Request failed:', error.message);
    });

    }).catch(error => {
    console.error("Error reading files:", error);
    });

    });
});

document.getElementsByName('runTag')[0].addEventListener('click', function(event) {
    event.preventDefault();

    var formData = new FormData(document.getElementById('form-page-2'));
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    if (data.file1.name == "" && ! file1UsingSaved) {
        document.querySelector("label[name='file1label']").style.color = "red";
        document.querySelector("label[name='file1label']").textContent = "Individuals xlsx file: Field Required";
    } else {
        document.querySelector("label[name='file1label']").style.color = "black";
        document.querySelector("label[name='file1label']").textContent = "Individuals xlsx file:";
    }
    if (data.file2.name == "" && ! file2UsingSaved) {
        document.querySelector("label[name='file2label']").style.color = "red";
        document.querySelector("label[name='file2label']").textContent = "Locations xlsx file: Field Required";
    } else {
        document.querySelector("label[name='file2label']").style.color = "black";
        document.querySelector("label[name='file2label']").textContent = "Locations xlsx file:";
    }

    if ((data.file1.name == "" && !file1UsingSaved) || (data.file2.name == "" && !file2UsingSaved)) {
        return;
    }

    saveForm("page2.html").then(() => {

    if (!getCookie("loginKey")) {
        window.location.href = "login.html?prev=page2&mes=You_must_be_logged_in_to_run_a_sort.";
        return;
    }

    formData.append("email", getCookie("email"));
    formData.append("key", getCookie("loginKey"));

    document.getElementsByName("runTag")[0].value = "Running ...";

    var page1Data = JSON.parse(localStorage.getItem('page1Data'));
    for (var key in page1Data) {
        formData.append(key, page1Data[key]);
    }

    let file1;
    let file2;
    if (!file1UsingSaved) {
        file1 = formData.get('file1');
    } else {
        file1 = "USING_SAVED";
    }
    if (!file2UsingSaved) {
        file2 = formData.get('file2');
    } else {
        file2 = "USING_SAVED";
    }

    // Read the files and replace them in the formData with their text representation.
    Promise.all([readFileAsXlsx(file1), readFileAsXlsx(file2)]).then(([text1, text2]) => {
        if (text1 == "USING_SAVED") {
            var page2Data = JSON.parse(localStorage.getItem('page2Data'));
            formData.set('file1', page2Data.file1);
        } else {
            formData.set('file1', text1);
        }
        if (text2 == "USING_SAVED") {
            var page2Data = JSON.parse(localStorage.getItem('page2Data'));
            formData.set('file2', page2Data.file2);
        } else {
            formData.set('file2', text2);
        }

    if (running) {
        document.getElementsByName("errorText")[0].innerHTML = "Already running.";
        return;
    }

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;
    
    fetch('http://localhost:8080/upload?type=normal', {
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].innerHTML = "Sort was not successful. Please check inputs. Make sure that all files are correctly formatted and that all numeric inputs are non negative integers.";
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            if (result.hasOwnProperty('text')) {
                window.location.href = "login.html?prev=page2&mes=You_must_be_logged_in_to_run_a_sort.";
            } else {
                localStorage.setItem('sortResult', JSON.stringify(result));
                window.location.href = "output.html";
            }
        });
    })
    .catch(function(error) {
        document.getElementsByName("runTag")[0].value = "Run >";
        running = false;
        console.error('Request failed:', error.message);
    });

    }).catch(error => {
    console.error("Error reading files:", error);
    });

    });
});

document.getElementsByName('file1button')[0].addEventListener('click', function(event) {
    event.preventDefault();

    document.querySelector('div[name="file1buttondiv"]').style.display = "none";
    document.querySelector('label[name="file1label"]').style.display = "block";
    document.querySelector('label[name="file1label"]').innerHTML = "Individuals xlsx file:";
    document.querySelector('input[name="file1"]').style.display = "block";
    file1UsingSaved = false;
});

document.getElementsByName('file2button')[0].addEventListener('click', function(event) {
    event.preventDefault();

    document.querySelector('div[name="file2buttondiv"]').style.display = "none";
    document.querySelector('label[name="file2label"]').style.display = "block";
    document.querySelector('label[name="file2label"]').innerHTML = "Locations xlsx file:";
    document.querySelector('input[name="file2"]').style.display = "block";
    file2UsingSaved = false;
});