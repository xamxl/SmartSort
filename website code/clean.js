let running = false;

document.getElementById('form-clean').addEventListener('submit', function(event) {
    event.preventDefault();

    var formData = new FormData(event.target);
    var data = {};

    for (var pair of formData.entries()) {
        data[pair[0]] = pair[1];
    }

    if (data.toClean.name == "") {
        document.querySelector("label[name='toCleanlabel']").style.color = "red";
        document.querySelector("label[name='toCleanlabel']").textContent = "Xlsx file with dirty data: Field Required";
    } else {
        document.querySelector("label[name='toCleanlabel']").style.color = "black";
        document.querySelector("label[name='toCleanlabel']").textContent = "Xlsx file with dirty data:";
    }
    if (data.clean.name == "") {
        document.querySelector("label[name='cleanlabel']").style.color = "red";
        document.querySelector("label[name='cleanlabel']").textContent = "Xlsx file with clean data: Field Required";
    } else {
        document.querySelector("label[name='cleanlabel']").style.color = "black";
        document.querySelector("label[name='cleanlabel']").textContent = "Xlsx file with clean data:";
    }

    if (data.toClean.name == "" || data.clean.name == "") {
        return;
    }

    document.getElementsByName("runTag")[0].value = "Cleaning ...";

    // Assuming you have a formData with toClean and clean.
    let toClean = formData.get('toClean');
    let clean = formData.get('clean');

    /// Function to read a file and return a Promise that resolves with the JSON array of arrays.
    function readFileAsXlsx(file) {
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
                document.getElementsByName("runTag")[0].value = "Clean and Download Data >";
                running = false;
                reject(new Error("File could not be read: " + event.target.error));
            };
        
            reader.readAsArrayBuffer(file);
        });
    }

    // Read the files and replace them in the formData with their text representation.
    Promise.all([readFileAsXlsx(toClean), readFileAsXlsx(clean)]).then(([text1, text2]) => {
        formData.set('toClean', text1);
        formData.set('clean', text2);

    if (running) {
        document.getElementsByName("errorText")[0].innerHTML = "Already cleaning.";
        document.getElementsByName("errorText")[0].style.color = "red";
        return;
    }

    document.getElementsByName("errorText")[0].innerHTML = "";

    running = true;

    fetch('http://localhost:8080/uploadClean', { 
        method: 'POST', 
        body: formData
    })
    .then(function(response) {
        if (!response.ok) {
            document.getElementsByName("errorText")[0].style.color = "red";
            document.getElementsByName("errorText")[0].innerHTML = "Cleaning was not successful. Please check inputs. Make sure that all files are correctly formatted and that the column index is correct. Remember that the first column is column 0.";
            running = false;
            throw new Error('HTTP error, status = ' + response.status);
        }
        response.json().then((result) => {
            document.getElementsByName("runTag")[0].value = "Clean and Download Data >";
            document.getElementsByName("errorText")[0].style.color = "green";
            document.getElementsByName("errorText")[0].innerHTML = "Your data has been cleaned and downloaded.";
            running = false;
            downloadFileFromResult(result, toClean.name.substring(0, toClean.name.length - 5) + "_clean.xlsx");
        });
    })
    .catch(function(error) {
        document.getElementsByName("runTag")[0].value = "Clean and Download Data >";
        running = false;
        console.error('Request failed:', error.message);
    });

    }).catch(error => {
    console.error("Error reading files:", error);
    });

});

function downloadFileFromResult(fileString, filename) {
    // Get the data from the 'output' property of the fileString object
    const fileContent = fileString.output;
  
    // If there's no content for the 'output' property, return
    if (!fileContent) {
      console.error('No content found for the given key.');
      return;
    }
  
    // Replace null values with an empty string
    const sanitizedContent = fileContent.map(row => row.map(cell => cell === null ? '' : cell));
  
    // Create a new workbook and a new worksheet
    var wb = XLSX.utils.book_new();
    var ws = XLSX.utils.aoa_to_sheet(sanitizedContent);
  
    // Append the worksheet to the workbook
    XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
  
    // Generate XLSX file and send to client
    XLSX.writeFile(wb, filename || 'output.xlsx');
}
  