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
                <LogTab stack={props.stack}/>
            </TabPanel>
        </Tabs>
    </div>
);


const LogTab = (props) => (
    <div>Hello log</div>
);

const InfoTab = (props) => {
    if (props.params === undefined) {
        props = {};
        props.params = {};
    }
    const render_params = Object.keys(props.params).map(key =>
        <p>{key}: {props.params[key]}</p>
    );
    return (
        <div>
            {render_params}
        </div>
    );
};
