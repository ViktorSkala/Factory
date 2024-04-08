$(document).ready(function () {
    var urlParams = new URLSearchParams(window.location.search);
    var threadName = urlParams.get('threadName');

    var machineId = 0;
    var productId = 0;

    $.ajax({
        url: "/production/info/" + encodeURIComponent(threadName),
        type: "GET",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (productionThreadDto) {
            $("#production-info").text("Процес: " + threadName + ", Стан: " + productionThreadDto.state);
            machineId = productionThreadDto.machineId;
            productId = productionThreadDto.productId;

            const stoppageFilterDto = {
                machineId: machineId,
                productId: productId,
            };

            $.ajax({
                url: "/stoppage/not_finished/all_filtered",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(stoppageFilterDto),
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("stoppage-creation-list-container").empty();

                    data.forEach(function (stoppageCreationDto) {
                        var listItem = $("<li>").addClass("stoppage-creation-item");
                        var startDate = $("<span>").text(stoppageCreationDto.startDate);
                        var product = $("<span>").text(stoppageCreationDto.productName);
                        var machine = $("<span>").text(stoppageCreationDto.machineName);
                        var manageButton = $("<button>").text("Керувати").click(function () {
                            window.location.href = "/create-stoppage.html?stoppageId=" + stoppageCreationDto.id;
                        });

                        listItem.append(startDate, product, machine, manageButton);
                        $("#stoppage-creation-list-container").append(listItem);
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

    $("#pause-production").on("click", function () {
        $.ajax({
            url: "/production/wait/" + encodeURIComponent(threadName),
            type: "GET",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (stoppageResponseDto) {
                window.location.href = "/create-stoppage.html?stoppageId=" + stoppageResponseDto.id;

                // // Создаем объект StoppageCreateDto
                // var stoppageDto = {
                //     productId: data.productId, // Подставьте реальные значения, полученные от сервера
                //     machineId: data.machineId, // Подставьте реальные значения, полученные от сервера
                // };
                //
                // // Отправляем POST-запрос на сервер для создания Stoppage
                // $.ajax({
                //     url: "/stoppage",
                //     type: "POST",
                //     contentType: "application/json",
                //     data: JSON.stringify(stoppageDto),
                //     beforeSend: function(xhr) {
                //         var jwtToken = localStorage.getItem("jwtToken");
                //         if (jwtToken) {
                //             xhr.setRequestHeader("Authorization", "Bearer " + jwtToken);
                //         }
                //     },
                //     success: function (stoppageResponseDto) {
                //         window.location.href = "/create-stoppage.html?stoppageId=" + stoppageResponseDto.id;
                //     },
                //     error: function (xhr) {
                //         if (xhr.status == 401) {
                //             window.location.href = '/login.html';
                //         } else if (xhr.status == 403) {
                //             window.location.href = '/access-denied.html';
                //         } else {
                //             alert(xhr.responseText);
                //         }
                //     }
                // });
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

    // Обработчик кнопки "Возобновить"
    // $("#resume-production").on("click", function () {
    //     // Отправляем GET-запрос на сервер
    //     $.ajax({
    //         url: "/production/resume/" + encodeURIComponent(threadName),
    //         type: "GET",
    //         success: function () {
    //             location.reload();
    //         },
    //         error: function () {
    //             alert("Ошибка при возобновлении производства");
    //         }
    //     });
    // });

    $("#finish-production").on("click", function () {
        // Отправляем GET-запрос на сервер
        $.ajax({
            url: "/production/finish/" + encodeURIComponent(threadName),
            type: "GET",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function () {
                window.location.href = "/production.html";
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
});
