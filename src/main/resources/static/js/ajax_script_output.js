var interval;
var resultDiv = $("#postResultDiv");

$(document).ready(function () {
    var id = $("#handlerId").html();
    ajax_get_script_output(id);
    interval = setInterval(function () {
        ajax_get_script_output(id);
    }, 500);
});


function ajax_get_script_output(id) {
    $.ajax({
        type: "GET",
        data: {"id": id},
        url: "/api/new-output/",
        cache: false,
        timeout: 600000,
        success: function (data) {
            var json = data["newText"];
            if (json !== "") {
                resultDiv.append(json + "\n");
            }
            if (data["end"] === true) {
                clearInterval(interval);

                ajax_stop_script_output(id);
                return;
            }
            console.log("SUCCESS : ", data);

        },
        error: function (e) {
            clearInterval(interval);
            resultDiv.append("The error is gotten during the operation executing: " + e.responseText);
            console.log("SCRIPT ERROR : ", e);
        }
    });
}

function ajax_stop_script_output(id) {
    var data = {
        "id": id,
        "command": $("#command").text()
    };

    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: "/api/end-new-output",
        data: JSON.stringify(data),
        cache: false,
        dataType: "json",
        timeout: 100000,
        success: function (result) {
            console.log("STOP SUCCESS: ");
            if (result["success"] === true) {
                resultDiv.append("\n-----------------------\nThe script executing is successfully completed.");
            }
            else {
                resultDiv.append("\n-----------------------\nThe script executing is not completed.");
            }
            $("#loader-icon").hide();
            console.log("SCRIPT SUCCESS: ", data);
        },
        error: function (e) {
            $("#loader-icon").hide();
            console.log("STOP ERROR : ", e);
        },
        done: function () {
            console.log("STOP DONE");
        }
    });
}
