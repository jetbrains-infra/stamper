import React, {Component} from "react";
import {Link} from "react-router-dom";

export class Header extends Component {
    render() {
        return (
            <div className="masthead">
                <UserForm/>
                <h1><Link to={"/"}>Stamper</Link></h1>
            </div>
        );
    }
}

class UserForm extends Component {
    constructor(...args) {
        super(...args);
        this.state = {name: null, serverAddress: ""};
        this.getServerAddress = this.getServerAddress.bind(this);
        this.serverAddress = "";
    }


    getServerAddress() {
        fetch("/api/serveraddress", {credentials: "same-origin"})
            .then(res => res.text())
            .then(text => {
                this.setState({serverAddress: text});
            });
    }

    componentWillUnmount() {
        this.loadInterval && clearInterval(this.loadInterval);
        this.loadInterval = false;
    }


    componentDidMount() {
        this.getServerAddress(this);
        this.loadInterval = this.updateUser();
        setInterval(() => this.updateUser(), 5000);
    }

    updateUser() {
        fetch("/api/user", {credentials: "same-origin"})
            .then(res => res.text())
            .then(text => {
                if (text === "") {
                    this.setState({name: ""});
                    return;
                }
                const json = JSON.parse(text);
                if (json !== null) {
                    this.setState({name: json.name});
                }
            });
    }

    logout() {
        console.log("logout");
        fetch("/logout", {method: "post", credentials: "same-origin"})
            .then(() => this.setState({name: null}));
    }

    render() {
        const profile = this.state.name ?
            <LogoutComponent name={this.state.name} serverAddress={this.state.serverAddress}
                             logout={() => this.logout()}/> :
            <LoginForm serverAddress={this.state.serverAddress}/>;
        return (
            <ul className="nav nav-pills pull-right">
                {profile}
            </ul>
        );
    }
}

const LogoutComponent = (props) => (
    <li>
        <form className="form-inline" method="post" action={`${props.serverAddress}/logout`}>
            <b>User:</b>
            <span>{props.name}</span>
            <button className="btn btn-sm button-right btn-success">Logout</button>
        </form>
    </li>
);

const LoginForm = (props) => (
    <li>
        <form action={`${props.serverAddress}/login`} method="get">
            <input className="btn btn-sm btn-success " type="submit" value="Login"/>
        </form>
    </li>
);