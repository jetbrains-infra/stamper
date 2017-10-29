import React, {Component} from "react";
import {MainInfo} from "./main_info";
import {Tabs} from "./tabs";

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}};
    }

    loadStackFromServer() {
        fetch(`/api/stack/${this.props.match.params.stack_name}`, {method: 'get', credentials: 'same-origin'})
            .then(result => result.json())
            .then(data => this.setState({stack: data}));
    }

    componentDidMount() {
        this.loadStackFromServer();
    }

    render() {
        return (
            <div>
                <MainInfo stack={this.state.stack}/>
                <Tabs/>
            </div>
        );
    }
}


