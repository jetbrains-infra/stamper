import React from 'react';
import ReactDOM from 'react-dom';
import './css/index.css';
import registerServiceWorker from './registerServiceWorker';
import App from "./js/main";

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();