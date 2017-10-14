import React, {Component} from "react";
import ReactDOM from "react-dom";

import {BrowserRouter, Route} from "react-router-dom";
import {Header} from "./header";
import {MainPage} from "./main-page";
import {RunForm} from "./run-form";
import {StackCard} from "./stack_card/stack-card";

export default class App extends Component {
    render() {
        return (<BrowserRouter>
            <div className="container">
                <Header/>
                <Route exact path='/' component={MainPage}/>
                <Route path='/template/:template_name/run' component={RunForm}/>
                <Route path='/stack/:stack_name' component={StackCard}/>
            </div>
        </BrowserRouter>);
    }
}
