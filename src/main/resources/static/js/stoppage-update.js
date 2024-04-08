$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const stoppageId = urlParams.get('stoppageId');

    $.ajax({
        url: '/stoppage/' + stoppageId,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (stoppageResponceDto) {
            $.ajax({
                url: "/machine/all",
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#machine-name").empty();

                    data.forEach(function (machineDto) {
                        const option = $("<option></option>")
                            .attr("value", machineDto.id)
                            .text(machineDto.name);

                        if (machineDto.id === stoppageResponceDto.machineId) {
                            option.attr("selected", "selected");
                        }

                        $("#machine-name").append(option);
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

            $.ajax({
                url: "/product/allDto",
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#product-name").empty();

                    data.forEach(function (productDto) {
                        const option = $("<option></option>")
                            .attr("value", productDto.id)
                            .text(productDto.productName);

                        if (productDto.id === stoppageResponceDto.productId) {
                            option.attr("selected", "selected");
                        }

                        $("#product-name").append(option);
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

            $.ajax({
                url: "/base_type_stoppage/all",
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#base-type-name").empty();

                    data.forEach(function (baseStoppageDto) {
                        const option = $("<option></option>")
                            .attr("value", baseStoppageDto.id)
                            .text(baseStoppageDto.name);

                        if (baseStoppageDto.id === stoppageResponceDto.baseTypeStoppageId) {
                            option.attr("selected", "selected");
                        }

                        $("#base-type-name").append(option);
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

            $.ajax({
                url: "/sub_type_stoppage/all",
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#sub-type-name").empty();

                    data.forEach(function (subStoppageDto) {
                        const option = $("<option></option>")
                            .attr("value", subStoppageDto.id)
                            .text(subStoppageDto.name);

                        if (subStoppageDto.id === stoppageResponceDto.subTypeStoppageId) {
                            option.attr("selected", "selected");
                        }

                        $("#sub-type-name").append(option);
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

            const startDateParts = stoppageResponceDto.startDate.split(' ');
            const startDate = startDateParts[0].split('.').reverse().join('-');

            console.log(startDateParts);
            console.log(startDate);

            const endDateParts = stoppageResponceDto.endDate.split(' ');
            const endDate = endDateParts[0].split('.').reverse().join('-');

            $("#datepicker-start").val(startDate);
            $("#timepicker-start").val(startDateParts[1]);
            $("#datepicker-end").val(endDate);
            $("#timepicker-end").val(endDateParts[1]);

            $('#update-product').on('click', function () {
                const updatedStoppageData = {
                    machineId: $("#machine-name").val(),
                    productId: $("#product-name").val(),
                    baseTypeStoppageId: $("#base-type-name").val(),
                    subTypeStoppageId: $("#sub-type-name").val(),
                    startDate: new Date($("#datepicker-start").val() + 'T' + $("#timepicker-start").val() + 'Z'),
                    endDate: new Date($("#datepicker-end").val() + 'T' + $("#timepicker-end").val() + 'Z'),
                };

                $.ajax({
                    url: '/stoppage/' + stoppageId,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedStoppageData),
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                    },
                    success: function () {
                        window.location.href = '/stoppage.html';
                    },
                    error: function (error) {
                        console.error('Помилка при оновленні простою:', error);
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
