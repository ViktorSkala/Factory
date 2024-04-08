$(document).ready(function () {
    $("#base-stoppage").on("click", function () {
        window.location.href = "base-stoppage.html";
    });

    $("#sub-stoppage").on("click", function () {
        window.location.href = "sub-stoppage.html";
    });

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

                $("#machine-name").append("<option value='' selected>Оберіть машину</option>");
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

    function fillProductDropdown() {
        $.ajax({
            url: "/product/allDto",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#product-name").empty();

                $("#product-name").append("<option value='' selected>Оберіть продукт</option>");
                data.forEach(function (productDto) {
                    $("#product-name").append("<option value='" + productDto.id + "'>" + productDto.productName + " " + productDto.machineName + "</option>");
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

    fillProductDropdown();

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

                $("#base-type-name").append("<option value='' selected>Оберіть базовий тип</option>");
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

    function fillSubTypesDropdown() {
        $.ajax({
            url: "/sub_type_stoppage/all",
            type: "GET",
            dataType: "json",
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                $("#sub-type-name").empty();

                $("#sub-type-name").append("<option value='' selected>Оберіть підтип</option>");
                data.forEach(function (subStoppageDto) {
                    $("#sub-type-name").append("<option value='" + subStoppageDto.id + "'>" + subStoppageDto.name + "</option>");
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

    fillSubTypesDropdown();

    $("#machine-name").on("change", function () {
        var machineId = $(this).val();

        if (machineId) {
            $.ajax({
                url: "/product/all/byMachineId/" + machineId,
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#product-name").empty();

                    $("#product-name").append("<option value='' selected>Оберіть продукт</option>");
                    data.forEach(function (productDto) {
                        $("#product-name").append("<option value='" + productDto.id + "'>" + productDto.productName + "</option>");
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
        } else {
            fillProductDropdown();
        }
    });

    $("#base-type-name").on("change", function () {
        var baseStoppageId = $(this).val();

        if (baseStoppageId) {
            $.ajax({
                url: "/sub_type_stoppage/all/by_base_stoppage/" + baseStoppageId,
                type: "GET",
                dataType: "json",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                },
                success: function (data) {
                    $("#sub-type-name").empty();

                    $("#sub-type-name").append("<option value='' selected>Оберіть підтип</option>");
                    data.forEach(function (subStoppage) {
                        $("#sub-type-name").append("<option value='" + subStoppage.id + "'>" + subStoppage.name + "</option>");
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
        } else {
            fillSubTypesDropdown();
        }
    });

    var currentPage = 0;
    var totalPages = 1;

    $("#firstPage").on("click", function () {
        currentPage = 0;
        fetchData();
    });

    $("#prevPage").on("click", function () {
        if (currentPage > 0) {
            currentPage--;
            fetchData();
        }
    });

    $("#nextPage").on("click", function () {
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchData();
        }
    });

    $("#lastPage").on("click", function () {
        currentPage = totalPages - 1;
        fetchData();
    });

    function fetchData() {
        const machineId = $("#machine-name").val();
        const productId = $("#product-name").val();
        const baseTypeStoppageId = $("#base-type-name").val();
        const subTypeStoppageId = $("#sub-type-name").val();
        const startDate = new Date(($("#datepicker-start").val() || "1900-01-01") + 'T' + ($("#timepicker-start").val() || "00:00:00") + 'Z');
        const endDate = new Date(($("#datepicker-end").val() || "2100-01-01") + 'T' + ($("#timepicker-end").val() || "23:59:59") + 'Z');
        const durationStart = (parseInt($("#days-start").val()) || 0) * 86400 + (parseInt($("#hours-start").val()) || 0) * 3600 + (parseInt($("#minutes-start").val()) || 0) * 60 + (parseInt($("#seconds-start").val()) || 0);
        const durationEnd = (parseInt($("#days-end").val()) || 0) * 86400 + (parseInt($("#hours-end").val()) || 0) * 3600 + (parseInt($("#minutes-end").val()) || 0) * 60 + (parseInt($("#seconds-end").val()) || 0);
        const finalDurationEnd = durationEnd === 0 ? 9999999999 : durationEnd;
        const pageSize = parseInt($("#stoppagesOnPage").val()) || 20;

        const stoppageFilterDto = {
            machineId: machineId,
            productId: productId,
            baseTypeStoppageId: baseTypeStoppageId,
            subTypeStoppageId: subTypeStoppageId,
            startDate: startDate,
            endDate: endDate,
            durationStart: durationStart,
            durationEnd: finalDurationEnd
        };

        $.ajax({
            url: `/stoppage/all_by_criteria_paged?page=${currentPage}&size=${pageSize}`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(stoppageFilterDto),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                totalPages = data.totalPages;
                $("#currentPage").text(currentPage + 1);

                $("#stoppage-list-container").empty();

                data.content.forEach(function (stoppageResponseDto) {
                    var listItem = $("<li>").addClass("stoppage-item");
                    var startDate = $("<span>").text(stoppageResponseDto.startDate);
                    var duration = $("<span>").text(stoppageResponseDto.duration);
                    var product = $("<span>").text(stoppageResponseDto.productName);
                    var machine = $("<span>").text(stoppageResponseDto.machineName);
                    var baseType = $("<span>").text(stoppageResponseDto.baseTypeStoppageName);
                    var subType = $("<span>").text(stoppageResponseDto.subTypeStoppageName);
                    var manageButton = $("<button>").text("Керувати").click(function () {
                        window.location.href = "/stoppage-update.html?stoppageId=" + stoppageResponseDto.id;
                    });

                    listItem.append(startDate, duration, product, machine, baseType, subType, manageButton);
                    $("#stoppage-list-container").append(listItem);
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

    $("#find-stoppage").on("click", function () {
        fetchData();
    });

    $("#showDiagram").on("click", function () {

        const machineId = $("#machine-name").val();
        const productId = $("#product-name").val();
        const baseTypeStoppageId = $("#base-type-name").val();
        const subTypeStoppageId = $("#sub-type-name").val();
        const startDate = new Date(($("#datepicker-start").val() || "1900-01-01") + 'T' + ($("#timepicker-start").val() || "00:00:00") + 'Z');
        const endDate = new Date(($("#datepicker-end").val() || "2100-01-01") + 'T' + ($("#timepicker-end").val() || "23:59:59") + 'Z');
        const durationStart = (parseInt($("#days-start").val()) || 0) * 86400 + (parseInt($("#hours-start").val()) || 0) * 3600 + (parseInt($("#minutes-start").val()) || 0) * 60 + (parseInt($("#seconds-start").val()) || 0);
        const durationEnd = (parseInt($("#days-end").val()) || 0) * 86400 + (parseInt($("#hours-end").val()) || 0) * 3600 + (parseInt($("#minutes-end").val()) || 0) * 60 + (parseInt($("#seconds-end").val()) || 0);
        const finalDurationEnd = durationEnd === 0 ? 9999999999 : durationEnd;

        const stoppageFilterDto = {
            machineId: machineId,
            productId: productId,
            baseTypeStoppageId: baseTypeStoppageId,
            subTypeStoppageId: subTypeStoppageId,
            startDate: startDate,
            endDate: endDate,
            durationStart: durationStart,
            durationEnd: finalDurationEnd
        };

        var selectedParameter = $("#group-by-parameter").val();

        var canvas = document.getElementById('barChart');

        canvas.width = 1200;
        canvas.height = 400;

        $.ajax({
            url: "/stoppage/all_by_criteria",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(stoppageFilterDto),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (data) {
                var durationSumByParameter = {};

                data.forEach(function (stoppageResponseDto) {

                    var groupByValue = stoppageResponseDto[selectedParameter];
                    if (!durationSumByParameter[groupByValue]) {
                        durationSumByParameter[groupByValue] = 0;
                    }
                    durationSumByParameter[groupByValue] += stoppageResponseDto.durationInSec;
                });

                var chartLabels = Object.keys(durationSumByParameter);
                var chartData = Object.values(durationSumByParameter);

                displayBarChart(chartLabels, chartData);
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

    var myChart;

    function displayBarChart(labels, data) {
        if (myChart) {
            myChart.destroy();
        }
        var ctx = document.getElementById('barChart').getContext('2d');
        myChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Сумма продолжительности простоев',
                    data: data,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                },
                maintainAspectRatio: true,
                responsive: false,
            }
        });
    }
});
