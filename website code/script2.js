let running = false;
let file1UsingSaved = false;
let file2UsingSaved = false;

function getCookie(name) {
    const value = document.cookie.split('; ').find(row => row.startsWith(name + '='));
    return value ? value.split('=')[1] : false;
}

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
        document.querySelector('label[name="file1buttonlabel"]').innerHTML = "Using saved file: " + page2Data.file1Name;
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

// Function to save the current form data
// function saveForm() {
//     var form = document.getElementById('form-page-2');
//     var formData = new FormData(form);
//     var data = {};

//     // convert form data to JSON
//     for (var pair of formData.entries()) {
//         data[pair[0]] = pair[1];
//     }

//     if (data.file1.name != "") {
//         data.file1Name = data.file1.name;
//         async function processForSave() {
//             const [text1] = await Promise.all([readFileAsXlsx(data.file1)]);
//             alert("not final" + text1);
//             data.file1 = text1;
//             alert(data.file1);
//         }
//         processForSave();
//     } else {
//         var page2Data = JSON.parse(localStorage.getItem('page2Data'));
//         if (page2Data) {
//             alert("Working");
//             console.log(page2Data.file1);
//             console.log(data.file1);
//             data.file1 = page2Data.file1;
//             data.file1Name = page2Data.file1Name;
//         }
//         alert("other" + data.file1);
//     }
//     if (data.file2.name != "") {
//         data.file2Name = data.file2.name;
//         async function processForSave() {
//             const [text2] = await Promise.all([readFileAsXlsx(data.file2)]);
//             data.file2 = text2;
//         }
//         processForSave();
//     } else {
//         var page2Data = JSON.parse(localStorage.getItem('page2Data'));
//         if (page2Data) {
//             data.file2 = page2Data.file2;
//             data.file2Name = page2Data.file2Name;
//         }
//     }

//     alert("final" + JSON.stringify(data));
//     // store form data in LocalStorage
//     localStorage.setItem('page2Data', JSON.stringify(data));
// }

async function saveForm() {
    var form = document.getElementById('form-page-2');
    var formData = new FormData(form);
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    async function processForSave(file, key) {
        const [text] = await Promise.all([readFileAsXlsx(file)]);
        data[key] = text;
    }

    if (data.file1.name !== "") {
        data.file1Name = data.file1.name;
        await processForSave(data.file1, 'file1');
    } else {
        var page2Data = JSON.parse(localStorage.getItem('page2Data'));
        if (page2Data) {
            data.file1 = page2Data.file1;
            data.file1Name = page2Data.file1Name;
        }
    }

    if (data.file2.name !== "") {
        data.file2Name = data.file2.name;
        await processForSave(data.file2, 'file2');
    } else {
        var page2Data = JSON.parse(localStorage.getItem('page2Data'));
        if (page2Data) {
            data.file2 = page2Data.file2;
            data.file2Name = page2Data.file2Name;
        }
    }

    localStorage.setItem('page2Data', JSON.stringify(data));
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

    // data.file1Name = data.file1.name;
    // data.file2Name = data.file2.name;
    // async function processForSave() {
    //     const [text1, text2] = await Promise.all([readFileAsXlsx(data.file1), readFileAsXlsx(data.file2)]);
    //     data.file1 = text1;
    //     data.file2 = text2;
    // }

    // processForSave();
    // localStorage.setItem('page2Data', JSON.stringify(data));
    saveForm().then(() => {

    console.log(!getCookie("loginKey"));
    if (!getCookie("loginKey")) {
        window.location.href = "login.html?prev=page2";
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
                window.location.href = "login.html?prev=page2";
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

    // data.file1Name = data.file1.name;
    // data.file2Name = data.file2.name;
    // async function processForSave() {
    //     const [text1, text2] = await Promise.all([readFileAsXlsx(data.file1), readFileAsXlsx(data.file2)]);
    //     data.file1 = text1;
    //     data.file2 = text2;
    // }

    // processForSave();
    // localStorage.setItem('page2Data', JSON.stringify(data));
    saveForm().then(() => {

    if (!getCookie("loginKey")) {
        window.location.href = "login.html?prev=page2";
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
                window.location.href = "login.html?prev=page2";
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

document.querySelector('a[href="page1.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="output.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="/index.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="/page1.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="/page2.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="/output.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});

document.querySelector('a[href="/clean.html"]').addEventListener('click', function(event) {
    saveForm().then(() => {});
});