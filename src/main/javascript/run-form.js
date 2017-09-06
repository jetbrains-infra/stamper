import React, {Component} from 'react';

export class RunForm extends Component {

    loadTemplateFromServer() {
        const self = this;
        $.ajax({
            type: "GET",
            url: `/api/templates/${match.params.template_name}`,
            cache: false
        }).then(function (data) {
            self.setState({template: data});
        });
    }

    render() {
        const name = this.props.match.params.template_name;
        return (
            <div className="masthead">
                <h1>Hi all from ${name}</h1>
            </div>
        );
    }
}
