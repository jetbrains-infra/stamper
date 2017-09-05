import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Header} from './header';
import {MainPage} from './main-page';

import {BrowserRouter, Route} from './temp/react-router-dom';


class Temp extends Component {
    render() {
        return (
            <h1>Hi!</h1>
        );
    }
}


class App extends Component {
    render() {
        return ( <div className="container">
            <Header/>
            <Route exact path='/react/' component={MainPage}/>
            <Route exact path='/react/temp/' component={Temp}/>

        </div> );
    }
}


ReactDOM.render(<BrowserRouter>
    <App/>
</BrowserRouter>, document.getElementById('root'));
