import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "./stylesheets/style.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import Navbar from "./Components/Navbar/Navbar";
import {Router} from "./Components/Router/Router";
import {checkToken} from "./utils/token";

// Check if the user is still authenticated and then load the navbar
(checkToken)().then(Navbar);
Router(); // The router will automatically load the root page
