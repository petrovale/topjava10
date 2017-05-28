var ajaxUrl = "ajax/profile/meals/";
var datatableApi;

$('#startDate').datetimepicker({
    timepicker:false,
    format:'d.m.Y'
});

$('#startTime').datetimepicker({
    datepicker:false,
    format:'H:i'
});

$('#endTime').datetimepicker({
    datepicker:false,
    format:'H:i'
});

$('#endDate').datetimepicker({
    timepicker:false,
    format:'d.m.Y'
});

function updateTable() {
    var newStartDate = $("#startDate").val().substring(6, 10) + "-"
        + $("#startDate").val().substring(3, 5) + "-"
        + $("#startDate").val().substring(0, 2);
    var newEndDate = $("#endDate").val().substring(6, 10) + "-"
        + $("#endDate").val().substring(3, 5) + "-"
        + $("#endDate").val().substring(0, 2);

    $.ajax({
        type: "POST",
        url: ajaxUrl + "filter",
        data: {
            startDate: (newStartDate.length >2) ? newStartDate : "",
            startTime: $("#startTime").val(),
            endDate: (newEndDate.length >2) ? newEndDate : "",
            endTime: $("#endTime").val()
        },
        success: updateTableByData
    });
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(ajaxUrl, updateTableByData);
}

$(function () {
    datatableApi = $("#datatable").DataTable({
        "ajax": {
            "url": ajaxUrl,
            "dataSrc": ""
        },
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "dateTime",
                "render": function (date, type, row) {
                    if (type === 'display') {
                        return '<span>' + date.substring(0, 10) + " " + date.substring(11, 19) + '</span>';
                    }
                    return date;
                }
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "defaultContent": "Edit",
                "orderable": false,
                "render": renderEditBtn
            },
            {
                "defaultContent": "Delete",
                "orderable": false,
                "render": renderDeleteBtn
            }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ],
        "createdRow": function (row, data, dataIndex) {
            if (data.exceed) {
                $(row).addClass("exceeded");
            } else {
                $(row).addClass("normal");
            }
        },
        "initComplete": makeEditable
    });
});
