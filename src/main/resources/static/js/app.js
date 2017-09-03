const Template = React.createClass({
    runStack: function () {
        alert('hello!');
    },

    render: function () {
        return (
            <li className="list-group-item">
                <span>{this.props.template.name}</span>
                <button className="btn btn-xs btn-success pull-right" onClick={this.runStack}>Run</button>
            </li>
        );
    }
});

const TemplateList = React.createClass({
    render: function () {
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
});

const StackList = React.createClass({
    render: function () {
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
});

const Stack = React.createClass({
    destroy: function () {
        alert('destroy!');
    },

    render: function () {
        return (
            <li className="list-group-item">
                <span id="icon" className="fa fa-circle-o-notch fa-spin"/>
                <a href="/script/{this.props.template.name}">{this.props.stack.name}</a>
                <button className="btn btn-xs btn-danger pull-right" onClick={this.destroy}>Destroy</button>
            </li>
        );
    }
});

const Menu = React.createClass({
    render: function () {
        return (
            <div className="masthead">
                <ul className="nav nav-pills pull-right">
                </ul>
                <h1><a href="/react" className="muted">Stamper</a></h1>
            </div>
        );
    }
});


const App = React.createClass({
    loadTemplatesFromServer: function () {
        const self = this;
        $.ajax({
            type: "GET",
            url: "/api/templates",
            cache: false
        }).then(function (data) {
            self.setState({templates: data});
        });
    },

    loadStacksFromServer: function () {
        const self = this;
        $.ajax({
            type: "GET",
            url: "/api/stacks",
            cache: false
        }).then(function (data) {
            self.setState({stacks: data});
        });
    },

    getInitialState: function () {
        return {templates: [], stacks: []};
    },

    componentDidMount: function () {
        this.loadTemplatesFromServer();
        this.loadStacksFromServer();
    },

    render() {
        return ( <div className="container">
            <Menu/>
            <TemplateList templates={this.state.templates}/>
            <br/>
            <StackList stacks={this.state.stacks}/>
        </div> );
    }
});


ReactDOM.render(<App/>, document.getElementById('root'));
