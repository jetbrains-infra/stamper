import React, {Component} from "react";
import $ from 'jquery'
import {Redirect} from "react-router-dom";

export class RunForm extends Component {
    constructor() {
        super();
        this.state = {template: {params: []}};
    }

    loadTemplateFromServer() {
        const self = this;

        $.ajax({
            type: "GET", url: `/api/templates/${this.props.match.params.template_name}`, cache: false
        }).then(function (data) {
            self.setState({template: data});
        });
    }

    componentDidMount() {
        this.loadTemplateFromServer();
    }


    render() {
        const name = this.props.match.params.template_name;
        return (
            <div>
                <h1>Run configuration of {name} template</h1>
                <InputParams params={this.state.template.params} templateName={name}/>

            </div>
        );
    }
}

class InputParams extends Component {
    constructor(props) {
        super(props);
        this.templateName = props.templateName;
        this.state = {};
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.getParams = this.getParams.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    getParams() {
        if (this.props.params.length === 0) {
            return [];
        }
        const a = Object.assign(...this.props.params.map(d => ({[d.name]: d})));
        const b = Object.values(this.state);
        for (let key in this.state) {
            const value = this.state[key];
            a[key].value = value;
        }
        return a;
    }

    handleSubmit(e) {
        e.preventDefault();
        const params = this.getParams();
        const object = {};
        Object.keys(params).forEach(key => object[key] = params[key].value);
        const formData = new FormData();
        for (let key in object) {
            formData.append(key, object[key]);
        }
        fetch(`/api/template/${this.templateName}`, {method: 'post', credentials: 'same-origin', body: formData})
            .then(result => this.setState({goToStackCard: true, runStack: object.name}));
    }

    render() {
        const redirect = this.state.goToStackCard;

        if (redirect) {
            return <Redirect to={`/stack/${this.state.runStack}`}/>;
        }

        const rows = Object.values(this.getParams()).map(param => (<InputParam param={param} key={param.name}/>));
        return (
            <div>
                <h2>Run params:</h2>
                <form className="form-horizontal" method="POST" onChange={this.handleChange}
                      onSubmit={this.handleSubmit}>
                    {rows}
                    <button id="run-script-btn" className="btn btn-primary">Run stack
                    </button>
                </form>
            </div>);
    }


}

class InputParam extends Component {
    getInputForm() {
        const param = this.props.param;
        if (param.availableValues.length !== 0) {
            return <SelectListInput param={param}/>;
        }
        return <BasicInput param={param}/>;
    }

    render() {

        const param = this.props.param;
        return (
            <div className="form-group">
                <label htmlFor={param.name} className="col-xs-2 control-label">
                    {param.name}
                </label>
                <div className="col-xs-3">
                    {this.getInputForm()}
                </div>
            </div>);
    }
}

class BasicInput extends Component {
    render() {
        const param = this.props.param;
        return (
            <input className="form-control"
                   defaultValue={param.value}
                   name={param.name}/>
        );
    }
}

class SelectListInput extends Component {
    render() {
        const param = this.props.param;
        const options = param.availableValues.map(item => (<option value={item} key={item}>{item}</option>));
        return (
            <select className="form-control" name={param.name} defaultValue={param.value}
                    data-live-search="true">
                {options}
            </select>
        );
    }
}