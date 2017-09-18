import React, {Component} from "react";
import $ from 'jquery'
import jQuery from 'jquery'

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
                <InputParams params={this.state.template.params}/>
                <button id="run-script-btn" className="btn btn-primary">Run stack</button>
            </div>
        );
    }
}

class InputParams extends Component {
    render() {
        const rows = this.props.params.map(param => (<InputParam param={param} key={param.name}/>));
        return (
            <div>
                <h2>Run params:</h2>
                <form className="form-horizontal">
                    {rows}
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
                   defaultValue={param.defaultValue}
                   name={param.name}/>
        );
    }
}

class SelectListInput extends Component {
    render() {
        const param = this.props.param;
        const options = param.availableValues.map(item => (<option value={item} key={item}>{item}</option>));
        return (
            <select className="form-control" name={param.name} defaultValue={param.defaultValue}
                    data-live-search="true">
                {options}
            </select>
        );
    }
}