function getCookie(name) {
    const value = document.cookie.split('; ').find(row => row.startsWith(name + '='));
    return value ? value.split('=')[1] : false;
}

async function isLoggedIn() {
    if (!getCookie("loginKey")) {
        return false;
    }

    const formData = new FormData();
    formData.append("email", getCookie("email"));
    formData.append("key", getCookie("loginKey"));

    try {
        const response = await fetch('http://localhost:8080/verifyLogin', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            return false;
        }

        const result = await response.json();
        return result.text == "VALID";
        
    } catch (error) {
        return false;
    }
}

async function saveForm(form) {
    if (form == "page1.html") {
        var form = document.getElementById('form-page-1');
        var formData = new FormData(form);
        var data = {};

        // convert form data to JSON
        for (var pair of formData.entries()) {
            data[pair[0]] = pair[1];
        }

        // store form data in LocalStorage
        localStorage.setItem('page1Data', JSON.stringify(data));
    } else if (form == "page2.html") {
        var form = document.getElementById('form-page-2');
        var formData = new FormData(form);
        let data = {};

        for (var pair of formData.entries()) {
            data[pair[0]] = pair[1];
        }

        async function processForSave(file, key) {
            const [text] = await Promise.all([readFileAsXlsx(file)]);
            data[key] = text;
        }

        if (data.file1.name != "") {
            data.file1Name = data.file1.name;
            await processForSave(data.file1, 'file1');
        } else {
            var page2Data = JSON.parse(localStorage.getItem('page2Data'));
            if (page2Data) {
                data.file1 = page2Data.file1;
                data.file1Name = page2Data.file1Name;
            }
        }

        if (data.file2.name != "") {
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
}