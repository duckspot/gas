/auth get json -> isLoggedIn, userName
/auth get html - login/register form

/auth post json email=a@bc.com&password=xxx - login
/auth post json email=a@bc.com&password=xxx[&name=xx]&action=register - register new user
/auth post json name=Fullname - works if logged in
/auth post json action=logout

/auth/settings/* post name=Full Name
/auth/* post oldpassword=&newpassword=&newpassword2=
/auth/* post oldPwsh, newPwsh
/auth/settings/* get json -> [] email, primary, verified
/auth/settings/* get html -> form for email changes, link to form for password change
/auth/password/* get html -> form for password changes
