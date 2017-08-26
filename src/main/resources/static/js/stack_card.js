const stackName = getStackName();

function getStackName() {
    const url = window.location.pathname;
    return url.substring(url.lastIndexOf("/") + 1);
}

$(document).ready(function () {
    const stackName = getStackName();

    function getStackName() {
        const url = window.location.pathname;
        return url.substring(url.lastIndexOf("/") + 1);
    }

    $("#executing_operation").hide();
    updateStackCardInfo(stackName);
});


function updateStackCardInfo() {
    updateLogs();
    updateStackStatus();
    updateStackParams();
}

const stack_status = $("#stack_status");
const status_info = $("#stack_status_info");


function hide_elements() {
    $("#apply-btn").hide();
    $("#destroy-btn").hide();
    $("#loader-icon").hide();
    $("#applied-icon").hide();
    $("#destroyed-icon").hide();
    $("#failed-icon").hide();
}


function updateStackStatus() {
    hide_elements();

    $.ajax({
        type: "GET",
        url: "/api/stack/" + stackName + "/status",
        cache: false,
        timeout: 600000,
        success: function (data) {
            stack_status.html(data["stackStatus"]);

            function onApply() {
                status_info.html(data["output"]);
                $("#applied-icon").show();
                $("#destroy-btn").show();
            }

            function onFail() {
                status_info.html(data["output"]);
                $("#failed-icon").show();
                $("#apply-btn").show();
                $("#destroy-btn").show();
            }

            function onDestroyed() {
                $("#destroyed-icon").show();
                status_info.html("The stack is completely destroyed now.\n");
            }

            function onInProgress() {
                status_info.html("The stack is not created. Status is unavailable");
                const id = data["commandId"];
                $("#loader-icon").show();
                const operationLog = new OperationLog(id, updateStackCardInfo, updateStackCardInfo);
                operationLog.run();
            }

            switch (data["stackStatus"]) {
                case "APPLIED":
                    onApply();
                    break;
                case"FAILED":
                    onFail();
                    break;
                case "IN_PROGRESS":
                    onInProgress();
                    break;
                case "DESTROYED":
                    onDestroyed();
                    break;
                default:
                    alert("Unknown stack status");
            }
            console.log(`SUCCESS get api/stack/${stackName}/status`, data);
        },
        error: function (e) {
            console.log(`ERROR get api/stack/${stackName}/status`, e);
        }
    });
}


function updateLogs() {
    $.ajax({
        type: "GET",
        url: "/api/stack/" + stackName + "/logs",
        cache: false,
        timeout: 600000,
        success: function (logs) {
            let log_div = $("#logs").html("");
            logs.forEach(function (t) {
                renderLog(log_div, t);
            });
            console.log("SUCCESS get logs : ", logs);

        },
        error: function (e) {
            console.log("ERROR get logs: ", e);
        }
    });
}

function updateStackParams() {
    let param_element = $("#stack-params");

    $.ajax({
        type: "GET",
        url: "/api/stack/" + stackName,
        cache: false,
        timeout: 600000,
        success: function (data) {
            param_element.html("");
            let params = data["params"];
            if (jQuery.isEmptyObject(params)) {
                renderParamsUnexist(param_element);
            }
            Object.entries(params).forEach(
                ([key, value]) => renderParam(param_element, key, value)
            );
            console.log(`SUCCESS get api/stack/${stackName}`, data);
        },
        error: function (e) {
            console.log(`ERROR get api/stack/${stackName}`, e);
        }
    });
}

function renderLog(log_div, operation) {
    log_div.append(`
        <div>
            <h3><a href="#${operation.id}" data-toggle="collapse">${operation.title}</a></h3>
            <div id="${operation.id}" class="collapse" >
                <pre>${operation["executeResult"]["output"]}</pre>
            </div>
        </div>
    `);
}

function renderParam(param_element, param_name, param_value) {
    param_element.append(`
        <p>
            <strong>${param_name}</strong>: ${param_value}
        </p>
    `);
}

function renderParamsUnexist(param_element) {
    param_element.append(`
        <p class="text-muted">
            <i>There are not parameters for this stack</i>
        </p>
    `);
}


$("#destroy-btn").click(function () {
    const stackDestroyer = new StackDestroyer();
    stackDestroyer.run();
});

class StackDestroyer {
    static updateViewToDestroyed() {
        hide_elements();
        status_info.html("Stack is destroyed and deleted.");
        stack_status.html("DESTROYED");
        $("#destroyed-icon").show();
    }

    run() {
        hide_elements();
        stack_status.html("IN_PROGRESS");
        $("#loader-icon").show();
        this.runDestroy();
    }

    runDestroy() {
        let operationId;
        $.ajax({
            type: "DELETE",
            url: "/api/stack/" + stackName,
            cache: false,
            timeout: 600000,
            success: function (id) {
                operationId = id;
                const operationLog = new OperationLog(id, StackDestroyer.updateViewToDestroyed, updateStackCardInfo);
                operationLog.run();
                console.log("SUCCESS get destroy operation id: ", stackName);
            },
            error: function (e) {
                console.log("ERROR on get destroy operation id: ", e);
            }
        });
    }
}

class OperationLog {
    constructor(operationId, successHandler, failHandler) {
        this.id = operationId;
        this.success = successHandler;
        this.fail = failHandler;
        this.operationDiv = $("#executing_operation");
        this.outputField = this.operationDiv.find("#operation_output");
        this.outputField.html("");
        this.isCompleted = false;
    }

    run() {
        this.lastRead = 0;
        this.operationDiv.show();

        let context = this;
        this.interval = setInterval(function () {
            context.loadOperationOutput(context);
        }, 600);
    }

    loadOperationOutput(context) {
        $.ajax({
            type: "GET",
            data: {
                "id": this.id,
                "start": this.lastRead
            },
            url: "/api/new-output/",
            cache: false,
            timeout: 600000,
            success: function (data) {
                context.onSuccess(data);
            },
            error: function (e) {
                context.onFail(e);
            }
        });
    }

    onFail(e) {
        clearInterval(this.interval);
        this.outputField.append("Fail load operation output: " + e.responseText);
        this.fail();
    }

    onSuccess(data) {
        if (this.isCompleted === true) {
            return;
        }

        const json = data["newText"];
        this.lastRead = data["last"];

        if (json !== "" && !this.isCompleted) {
            this.outputField.append(json + "\n");
        }
        if (data["end"] === true) {
            this.isCompleted = true;
            clearInterval(this.interval);
            const exception = data["exception"];
            if (exception === null) {
                this.outputField.append(
                    "\n-----------------------\nThe script executing is successfully completed.\n");
                this.success();
            }
            else {
                this.outputField.append("\n-----------------------\nThe script executing is  failed.\n");
                this.outputField.append(exception);
                this.fail();
            }

        }
        console.log("Success load operation output: ", data);
    }
}
