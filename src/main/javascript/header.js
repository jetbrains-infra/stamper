import React, {Component} from 'react';

export class Header extends Component {
    render() {
        return (
            <div className="masthead">
                <ul className="nav nav-pills pull-right">
                </ul>
                <h1><a href="/react" className="muted">Stamper</a></h1>
            </div>
        );
    }
}
