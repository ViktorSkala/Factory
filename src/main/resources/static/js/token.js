$(document).ready(function() {
    $.ajax({
        url: '/token-info',
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function(data) {
            displayTokenInfo(data);
        },
        error: function(xhr, textResponse) {
            $('#token-info').html('<p>Error fetching token information: ' + textResponse + '</p>');
        }
    });

    function displayTokenInfo(data) {
        var tokenInfoHtml = '<p><strong>Token Creation Time:</strong> ' + data.issuedAt + '</p>';
        tokenInfoHtml += '<p><strong>Token Expiration Time:</strong> ' + data.expiration + '</p>';
        tokenInfoHtml += '<p><strong>Actual System Time:</strong> ' + data.actualTime + '</p>';
        tokenInfoHtml += '<p><strong>Token Subject:</strong> ' + data.subject + '</p>';
        $('#token-info').html(tokenInfoHtml);
    }
});