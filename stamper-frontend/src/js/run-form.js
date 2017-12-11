import React, {Component} from "react";
import {Alert} from "react-bootstrap";
import {Redirect} from "react-router-dom";

export class RunForm extends Component {
    constructor(props) {
        super(props);
        this.state = {template: {params: []}};
    }

    loadTemplateFromServer() {
        fetch(`/api/templates/${this.props.match.params.template_name}`,
            {method: "get", credentials: "same-origin"}).then(result => result.json())
            .then(data => this.setState({template: data}));
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
        this.state = {params: {}};
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.getParams = this.getParams.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState((prevState) => {
            const newState = prevState;
            newState.params[name] = value;
            return newState;
        });
    }

    getParams() {
        if (this.props.params.length === 0) {
            return [];
        }
        const a = Object.assign(...this.props.params.map(d => ({[d.name]: d})));
        for (let key in this.state.params) {
            a[key].value = this.state.params[key];
        }
        return a;
    }

    handleSubmit(e) {
        e.preventDefault();
        this.setState({msgError: null});
        const params = this.getParams();
        const object = {};
        Object.keys(params).forEach(key => object[key] = params[key].value);
        const formData = new FormData();
        for (let key in object) {
            formData.append(key, object[key]);
        }
        fetch(`/api/template/${this.templateName}`, {method: "post", credentials: "same-origin", body: formData})
            .then((response) => {
                if (response.status === 403) {
                    response.json().then((data) => this.setState({msgError: data["msg"]}));
                } else {
                    this.setState({goToStackCard: true, runStack: object.name});
                }
            });
    }

    render() {
        const redirect = this.state.goToStackCard;

        if (redirect) {
            return <Redirect to={`/stack/${this.state.runStack}`}/>;
        }
        let alert = <div/>;
        if (this.state.msgError) {
            alert = <Alert bsStyle="danger">
                <strong>Error!</strong> {this.state.msgError}
            </Alert>;
        }
        const rows = Object.values(this.getParams()).map(param => (<InputParam param={param} key={param.name}/>));
        return (
            <div>
                {alert}
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
                <label className="col-xs-2 control-label">
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