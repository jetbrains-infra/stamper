import React from "react";
import {Tab, TabList, TabPanel, Tabs} from 'react-tabs';
import 'react-tabs/style/react-tabs.css';

export const StackTabs = (props) => (
    <div>
        <Tabs>
            <TabList>
                <Tab>Detailed Info</Tab>
                <Tab>Logs</Tab>
            </TabList>

            <TabPanel>
                <InfoTab params={props.stack.params}/>
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
    return (<div>
        {logs}
    </div>);
};

const LogElement = (props) => {
    const log = props.log;
    return (
        <div>
            <h3>{log.title}</h3>
            <pre>{log["executeResult"]["output"]}</pre>
        </div>);
};

const InfoTab = (props) => {
    if (props.params === undefined) {
        props = {};
        props.params = {};
    }
    const render_params = Object.keys(props.params).map(key =>
        <p key={key}>{key}: {props.params[key]}</p>
    );
    return (
        <div>
            {render_params}
        </div>
    );
};
