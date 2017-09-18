var run_form = $("#run_form");
var stack_name = $("#stack_name");
var stack_name_error = $("#stack_name_error_span");

$(document).ready(function () {
    var stack_name_error = $("#stack_name_error_span");
    run_form.submit(function (event) {
        event.preventDefault();
        stack_name.parent().removeClass("has-error");
        stack_name_error.hide();
        validate_stack_name(stack_name.val());
    });
});


function validate_stack_name(id) {
    $.ajax({
        type: "GET",
        url: "api/stack/" + id,
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS get api/stack/" + id, data);

            if (jQuery.isEmptyObject(data)) {
                run_form[0].submit();
            }
            else {
                stack_name.parent().addClass("has-error");
                stack_name_error.show();
            }
        },
        error: function (e) {
            console.log("SCRIPT ERROR get api/stack/" + id, e);
        }
    });
}