import React, {Component} from "react";
import ReactDOM from "react-dom";
import {Header} from "./header";
import {MainPage} from "./main-page";
import {RunForm} from "./run-form";

import {BrowserRouter, Route} from "./temp/react-router-dom";

class App extends Component {
    render() {
        return ( <div className="container">
            <Header/>
            <Route exact path='/react/' component={MainPage}/>
            <Route path='/react/template/:template_name/run' component={RunForm}/>

        </div> );
    }
}

ReactDOM.render(<BrowserRouter>
    <App/>
</BrowserRouter>, document.getElementById("root"));
