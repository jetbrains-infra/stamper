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
                <h1>Stack: {stack.name}</h1>
                <StatusInfoParam status={stack.status}/>
                <UserInfoParam user={stack.user}/>
                <DateInfoParam name="createdDate" value={stack.createdDate}/>
                <DateInfoParam name="notificationDate" value={stack.notificationDate}/>
                <DateInfoParam name="expiredDate" value={stack.expiredDate}/>
            </div>);
    }
}


const DateInfoParam = (props) => (
    <p><strong>{props.name}:</strong> <Timestamp time={props.value} format='ago'/></p>
);

const UserInfoParam = (props) => (
    <p>
        <strong>User:</strong> {props.user.name} ({props.user.email})
    </p>
);

const StatusInfoParam = (props) => (
    <p>
        <strong>Status:</strong> {props.status} <StatusIcon status={props.status}/>
    </p>
);
