import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Header} from './header';

class Template extends Component {
    static runStack() {
        alert('hello!');
    }

    render() {
        return (
            <li className="list-group-item">
                <span>{this.props.template.name}</span>
                <button className="btn btn-xs btn-success pull-right" onClick={Template.runStack}>Run</button>
            </li>
        );
    }
}

class TemplateList extends Component {
    render() {
        const rows = [];
        this.props.templates.forEach(function (template) {
            rows.push(<Template template={template} key={template.name}/>);
        });
        return (
            <div>
                <h2>Available Templates:</h2>
                <ul className="list-group">
                    {rows}
                </ul>
            </div>);
    }
}

class StackList extends Component {
    render() {
        const rows = [];
        this.props.stacks.forEach(function (stack) {
            rows.push(<Stack stack={stack} key={stack.name}/>);
        });
        return (
            <div>
                <h2>Running Stacks:</h2>
                <ul className="list-group">
                    {rows}
                </ul>
            </div>);
    }
}

class Stack extends Component {
    static destroy() {
        alert('destroy!');
    }

    render() {
        return (
            <li className="list-group-item">
                <span id="icon" className="fa fa-circle-o-notch fa-spin"/>
                <a href="/script/{this.props.template.name}">{this.props.stack.name}</a>
                <button className="btn btn-xs btn-danger pull-right" onClick={Stack.destroy}>Destroy</button>
            </li>
        );
    }
}

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {templates: [], stacks: []};
    }

    loadTemplatesFromServer() {
        const self = this;
        $.ajax({
            type: "GET",
            url: "/api/templates",
            cache: false
        }).then(function (data) {
            self.setState({templates: data});
        });
    }

    loadStacksFromServer() {
        const self = this;
        $.ajax({
            type: "GET",
            url: "/api/stacks",
            cache: false
        }).then(function (data) {
            self.setState({stacks: data});
        });
    }

    componentDidMount() {
        this.loadTemplatesFromServer();
        this.loadStacksFromServer();
    }

    render() {
        return ( <div className="container">
            <Header/>
            <TemplateList templates={this.state.templates}/>
            <br/>
            <StackList stacks={this.state.stacks}/>
        </div> );
    }
}


ReactDOM.render(<App/>, document.getElementById('root'));
