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
        this.state = {name: null};
    }

    componentDidMount() {
        this.updateUser();
        setInterval(() => this.updateUser(), 5000);
    }

    updateUser() {
        fetch("/api/user", {credentials: "same-origin"})
            .then(res => res.json())
            .then(json => {
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
            <LogoutComponent name={this.state.name} logout={() => this.logout()}/> :
            <LoginForm/>;
        return (
            <ul className="nav nav-pills pull-right">
                {profile}
            </ul>
        );

    }
}

const LogoutComponent = (props) => (
    <li>
        <form className="form-inline" method="post" action="http://localhost:8080/logout">
            <b>User:</b>
            <span>{props.name}</span>
            <button className="btn btn-sm button-right btn-success">Logout</button>
        </form>
    </li>
);

const LoginForm = () => (
    <li>
        <form action="http://localhost:8080/login" method="get">
            <input className="btn btn-sm btn-success " type="submit" value="Login"/>
        </form>
    </li>
);