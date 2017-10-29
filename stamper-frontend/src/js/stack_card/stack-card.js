import React, {Component} from "react";
import Timestamp from "react-timestamp";
import {StatusIcon} from "../common";
import $ from 'jquery'

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}};
    }

    loadStackFromServer() {
        const self = this;
        $.ajax({
            type: "GET", url: `/api/stack/${this.props.match.params.stack_name}`, cache: false
        }).then(function (data) {
            self.setState({stack: data});
        });
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

class MainInfo extends Component {
    render() {
        const stack = this.props.stack;
        if (Object.keys(stack).length === 0) {
            return (<div/>);
        }
        return (
            <div>
                <h1>Stack: {stack.name}</h1>
                <StatusInfoParam status={stack.status}/>
                <UserInfoParam user={stack.user}/>
                <DateInfoParam name="createdDate" value={stack.createdDate}/>
                <DateInfoParam name="notificationDate" value={stack.notificationDate}/>
                <DateInfoParam name="expiredDate" value={stack.expiredDate}/>
            </div>);
    }
}

class DateInfoParam extends Component {
    render() {
        return (
            <p><strong>{this.props.name}:</strong> <Timestamp time={this.props.value} format='ago'/></p>
        );
    }
}

class UserInfoParam extends Component {
    render() {
        return (
            <p><strong>User:</strong> {this.props.user.name} ({this.props.user.email}) </p>
        );
    }
}

class StatusInfoParam extends Component {
    render() {
        return (
            <p>
                <strong>Status:</strong> {this.props.status} <StatusIcon status={this.props.status}/>
            </p>
        );
    }
}

class Tabs extends Component {
    render() {
        return (<div>
            <ParamTab/>
            <br/>
            <LogTab/>
        </div>);
    }
}

class ParamTab extends Component {
    render() {
        return (<div>
            Hello world!
        </div>);
    }
}

class LogTab extends Component {
    render() {
        return (<div>
            Hello world2!
        </div>);
    }
}