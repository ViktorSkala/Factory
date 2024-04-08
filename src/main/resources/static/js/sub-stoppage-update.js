$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const subStoppageId = urlParams.get('id');

    $.ajax({
        url: '/sub_type_stoppage/' + subStoppageId,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (subStoppageDto) {
            $('#sub-stoppage-name').val(subStoppageDto.name);

            $.ajax({
                url: "/base_type_stoppage/all",
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#base-stoppage-name").empty();

                    data.forEach(function (baseStoppageDto) {
                        const option = $("<option></option>")
                            .attr("value", baseStoppageDto.id)
                            .text(baseStoppageDto.name);

                        if (baseStoppageDto.id === subStoppageDto.baseTypeStoppageId) {
                            option.attr("selected", "selected");
                        }

                        $("#base-stoppage-name").append(option);
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

            $('#update-sub-stoppage').on('click', function () {
                const updatedStoppageData = {
                    name: $('#sub-stoppage-name').val(),
                    baseTypeStoppageId: $('#base-stoppage-name').val()
                };

                $.ajax({
                    url: '/sub_type_stoppage/' + subStoppageId,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedStoppageData),
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                    },
                    success: function () {
                        alert("Підтип простою успішно відредагована")
                        window.location.href = '/sub-stoppage.html';
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
