$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const baseStoppageId = urlParams.get('id');

    $.ajax({
        url: '/base_type_stoppage/' + baseStoppageId,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (baseStoppageDto) {
            $('#base-stoppage-name').val(baseStoppageDto.name);

            $('#update-base-stoppage').on('click', function () {
                const updatedStoppageData = {
                    id: baseStoppageId,
                    name: $('#base-stoppage-name').val()
                };

                $.ajax({
                    url: '/base_type_stoppage/' + baseStoppageId,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedStoppageData),
                    beforeSend: function(xhr) {
                        var jwtToken = localStorage.getItem("jwtToken");
                        if (jwtToken) {
                            xhr.setRequestHeader("Authorization", "Bearer " + jwtToken);
                        }
                    },
                    success: function () {
                        alert("Базовий тип простою успішно відредагована")
                        window.location.href = '/base-stoppage.html';
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
