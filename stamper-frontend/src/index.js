import "bootstrap/dist/css/bootstrap.min.css";
import "font-awesome/css/font-awesome.min.css";
import React from "react";
import ReactDOM from "react-dom";
import "./css/index.css";
import App from "./js/main";
import registerServiceWorker from "./registerServiceWorker";

ReactDOM.render(<App/>, document.getElementById("root"));
registerServiceWorker();