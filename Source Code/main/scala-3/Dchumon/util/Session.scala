package Dchumon.util

import Dchumon.model.UserProfile

object Session {
  // Is the user authenticated?
  var isAuthenticated: Boolean = false

  // Store user profile data for the logged-in user
  var currentUser: Option[UserProfile] = None

  // Clear session when the user logs out
  def clear() = {
    isAuthenticated = false
    currentUser = None
  }
}