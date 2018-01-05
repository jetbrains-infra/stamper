import React, {Component} from "react";
import Timestamp from "react-timestamp";
import {StatusIcon} from "../common";

export class MainInfo extends Component {
    render() {
        const stack = this.props.stack;
        if (Object.keys(stack).length === 0) {
            return (<div/>);
        }
        return (
            <div>
                <h1 id="stack-name-title">Stack: {stack.name}</h1>
                <StatusInfoParam status={stack.status}/>
                <UserInfoParam user={stack.user}/>
                <DateInfoParam name="createdDate" value={stack.createdDate}/>
                <DateInfoParam name="notificationDate" value={stack.notificationDate}/>
                <DateInfoParam name="expiredDate" value={stack.expiredDate}/>
            </div>);
    }
}


const DateInfoParam = (props) => (
    <p id={props.name}><strong>{props.name}:</strong> <Timestamp time={props.value} format='ago'/></p>
);

const UserInfoParam = (props) => (
    <p id="user-info">
        <strong>User:</strong> <span>{props.user.name}</span>({props.user.email})
    </p>
);

const StatusInfoParam = (props) => (
    <p id="stack-status">
        <strong>Status:</strong> <span>{props.status}</span><StatusIcon status={props.status}/>
    </p>
);
