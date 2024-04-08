$(document).ready(function () {

    function getSubTypes() {
        $.ajax({
            url: "/sub_type_stoppage/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#sub-type-list-container").empty();

                data.forEach(function (stoppageDto) {
                    var listItem = $("<li>").addClass("sub-stoppage-item");

                    var subTypeName = $("<div>").text(stoppageDto.name);
                    var baseTypeName = $("<div>").text(stoppageDto.baseTypeStoppageName);

                    var editButton = $("<button class='edit-button'>").text("Редагувати").click(function () {
                        window.location.href = "/sub-stoppage-update.html?id=" + stoppageDto.id;
                    });

                    var deleteButton = $("<button class='delete-button'>").text("Видалити").click(function () {
                        $.ajax({
                            url: '/sub_type_stoppage/' + stoppageDto.id,
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

                    listItem.append(subTypeName, baseTypeName, editButton, deleteButton);
                    $("#sub-type-list-container").append(listItem);
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

    getSubTypes();

    function fillBaseTypesDropdown() {
        $.ajax({
            url: "/base_type_stoppage/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#base-type-name").empty();

                $("#base-type-name").append("<option value='' disabled selected>Оберіть базовий тип</option>");
                data.forEach(function (baseStoppageDto) {
                    $("#base-type-name").append("<option value='" + baseStoppageDto.id + "'>" + baseStoppageDto.name + "</option>");
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

    fillBaseTypesDropdown();

    $("#add-sub-type").on("click", function () {
        var baseTypeId = $("#base-type-name").val();
        var subTypeName = $("#sub-type-name").val();

        var typeStoppageData = {
            name: subTypeName,
            baseTypeStoppageId: baseTypeId
        };

        $.ajax({
            url: "/sub_type_stoppage",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(typeStoppageData),
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                alert("Добавлен новий базовий тип простою: " + data.name);

                $("#base-type-name").val('');
                $("#sub-type-name").val('');

                getSubTypes();
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
