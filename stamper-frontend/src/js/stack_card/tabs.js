import React from "react";
export const Tabs = (props) => (
    <div>
        <ParamTab/>
        <br/>
        <LogTab/>
    </div>
);


const LogTab = (props) => (
    <div>
        Hello world2 cpnst!
    </div>
);

const ParamTab = (props) => (
    <div>
        Hello world
    </div>
);