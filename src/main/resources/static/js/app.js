const Employee = React.createClass({
    render: function () {
        return (<div>employee</div>);
    }
});
const EmployeeTable = React.createClass({
    render: function () {
        return (<div>employee table</div>);
    }
});

ReactDOM.render(
    <EmployeeTable />, document.getElementById('root')
);