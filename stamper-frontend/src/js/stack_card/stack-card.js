import React, {Component} from "react";
import {MainInfo} from "./main_info";
import {StackTabs} from "./tabs";

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}, logs: {}};
        this.applyStack = this.applyStack.bind(this);
        this.deleteStack = this.deleteStack.bind(this);
    }

    loadStackFromServer() {
        fetch(`/api/stack/${this.props.match.params.stack_name}`, {method: "get", credentials: "same-origin"})
            .then(result => result.json())
            .then(data => {
                if (data.status === "APPLIED" || data.status === "FAILED") {
                    this.stopUpdate();
                }
                this.setState({stack: data});
                this.updateLogs(data.operations);
            })
            .catch(() => {
                this.stopUpdate();
            });
    }

    applyStack() {
        fetch(`/api/stack/${this.state.stack.name}/apply`, {method: "post", credentials: "same-origin"})
            .then(() => this.updateStatus(true));
    }

    updateStatus(firstTime) {
        if (firstTime === true) {
            this.updateStackTimer = setInterval(
                () => this.updateStatus(false), 1000);
        }
        this.loadStackFromServer();


    }

    deleteStack(force) {
        const url = `/api/stack/${this.state.stack.name}?force=${force}`;
        fetch(url, {method: "delete", credentials: "same-origin"})
            .then(() => this.updateStatus(true))
            .catch((error) => {
                this.stopUpdate();
                console.log("error");
            });
    }

    stopUpdate() {
        clearInterval(this.updateStackTimer);
    }

    updateLogs(operations) {
        operations.forEach(id => this.updateLog(id));
    }

    updateLog(id) {
        fetch(`/api/log/${id}`, {method: "get", credentials: "same-origin"})
            .then(result => result.json())
            .then(data => {
                this.setState((prevState) => {
                    const newState = prevState;
                    newState.logs[id] = data;
                    return newState;
                });
            });
    }

    componentDidMount() {
        this.updateStatus(true);
    }

    render() {
        return (
            <div>
                <div className="row">
                    <div className="col-md-8">
                        <MainInfo stack={this.state.stack}/>
                    </div>
                    <div className="col-md-4">
                        <div className="row button-group flex pull-right">
                            <StackApply stack={this.state.stack} apply={this.applyStack}/>
                            <StackDestroy stack={this.state.stack} destroy={this.deleteStack}/>
                        </div>
                    </div>
                </div>

                <StackTabs stack={this.state.stack} logs={this.state.logs}/>
            </div>
        );
    }
}

const StackApply = (props) => {
    if (props.stack.status !== "FAILED") {
        return <div/>;
    }
    return (
        <button id="apply-btn" onClick={props.apply} className="btn btn-success  margin-btn">
            Apply
        </button>
    );
};


const StackDestroy = (props) => {
    if (props.stack.status !== "APPLIED" && props.stack.status !== "FAILED") {
        return <div/>;
    }
    return (
        <div className="btn-group button-right" id="destroy-group">
            <button type="button" id="destroy-btn" className="btn btn-danger margin-btn"
                    onClick={() => props.destroy(false)}>Destroy
            </button>
            <button type="button" className="btn btn-danger dropdown-toggle" data-toggle="dropdown">
                <span className="caret"/>
            </button>
            <ul className="dropdown-menu" role="menu">
                <li>
                    <button type="button" id="force-destroy-btn" className="btn btn-danger margin-btn"
                            onClick={() => props.destroy(true)}>Force
                        destroy
                    </button>
                </li>
            </ul>
        </div>
    );
};