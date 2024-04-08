$(document).ready(function () {

    const urlParams = new URLSearchParams(window.location.search);
    const stoppageId = urlParams.get('stoppageId');

    $.ajax({
        url: "/stoppage/not_full/" + encodeURIComponent(stoppageId),
        type: "GET",
        dataType: "json",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (stoppageCreateResponseDto) {
            $("#machine-name").val(stoppageCreateResponseDto.machineName).attr('readonly', true).data("machine-id", stoppageCreateResponseDto.machineId);
            $("#product-name").val(stoppageCreateResponseDto.productName).attr('readonly', true).data("product-id", stoppageCreateResponseDto.productId);
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

                    $("#sub-type-name").append("<option value='' disabled selected>Оберіть підтип</option>");
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
        }
    });

    $("#create-stoppage").on("click", function () {
        const productId = $("#product-name").data("product-id");
        const machineId = $("#machine-name").data("machine-id");
        const baseTypeStoppageId = $("#base-type-name").val();
        const subTypeStoppageId = $("#sub-type-name").val();

        const stoppageCreateDto = {
            productId: productId,
            machineId: machineId,
            baseTypeStoppageId: baseTypeStoppageId,
            subTypeStoppageId: subTypeStoppageId
        };

        $.ajax({
            url: "/stoppage/not_full/" + encodeURIComponent(stoppageId),
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(stoppageCreateDto),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
            },
            success: function (stoppageResponseDto) {
                var threadName = stoppageResponseDto.productName + " " + stoppageResponseDto.machineName;

                $.ajax({
                    url: "/production/resume/" + encodeURIComponent(threadName),
                    type: "GET",
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                    },
                    success: function () {
                        window.location.href = "/manage-production.html?threadName=" + threadName;
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
    });
});
