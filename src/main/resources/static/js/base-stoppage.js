$(document).ready(function () {
    function getBaseTypes() {
        $.ajax({
            url: "/base_type_stoppage/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#base-type-list-container").empty();
                data.forEach(function (stoppageDto) {
                    var listItem = $("<li>").addClass("base-stoppage-item");

                    var baseTypeName = $("<div>").text(stoppageDto.name);

                    var editButton = $("<button class='edit-button'>").text("Редагувати").click(function () {
                        window.location.href = "/base-stoppage-update.html?id=" + stoppageDto.id;
                    });

                    var deleteButton = $("<button class='delete-button'>").text("Видалити").click(function (message) {
                        $.ajax({
                            url: '/base_type_stoppage/' + stoppageDto.id,
                            type: 'DELETE',
                            headers: {
                                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                            },
                            success: function () {
                                location.reload();
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

                    listItem.append(baseTypeName, editButton, deleteButton);
                    $("#base-type-list-container").append(listItem);
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
    }

    getBaseTypes();

    $("#add-base-type").on("click", function () {
        var baseTypeName = $("#base-type-name").val();

        var typeStoppageData = {
            name: baseTypeName
        };

        $.ajax({
            url: "/base_type_stoppage",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(typeStoppageData),
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                alert("Додали новий базовий тип простою: " + data.name);

                $("#base-type-name").val('');

                getBaseTypes();
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
    })
});
