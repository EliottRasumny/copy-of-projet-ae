import {STORED_USER} from "./const";

/**
 * Set the object (token) in the localStorage under the storedToken key
 * @param {string} storedToken the key of the stored object
 * @param {Object} object the token of the user
 */
const setLocalStorage = (storedToken, object) => {
  const storageValue = JSON.stringify(object);
  localStorage.setItem(storedToken, storageValue);
};

/**
 * Get the object (token) that is in the localStorage under the storedToken key
 * @param {string} storedToken the key of the stored object
 * @returns the token, null if there is none
 */
const getLocalObject = (storedToken) => {
  const retrievedObject = localStorage.getItem(storedToken);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject);
};

/**
 * Remove the objects (token) in the sessionStorage under the storedToken and
 * storedName keys
 * @param {String} storedObject the key of the stored object
 */
const removeLocalObject = (storedObject) => {
  localStorage.removeItem(storedObject);
};

/**
 * Set the object (username) in the sessionStorage under the storedName key
 * @param {string} storedName the key of the stored object
 * @param {Object} object the username of the connected user
 */
const setSessionObject = (storedName, object) => {
  const storageValue = JSON.stringify(object);
  sessionStorage.setItem(storedName, storageValue);
};

/**
 * Get the object (username) that is in the sessionObject under the storedName
 * key
 * @param {string} storedName the key of the stored object
 * @returns the username of the connected user, null if there is none.
 */
const getSessionObject = (storedName) => {
  const retrievedObject = sessionStorage.getItem(storedName);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject);
};

/**
 * Remove the objects (token and username) in the
 * sessionStorage under the storedToken and storedName keys
 * @param {String} storedObject the key of the stored object
 */
const removeSessionObject = (storedObject) => {
  sessionStorage.removeItem(storedObject);
};

/**
 * Take the object that contains user's information and return its username
 * @returns {*} a string of the username if exists, null otherwise
 */
const getUserName = () => {
  const retrievedObject = sessionStorage.getItem(STORED_USER);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject).username;
}

/**
 * Take the object that contains user's information and return its role
 * @returns {*} a string of the role if exists, null otherwise
 */
const getUserRole = () => {
  const retrievedObject = sessionStorage.getItem(STORED_USER);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject).role;
}

/**
 * Take the object that contains user's information and return its id
 * @returns {*} a string of the id if exists, null otherwise
 */
const getUserAddressId = () => {
  const retrievedObject = sessionStorage.getItem(STORED_USER);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject).address.idAddress;
}

/**
 * Take the object that contains user's information and return its address's id
 * @returns {*} a string of the role if exists, null otherwise
 */
const getUserId = () => {
  const retrievedObject = sessionStorage.getItem(STORED_USER);
  if (!retrievedObject) {
    return;
  }
  return JSON.parse(retrievedObject).idUser;
}

export {
  setLocalStorage,
  getLocalObject,
  removeLocalObject,
  setSessionObject,
  getSessionObject,
  removeSessionObject,
  getUserName,
  getUserRole,
  getUserId,
  getUserAddressId,
};