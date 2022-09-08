import {OBJECT_STATE_VALUE_EN, OBJECT_STATE_VALUE_FR} from "./const";

/**
 * Translate the state of an object from English to French.
 *
 * @param state the state to translate
 * @returns {string|null} the state in French. If there is no available translation
 * for this state it returns null.
 */
export function displayStateInFrench(state) {
  switch (state) {
    case OBJECT_STATE_VALUE_EN.DONATED:
      return OBJECT_STATE_VALUE_FR.DONATED;
    case OBJECT_STATE_VALUE_EN.ASSIGNABLE:
      return OBJECT_STATE_VALUE_FR.ASSIGNABLE;
    case OBJECT_STATE_VALUE_EN.ASSIGNED:
      return OBJECT_STATE_VALUE_FR.ASSIGNED;
    case OBJECT_STATE_VALUE_EN.GIVEN:
      return OBJECT_STATE_VALUE_FR.GIVEN;
    case OBJECT_STATE_VALUE_EN.CANCELLED:
      return OBJECT_STATE_VALUE_FR.CANCELLED;
    default:
      return null;
  }
}