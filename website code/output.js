document.querySelector('a[name="delete"]').addEventListener('click', function(event) {
    localStorage.clear();
});

window.onload = function() {
    const sortResult = JSON.parse(localStorage.getItem('sortResult'));
    console.log(sortResult);
    if(sortResult) {
        document.getElementsByName("mainText")[0].innerText = "Best " + sortResult.numberOfSorts + " of sorts. Average unhappiness: " + Number(Math.round(sortResult.averageUnhappiness + "e5") + "e-5");
        document.getElementsByName("titleText")[0].innerText = "Sort Results: " + sortResult.title;

        let html = '<div class="table-responsive"><table id="table" class="table table-hover table-striped"><thead class="thead-dark"><tr>';
        sortResult.output[0].forEach(header => {
            html += `<th scope="col">${header}</th>`;
        });
        html += '</tr></thead><tbody>';

        // Loop through the rest of the rows
        for(let i = 1; i < sortResult.output.length; i++) {
            html += "<tr>";
            sortResult.output[i].forEach(cell => {
                html += `<td>${cell}</td>`;
            });
            html += "</tr>";
        }
        html += "</tbody></table></div>";
        document.getElementById('tableDiv').innerHTML = html;

        html = '<div class="table-responsive"><table id="table1" class="table table-hover table-striped"><thead class="thead-dark"><tr>';
        html += `<th scope="col">Individual</th>`;
        html += `<th scope="col">Location</th>`;
        html += '</tr></thead><tbody>';

        // Loop through the rest of the rows
        for(let i = 0; i < sortResult.output1.length; i++) {
            html += "<tr>";
            sortResult.output1[i].forEach(cell => {
                html += `<td>${cell}</td>`;
            });
            html += "</tr>";
        }
        html += "</tbody></table></div>";
        document.getElementById('tableDiv1').innerHTML = html;

        const yValues = sortResult.averageUnhappinessOverIterations;

        const ctx = document.getElementById('myChart').getContext('2d');
        const myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: Array.from({length: yValues.length}, (_, i) => i + 1),
                datasets: [{
                    data: yValues,
                    borderColor: '#0D6EFD',
                    borderWidth: 4,
                    fill: false,
                    label: false, // Removing the label at the top
                    pointRadius: 0
                }]
            },
            options: {
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        enabled: false // Disable tooltip
                    },
                    title: {
                        display: true,
                        text: 'Average Unhappiness Over Iterations',  // Specify your title text here
                        font: {
                            size: 18
                        }
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Iterations'
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Average Unhappiness'
                        }
                    }
                }
            }
        });       

        if (yValues.length != 0) {
            document.getElementById('myChart').style.display = "block";
        }
    } else {
        document.getElementsByName("mainText")[0].innerText = "";
        document.getElementsByName("titleText")[0].innerText = "No Saved Output";
        document.getElementsByName("download")[0].style.visibility = "hidden";
        document.getElementsByName("download1")[0].style.visibility = "hidden";
    }
}

function downloadTableAsXLSX(tableId, filename = '') {
    // Get the table
    var table = document.getElementById(tableId);
    var rows = Array.from(table.getElementsByTagName("tr"));

    // Transform each row into an array of arrays
    var data = [];
    for (var i = 0; i < rows.length; i++) {
        var row = [], cols = rows[i].querySelectorAll('td, th');
        for (var j = 0; j < cols.length; j++) {
            row.push(cols[j].innerText);
        }
        data.push(row);
    }

    // Create a new workbook and a new worksheet
    var wb = XLSX.utils.book_new();
    var ws = XLSX.utils.aoa_to_sheet(data);

    // Append the worksheet to the workbook
    XLSX.utils.book_append_sheet(wb, ws, "Sheet1");

    // Generate XLSX file and send to client
    XLSX.writeFile(wb, filename || 'output.xlsx');
}

document.querySelector('a[name="download"]').addEventListener('click', function(event) {
    event.preventDefault();
    const sortResult = JSON.parse(localStorage.getItem('sortResult'));
    downloadTableAsXLSX('table', sortResult.title + "_table1.xlsx");
});

document.querySelector('a[name="download1"]').addEventListener('click', function(event) {
    event.preventDefault();
    const sortResult = JSON.parse(localStorage.getItem('sortResult'));
    downloadTableAsXLSX('table1', sortResult.title + "_table2.xlsx");
});