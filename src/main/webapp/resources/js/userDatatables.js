var ajaxUrl = 'ajax/admin/users/';
var datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $('#datatable').DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "email"
            },
            {
                "data": "roles"
            },
            {
                "data":   "enabled"
            },
            {
                "data": "registered"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
    makeEditable();

    $('input:checkbox').click(function () {
        var check_id = $(this).attr("id");
        var check = $(this).prop( 'checked' );

        $.ajax({
            url: ajaxUrl + check_id,
            type: 'POST',
            data: "checked="+check,
            success: function () {
                updateTableNew();
                successNoty('update');
            }
        });
    });
});

function updateTableNew() {
    $.get(ajaxUrl, function (data) {
        datatableApi.clear();
        datatableApi.rows.add(data);
        datatableApi.draw();
    });
}