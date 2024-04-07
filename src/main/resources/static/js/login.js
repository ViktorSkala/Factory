$(document).ready(function() {
    $("#loginButton").click(function() {
        // Очистка предыдущих ошибок
        $("#usernameError").text("");
        $("#passwordError").text("");

        const loginRequest = {
            email: $('#username').val(),
            password: $("#password").val()
        };

        // Отправка запроса на сервер
        $.ajax({
            type: "POST",
            url: "/login",
            contentType: "application/json",
            data: JSON.stringify(loginRequest),
            success: function(response) {
                var token = response.token;
                localStorage.setItem("jwtToken", token);

                alert("Login successful!");
                window.location.href = '/index.html';
            },
            error:  function(xhr, status, error) {
                var response = JSON.parse(xhr.responseText);
                console.log(response.message);
                alert(response.message);
            }
        });
    });

    $("#registerButton").click(function() {
        window.location.href = '/registration.html';
    });

    $("#googleSignInBtn").click(function () {
        window.location.href = '/oauth2/authorization/google';
    })
});