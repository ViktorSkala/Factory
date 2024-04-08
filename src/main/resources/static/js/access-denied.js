$(document).ready(function() {
    $.ajax({
        url: "/access-denied",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (data) {
            $("#accessDeniedMessage").text("Access denied for user: " + data.firstName + " " + data.lastName + ".");
        },
        error: function (xhr) {
            if (xhr.status == 401) {
                window.location.href = '/login.html';
            } else if (xhr.status == 403) {
                window.location.href = '/access-denied.html';
            } else {
                alert(xhr.responseText);
            }
        }
    });
});