import React, {Component} from "react";

export class StatusIcon extends Component {
    switchStatus() {
        switch (this.props.status) {
            case "APPLIED":
                return "glyphicon glyphicon-ok";
            case "DESTROYED":
                return "glyphicon glyphicon-remove";
            case "IN_PROGRESS":
                return "fa fa-circle-o-notch fa-spin";
            case "FAILED":
                return "fa fa-exclamation-circle";
            default:
                return "";
        }
    }

    render() {
        return (
            <span className={this.switchStatus()}/>
        );
    }
}