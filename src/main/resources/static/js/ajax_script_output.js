var interval;
var resultDiv = $("#postResultDiv");

var lastRead = 0;

$(document).ready(function () {
    var id = $("#handlerId").html();
    ajax_get_script_output(id);
    interval = setInterval(function () {
        ajax_get_script_output(id);
    }, 1300);
});

var isEnd = false;

function ajax_get_script_output(id) {
    if (isEnd === true) {
        return;
    }
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
            if (json !== "" && isEnd === false) {
                resultDiv.append(json + "\n");
            }
            if (data["end"] === true) {
                isEnd = true;
            }
            if (isEnd === true) {
                clearInterval(interval);
                $("#loader-icon").hide();
                var exception = data["exception"];
                if (exception === null) {
                    resultDiv.append("\n-----------------------\nThe script executing is successfully completed.");
                }
                else {
                    resultDiv.append("\n-----------------------\nThe script executing is  uncompleted.");
                    resultDiv.append(exception);
                }
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
