import React, {Component} from "react";
import {MainInfo} from "./main_info";
import {StackTabs} from "./tabs";

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}, logs: {}};
    }

    loadStackFromServer() {
        fetch(`/api/stack/${this.props.match.params.stack_name}`, {method: 'get', credentials: 'same-origin'})
            .then(result => result.json())
            .then(data => {
                this.setState({stack: data});
                this.updateLogs(data.operations);
            });
    }

    updateLogs(operations) {
        operations.forEach(id => this.updateLog(id));
    }

    updateLog(id) {
        fetch(`/api/log/${id}`, {method: 'get', credentials: 'same-origin'})
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
        this.loadStackFromServer();
    }

    render() {
        return (
            <div>
                <MainInfo stack={this.state.stack}/>
                <StackTabs stack={this.state.stack} logs={this.state.logs}/>
            </div>
        );
    }
}


