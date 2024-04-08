$(document).ready(function () {
    function getMachines() {
        $.ajax({
            url: "/machine/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#machine-list-container").empty();

                data.forEach(function (machineDto) {
                    var listItem = $("<li>").addClass("machine-item");

                    var machineName = $("<div>").text(machineDto.name);

                    var editButton = $("<button class='edit-button'>").text("Редагувати").click(function () {
                        window.location.href = "/machine-update.html?id=" + machineDto.id;
                    });

                    var deleteButton = $("<button class='delete-button'>").text("Видалити").click(function () {
                        $.ajax({
                            url: '/machine/' + machineDto.id,
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

                    listItem.append(machineName, editButton, deleteButton);
                    $("#machine-list-container").append(listItem);
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

    getMachines();

    $("#add-machine").on("click", function () {
        var machineName = $("#machine-name").val();
        var machineData = {
            name: machineName
        };

        $.ajax({
            url: "/machine",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(machineData),
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                alert("Добавлена нова машина: " + data.name);

                $("#machine-name").val('');

                getMachines();
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
