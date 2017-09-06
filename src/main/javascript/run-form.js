import React, {Component} from 'react';

export class RunForm extends Component {
    constructor() {
        super();
        this.state = {template: {params: {}}};
    }

    loadTemplateFromServer() {
        const self = this;
        $.ajax({
            type: "GET",
            url: `/api/templates/${this.props.match.params.template_name}`,
            cache: false
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
        return (
            <h1>Hi all {this.props.params.toString()}</h1>
        )
    }
}