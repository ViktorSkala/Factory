$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const machineId = urlParams.get('id');

    $.ajax({
        url: '/machine/' + machineId,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
        },
        success: function (machineDto) {
            $('#machine-name').val(machineDto.name);

            $('#update-machine').on('click', function () {
                const updatedMachineData = {
                    name: $('#machine-name').val(),
                };

                $.ajax({
                    url: '/machine/' + machineId,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(updatedMachineData),
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("jwtToken")
                    },
                    success: function () {
                        alert("Машина успішно відредагована")
                        window.location.href = '/machine.html';
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
