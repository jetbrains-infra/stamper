var stack_name;

$(document).ready(function () {
    stack_name = $("#stack_name");
    hide_status_icons();
    get_stack_status(stack_name.text());
});


var interval;
var lastRead = 0;


function hide_status_icons() {
    $("#loader-icon").hide();
    $("#applied-icon").hide();
    $("#destroyed-icon").hide();
    $("#failed-icon").hide();
}

function get_stack_status(stack_name) {
    var status_status = $("#stack_status");
    var status_info = $("#stack_status_info");
    hide_status_icons();
    $.ajax({
        type: "GET",
        url: "/api/stack/" + stack_name + "/status",
        cache: false,
        timeout: 600000,
        success: function (data) {
            status_status.text(data["stackStatus"]);
            if (data["stackStatus"] === "APPLIED") {
                status_info.append("Stack status:\n");
                status_info.append(data["output"]);
                $("#applied-icon").show();
            }
            if (data["stackStatus"] === "FAILED") {
                status_info.append("Stack status:\n");
                status_info.append(data["output"]);
                $("#failed-icon").show();
            }
            if (data["stackStatus"] === "IN_PROGRESS") {
                var id = data["commandId"];
                $("#loader-icon").show();
                interval = setInterval(function () {
                    ajax_get_script_output(id, status_info);
                }, 1300);
            }
            if (data["stackStatus"] === "DESTROYED") {
                $("#destroyed-icon").show();
                status_info.append("Stack status:\nThe stack is completely destroyed now.\n");
                status_info.append(data["output"]);
            }
            console.log("SUCCESS get api/stack/" + stack_name + "/status", data);
        },
        error: function (e) {
            console.log("ERROR get api/stack/" + stack_name + "/status", e);
        }
    });
}

function ajax_get_script_output(id, status) {
    $.ajax({
        type: "GET",
        data: {
            "id": id,
            "start": lastRead
        },
        url: "/api/new-output/",
        cache: false,
        timeout: 600000,
        success: function (data) {
            var json = data["newText"];
            lastRead = data["last"];
            if (json !== "") {
                status.append(json + "\n");
            }
            if (data["end"] === true) {
                clearInterval(interval);
                $("#loader-icon").hide();
                var exception = data["exception"];
                if (exception === null) {
                    status.append("\n-----------------------\nThe script executing is successfully completed.\n");
                }
                else {
                    status.append("\n-----------------------\nThe script executing is  uncompleted.\n");
                    status.append(exception);
                }
                get_stack_status(stack_name.text());
                return;
            }
            console.log("SUCCESS : ", data);

        },
        error: function (e) {
            clearInterval(interval);
            resultDiv.append("The error is gotten during the operation executing: " + e.responseText);
            console.log("SCRIPT ERROR : ", e);
            $("#loader-icon").hide();
        }
    });
}
