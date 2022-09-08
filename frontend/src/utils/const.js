// Value below which the user data can be stored.
// This object is frozen so that none of its values can be modified.
const STORED_USER = Object.freeze("user");
// Value below which the token of the user can be stored.
// This object is frozen so that none of its values can be modified.
const STORED_TOKEN = Object.freeze("token");
// Possible values to sort offers.
// This object is frozen so that none of its values can be modified.
const SORT_OPTION = Object.freeze({
  TYPE_ASC: "type_asc",
  TYPE_DESC: "type_desc",
  DATE_ASC: "date_asc",
  DATE_DESC: "date_desc",
  INTERESTS_ASC: "interests_asc",
  INTERESTS_DESC: "interests_desc",
});
// Possible values in French for the state of an offer.
// This object is frozen so that none of its values can be modified.
const OBJECT_STATE_VALUE_FR = Object.freeze({
  DONATED: `offerte`,
  ASSIGNABLE: `attribuable`,
  ASSIGNED: `attribu&eacute;e`,
  GIVEN: `donn&eacute;e`,
  CANCELLED: `annul&eacute;e`,
});
// Possible values in English for the state of an offer.
// This object is frozen so that none of its values can be modified.
const OBJECT_STATE_VALUE_EN = Object.freeze({
  DONATED: `donated`,
  ASSIGNABLE: `assignable`,
  ASSIGNED: `assigned`,
  GIVEN: `given`,
  CANCELLED: `canceled`,
})
// Possible values in English for the state of a user.
// This object is frozen so that none of its values can be modified.
const USER_STATE_VALUE_EN = Object.freeze({
  REGISTERED: `registered`,
  VALID: `valid`,
  DENIED: `denied`,
  UNAVAILABLE: `unavailable`
});
// Possible values in French for the state of a user.
// This object is frozen so that none of its values can be modified.
const USER_STATE_VALUE_FR = Object.freeze({
  REGISTERED: `en attente`,
  VALID: `valid&eacute;`,
  DENIED: `refus&eacute;`,
  UNAVAILABLE: `indisponible`,
});
// Possible type to apply to filter the offers when sending a request to the
// server.
// This object is frozen so that none of its values can be modified.
const OBJECT_FILTER_TYPE = Object.freeze({
  BY_NAME: "research-name",
  BY_TYPE: "research-type",
  BY_STATE: "research-state",
  BY_DATE: "research-date",
  DATE_FROM: "date-from",
  DATE_TO: "date-to",
});

const MEMBER_FILTER_TYPE = Object.freeze({
  BY_LASTNAME: "research-lastname",
  BY_POSTCODE: "research-postcode",
  BY_COMMUNE: "research-commune",
});

const VERSION = "version"; // the version of the object
const USER_VERSION = "userVersion";

export {
  STORED_USER,
  STORED_TOKEN,
  SORT_OPTION,
  OBJECT_STATE_VALUE_FR,
  OBJECT_STATE_VALUE_EN,
  OBJECT_FILTER_TYPE,
  MEMBER_FILTER_TYPE,
  USER_STATE_VALUE_EN,
  USER_STATE_VALUE_FR,
  VERSION,
  USER_VERSION
};
