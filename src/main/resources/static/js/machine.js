$(document).ready(function () {
    // Обработчик клика по кнопке "Logout"
    $("#logout").on("click", function () {
        // Ваш код для выхода из системы
        alert("Выход из системы");
    });

    // Функция для получения списка машин
    function getMachines() {
        $.ajax({
            url: "/machine/all",
            type: "GET",
            dataType: "json",
            success: function (data) {
                // Очищаем список машин перед обновлением
                $("#machine-list-container").empty();

                // Добавляем каждую машину в список
                data.forEach(function (machineDto) {
                    var listItem = $("<li>").addClass("machine-item");

                    var machineName = $("<div>").text(machineDto.name);

                    var editButton = $("<button class='edit-button'>").text("Редагувати").click(function () {
                        window.location.href = "/machine_update.html?id=" + machineDto.id;
                    });

                    var deleteButton = $("<button class='delete-button'>").text("Видалити").click(function () {
                        $.ajax({
                            url: '/machine/' + machineDto.id,
                            type: 'DELETE',
                            success: function () {
                                location.reload();
                            },
                            error: function (error) {
                                console.error('Помилка при видаленні машини:', error);
                            }
                        });
                    });

                    listItem.append(machineName, editButton, deleteButton);
                    $("#machine-list-container").append(listItem);
                });
            },
            error: function () {
                alert("Помилка при отриманні списку машин");
            }
        });
    }

    // Инициализация страницы
    getMachines();

    // Обработчик клика по кнопке "Добавить машину"
    $("#add-machine").on("click", function () {
        var machineName = $("#machine-name").val();

        // Создаем объект с данными машины
        var machineData = {
            name: machineName
        };

        // Отправляем POST-запрос на сервер
        $.ajax({
            url: "/machine",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(machineData),
            dataType: "json",
            success: function (data) {
                alert("Добавлена нова машина: " + data.name);

                $("#machine-name").val('');

                getMachines();
            },
            error: function () {
                alert("Помилка при видаленні машини");
            }
        });
    })

    // Дополнительные обработчики для других страниц (Главная, Продукты, Графики, Отчеты)
    // Добавьте собственные обработчики событий по аналогии с вышеуказанными.
});
