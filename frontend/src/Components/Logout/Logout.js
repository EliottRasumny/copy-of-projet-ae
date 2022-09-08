import Navbar from "../Navbar/Navbar";
import {Redirect} from "../Router/Router";
import {removeLocalObject, removeSessionObject} from "../../utils/session";
import {STORED_TOKEN, STORED_USER} from "../../utils/const";

/**
 * Remove all stored information about the user then redirect to the homepage.
 */
const Logout = () => {
  // Clear the user session data from the localStorage and the sessionStorage so
  // the user is no longer considered to be authenticated
  removeSessionObject(STORED_TOKEN);
  removeSessionObject(STORED_USER);
  removeLocalObject(STORED_TOKEN);
  // re-render the navbar (for a non-authenticated user)
  Navbar();
  Redirect("/");
};

export default Logout;