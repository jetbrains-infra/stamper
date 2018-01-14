import React from "react";
import {Panel} from "react-bootstrap";
import {Tab, TabList, TabPanel, Tabs} from "react-tabs";
import "react-tabs/style/react-tabs.css";

export const StackTabs = (props) => (
    <div>
        <Tabs>
            <TabList>
                <Tab>Detailed Info</Tab>
                <Tab>Stack State Info</Tab>
                <Tab>Logs</Tab>
            </TabList>

            <TabPanel>
                <InfoTab params={props.stack.params}/>
            </TabPanel>
            <TabPanel>
                <StateInfoTab stateInfo={props.stateInfo}/>
            </TabPanel>
            <TabPanel>
                <LogTab logs={props.logs}/>
            </TabPanel>
        </Tabs>
    </div>
);


const LogTab = (props) => {
    const logs = Object.keys(props.logs).map(key =>
        <LogElement key={key} log={props.logs[key]}/>
    );

    return (<div id='logs-tab'>
        {logs.reverse()}
    </div>);
};

const LogElement = (props) => {
    const log = props.log;
    return (
        <Panel collapsible header={log.title}>
            <pre>{log["executeResult"]["output"]}</pre>
        </Panel>);
};

const InfoTab = (props) => {
    if (props.params === undefined || Object.keys(props.params).length === 0) {
        return <p>There is not information about stack yet</p>;
    }
    const render_params = Object.keys(props.params).map(key =>
        <p id={key} key={key}><span>{key}</span>: <span>{props.params[key]}</span></p>
    );
    return (
        <div id='detailed-info-tab'>
            {render_params}
        </div>
    );
};

const StateInfoTab = (props) => {
    const state = props.stateInfo;
    return (
        <pre id='state-info-tab'>
            {state}
        </pre>
    );
};

