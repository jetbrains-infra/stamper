import React, {Component} from "react";
import {MenuItem, SplitButton} from "react-bootstrap";
import {MainInfo} from "./main_info";
import {StackTabs} from "./tabs";

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}, logs: {}, stateInfo: "There are any info yet"};
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
                this.updateStateInfo();
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
                console.log(error);
            });
    }

    stopUpdate() {
        this.updateStackTimer && clearInterval(this.updateStackTimer);
        this.updateStackTimer = false;
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

    updateStateInfo() {
        const stack = this.state.stack;
        fetch(`/api/stack/${stack.name}/status`, {method: "get", credentials: "same-origin"})
            .then(result => result.json())
            .then(data => {
                this.setState((prevState) => {
                    const newState = prevState;
                    newState.stateInfo = data.stateInfo;
                    return newState;
                });
            });
    }

    componentDidMount() {
        this.updateStatus(true);
    }

    componentWillUnmount() {
        this.stopUpdate();
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

                <StackTabs stack={this.state.stack} logs={this.state.logs} stateInfo={this.state.stateInfo}/>
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
        <div className="btn-group button-right margin-btn">
            <SplitButton bsStyle='danger' title='Destroy' id='destroy-btn'
                         onClick={() => props.destroy(false)}>
                <MenuItem eventKey="1" onClick={() => props.destroy(true)}>Force Destroy</MenuItem>
            </SplitButton>
        </div>
    );
};