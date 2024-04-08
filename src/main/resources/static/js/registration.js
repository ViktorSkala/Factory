$(document).ready(function() {
    $("#registerButton").click(function() {
        var firstName = $("#firstName").val();
        var lastName = $("#lastName").val();
        var email = $("#email").val();
        var password = $("#password").val();

        $("#firstNameError").text("");
        $("#lastNameError").text("");
        $("#emailError").text("");
        $("#passwordError").text("");

        $.ajax({
            type: "POST",
            url: "/register",
            contentType: "application/json",
            data: JSON.stringify({
                "firstName": firstName,
                "lastName": lastName,
                "email": email,
                "password": password
            }),
            success: function(response) {
                alert("Registration successful!");
                window.location.href = '/login.html';
            },
            error: function(xhr) {
                var response = JSON.parse(xhr.responseText);
                if (response.error === "invalid_firstName") {
                    $("#firstNameError").text("Invalid first name");
                } else if (response.error === "invalid_lastName") {
                    $("#lastNameError").text("Invalid last name");
                } else if (response.error === "invalid_email") {
                    $("#emailError").text("Invalid email");
                } else if (response.error === "invalid_password") {
                    $("#passwordError").text("Invalid password");
                } else {
                    alert("Unknown error occurred.");
                }
            }
        });
    });
});
