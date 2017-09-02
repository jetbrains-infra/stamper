const Template = React.createClass({
    render: function () {
        return (
            <li className="list-group-item">
                <span>{this.props.template.name}</span>
                <button name="action" className="btn btn-xs btn-success pull-right" value="run"> Run</button>
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

const App = React.createClass({
    loadTemplatesFormServer: function () {
        const self = this;
        $.ajax({
            type: "GET",
            url: "/api/templates",
            cache: false
        }).then(function (data) {
            self.setState({templates: data});
        });
    },

    getInitialState: function () {
        return {templates: []};
    },

    componentDidMount: function () {
        this.loadTemplatesFormServer();
    },

    render() {
        return ( <div className="container">
            <TemplateList templates={this.state.templates}/>
        </div> );
    }
});


ReactDOM.render(<App/>, document.getElementById('root'));
