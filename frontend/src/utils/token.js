import {getLocalObject, setSessionObject} from "./session";
import {STORED_TOKEN, STORED_USER} from "./const";

/**
 * Check if the user has a valid Token in the local storage. It is the place
 * where we store the token if the user want to stay connected.
 * If there is none, do nothing. The user won't be authenticated.
 * If there is one, makes a request to know if it is valid or not.
 * If it is valid, register the username that was sent back in the session
 * storage. If it is not valid, do nothing. The user won't be authenticated. A
 * user is considered to be authenticated when he has a token in the session
 * storage.
 *
 * @returns {Promise<boolean>}
 */
export async function checkToken() {
  // Retrieve the token from the local storage.
  let tokenObject = getLocalObject(STORED_TOKEN);
  if (tokenObject !== undefined) {
    try {
      const options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": tokenObject,
        },
      };
      const response = await fetch(`/api/auths/token`, options);
      const user = await response.json();
      if (user) {
        // save the user and its token into the sessionStorage. Token will be
        // needed for further authentication while attempting to make an action,
        // and the user's data are useful to display them for him.
        setSessionObject(STORED_USER, user);
        setSessionObject(STORED_TOKEN, tokenObject);
      }
    } catch (error) {
      console.error("Retrieve token::error: ", error);
    }
  }
}