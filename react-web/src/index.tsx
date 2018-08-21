import * as React from "react";
import * as ReactDOM from "react-dom";

import { App } from "./App";

// Create an element and append it to the body
const root = document.createElement("div");
document.body.appendChild(root);

// Render the App into this element
ReactDOM.render(<App />, root);
