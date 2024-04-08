function showMessageModal() {
    $('#messageModal').show();
}

function hideMessageModal() {
    $('#messageModal').hide();
}

$('#sendMessageBtn').on('click', function () {
    showMessageModal();
});

$('#cancelBtn').on('click', function () {
    hideMessageModal();
});

$('#continueBtn').on('click', function () {
    window.location.href = '/index.html';
});

$('#sendBtn').on('click', function () {
    const messageDto = {
        message: $('#messageInput').val()
    };
    $.ajax({
        url: "/send-message",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(messageDto),
        success: function () {
            alert('Message sent successfully!');
            hideMessageModal();
        },
        error: function (xhr) {
            alert('Error sending message: ' + xhr.responseText);
        }
    });
});
