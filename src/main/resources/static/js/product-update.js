$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    $.ajax({
        url: '/product/' + productId,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (productResponceDto) {
            $('#product-name').val(productResponceDto.productName);
            $("#machine-name").val(productResponceDto.machineName).attr('readonly', true).data("machine-id", productResponceDto.machineId);
            $("#product-numbersInPack").val(productResponceDto.numbersInPack);
            $("#product-expectedProductivity").val(productResponceDto.expectedProductivity);

            $('#update-product').on('click', function () {
                const updatedProductData = {
                    name: $('#product-name').val(),
                    machineId: $('#machine-name').data("machine-id"),
                    numbersInPack: $('#product-numbersInPack').val(),
                    expectedProductivity: $('#product-expectedProductivity').val()
                };

                $.ajax({
                    url: '/product/' + productId,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedProductData),
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                    },
                    success: function () {
                        window.location.href = '/product.html';
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
