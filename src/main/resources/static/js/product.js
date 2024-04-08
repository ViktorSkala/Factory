$(document).ready(function () {

    function getProducts() {
        $.ajax({
            url: "/product/allDto",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#product-list-container").empty();

                data.forEach(function (productDto) {
                    var listItem = $("<li>").addClass("product-item");

                    var productName = $("<div>").addClass("product-name").text(productDto.productName);
                    var numbersInPack = $("<div>").addClass("product-name").text(productDto.numbersInPack + " рул/уп");
                    var machineName = $("<div>").addClass("product-name").text(productDto.machineName);
                    var productivity = $("<div>").addClass("product-name").text(productDto.expectedProductivity + " уп/хв");

                    var editButton = $("<button class='edit-button'>").text("Редагувати").click(function () {
                        window.location.href = "/product-update.html?id=" + productDto.id;
                    });

                    var deleteButton = $("<button class='delete-button'>").text("Видалити").click(function () {
                        $.ajax({
                            url: '/product/' + productDto.id,
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

                    listItem.append(productName, numbersInPack, machineName, productivity, editButton, deleteButton);
                    $("#product-list-container").append(listItem);
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

    getProducts();

    function fillMachineDropdown() {
        $.ajax({
            url: "/machine/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#machine-name").empty();

                $("#machine-name").append("<option value='' disabled selected>Выберите машину</option>");
                data.forEach(function (machine) {
                    $("#machine-name").append("<option value='" + machine.id + "'>" + machine.name + "</option>");
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

    fillMachineDropdown();

    $("#add-product").on("click", function () {
        var name = $("#product-name").val();
        var machineId = $("#machine-name").val();
        var numbersInPack = $("#product-numbersInPack").val();
        var expectedProductivity = $("#product-expectedProductivity").val();

        var productData = {
            name: name,
            machineId: machineId,
            numbersInPack: numbersInPack,
            expectedProductivity: expectedProductivity
        };

        $.ajax({
            url: "/product",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(productData),
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (productResponseDto) {
                alert("Добавлен новый продукт: " + productResponseDto.productName);

                $("#product-name").val('');
                $("#machine-name").val('');
                $("#product-numbersInPack").val('');
                $("#product-expectedProductivity").val('');

                getProducts();
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
