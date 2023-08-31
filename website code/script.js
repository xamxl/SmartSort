document.querySelector('a[id="startWithoutSaved"]').addEventListener('click', function(event) {
    localStorage.clear();
});