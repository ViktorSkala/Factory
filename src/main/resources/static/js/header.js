$("#home").on("click", function () {
    window.location.href = "index.html";
});

$("#production").on("click", function () {
    window.location.href = "production.html";
});

$("#stoppage").on("click", function () {
    window.location.href = "stoppage.html";
});

$("#machines").on("click", function () {
    window.location.href = "machine.html";
});

$("#products").on("click", function () {
    window.location.href = "product.html";
});

$("#charts").on("click", function () {
    window.location.href = "chart.html";
});

$("#reports").on("click", function () {
    window.location.href = "report.html";
});

var modal = $("#myModal");
var btn = $("#sendMessage");
var span = $(".close");

btn.on("click", function() {
    modal.css("display", "block");
});

span.on("click", function() {
    modal.css("display", "none");
});

$(window).on("click", function(event) {
    if (event.target == modal[0]) {
        modal.css("display", "none");
    }
});

$("#sendBtn").on("click", function() {
    var message = $("#messageInput").val();
    const messageDto = {
        message: message
    };
    $.ajax({
        url: "/send-message",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(messageDto),
        success: function (response) {
            alert('Message sent successfully!');
        },
        error: function (xhr, status, error) {
            alert('Error sending message: ' + xhr.responseText);
        }
    });
    modal.css("display", "none");
});

$("#cancelBtn").on("click", function() {
    modal.css("display", "none");
});

$("#info").on("click", function () {
    window.location.href = "welcome.html";
});

$("#logout").on("click", function () {
    $.ajax({
        url: "/logout",
        type: "POST",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function(response) {
            localStorage.clear();
            location.reload();
            },
        error: function(xhr) {
            alert("Logout error")
        }
    });
});
