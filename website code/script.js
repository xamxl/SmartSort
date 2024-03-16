document.querySelector('a[id="startWithoutSaved"]').addEventListener('click', function(event) {
    localStorage.clear();
});

function toggleVisibility(id) {
    var element = document.getElementById(id);
    element.style.display = "block";
    document.getElementById("newFeaturesLink").style.display = "none";
}