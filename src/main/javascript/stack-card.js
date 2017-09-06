import React, {Component} from "react";

export class StackCard extends Component {
    constructor(props) {
        super(props);
        this.state = {stack: {}};
    }

    loadStackFromServer() {
        const self = this;
        $.ajax({
            type: "GET", url: `/api/stack/${this.props.match.params.stack_name}`, cache: false
        }).then(function (data) {
            self.setState({stack: data});
        });
    }

    componentDidMount() {
        this.loadStackFromServer();
    }

    render() {
        return (
            <div>
                <h1>Hi!</h1>
            </div>
        );
    }
}