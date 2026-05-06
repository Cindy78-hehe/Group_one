## Project identity
- App name :NduUpdates
- Youtubelink:https://youtu.be/DSuVSIbDVZw

# Team Roster
|Name  |students ids| roles|
|---------|------|-------|
|PENGERE DAVID ISRAEL|24/1/306/D/674 |LEAD DEVELOPER|
|OCHOLA PETER|24/1/306/D/682 |Testing and Quality Assurance Engineer AND UI/UX Specialist.|
|NAKATO CINDY PEACE|25/1/314/D/2978 |Git and Quality Manager.|
|KIZITO ARNOLD KIM|24/1/3I4/D/476 |Documentation and Research Lead AND UI/UX Specialist.|

## Feature Set: NduUpdates
# 1.Intelligent User Authentication
- Role-Based Access Control: Automatic identification of users as Admin, Lecturer, or Student based on university email suffixes.
- Secure Onboarding: Streamlined registration and login process with persistent session management.
# 2.Multi-Channel Notice System
- Categorized Updates: Support for distinct communication types including General Notices, Academic News, and Campus Events.
- Targeted Audience: Ability for staff to broadcast messages to specific groups (e.g., "Students Only" or "Staff Only").
- Rich Media Support: Integration of image and file attachments to provide comprehensive information.
# 3.Real-Time Interactive Feed
- Instant Updates: A dynamic dashboard that prioritizes recent and featured university announcements.
- Engagement Tools: A commenting system that allows students to seek clarifications directly on notice posts.
- Content Management: Full CRUD (Create, Read, Update, Delete) capabilities for authorized personnel to manage their posts.
# 4.Personalized User Experience
- Profile Customization: Users can manage their identity, update display names, and upload profile pictures via an integrated photo picker.
- Branded UI/UX: A custom-themed interface using Material 3, featuring professional typography (Playfair Display) tailored for an academic environment.
# 5.Offline Reliability
- Local Caching: Powered by a Room Database, ensuring that previously loaded notices are accessible even when students are in low-connectivity areas on campus.
- Resilient State: UI state persistence that prevents data loss during multitasking or screen transitions.

 ## Techhnical stack 
# 1. UI & Modern Design
- Jetpack Compose: The modern toolkit for building native Android UI.
- Material Design 3 (M3): The latest evolution of Material Design, used for our components, color surfacing, and theming.
- Material Icons Extended: Provides a comprehensive set of official Material icons (like the Edit and Person icons used in profiles).
# 2. Image Loading
- Coil (Compose): An image loading library for Android backed by Kotlin Coroutines.
- We use this to asynchronously load and cache profile pictures and notice images without lagging the UI.
# 3. Navigation
- Jetpack Navigation (Compose): Handles the screen flow between Login, Registration, and the various Dashboards while managing the backstack.
# 4.Data Persistence (Local Database)
- Room Database: A persistence library that provides an abstraction layer over SQLite.
- Room KTX: Adds Kotlin Coroutine support for database queries, allowing for non-blocking database operations.
- KSP (Kotlin Symbol Processing): Used to generate the necessary code for Room at compile-time for better performance.
# 5.Architecture & Lifecycle
- ViewModel (Compose): Used to store and manage UI-related data in a lifecycle-conscious way.
- Lifecycle Runtime KTX: Provides lifecycle-aware coroutine scopes, ensuring tasks (like fetching notices) are cancelled when the user leaves the screen.
- StateFlow: Used for reactive data streams from the database to the UI.
# 6.Utilities & Foundations
- Core KTX: Provides Kotlin extensions for common Android framework APIs.
- Activity Compose: Bridges the gap between traditional Android Activities and Jetpack Compose.


## Test Cases & Quality Assurance
|Test ID|Module|Test Scenario|Test Steps|Expected Result|User Role|Results |
|-------|------|-------------|----------|---------------|----------|------|
|TC-01|App Launch|Launch the application|Open the app|Welcome screen displays with Login and Register buttons|All|pass|
|TC-02|Registration|Create new user account|"1. Click Register 2. Fill Full Name, Email, Password,Confirm Password 3. Click Create Account|Account created successfully and user can login|New User|pass|
|TC-03|Login|Login with valid credentials|1. Enter Email and Password 2. Click Login|User redirected to correct dashboard based on  role (Admin / Student / Lecturer)|All|pass|
|TC-04|Admin Dashboard|Access Admin Home|Login as Admin|"Admin home screen shows ""Manage university updates| news and events from here"""|Admin|pass|
|TC-05|Admin - Create Post|Create new post with audience|1. Go to Posts → Click +  2. Enter Title, Type,Audience, Attachment 3.Post |Post created successfully and visible only to selected audience|Admin|pass|
|TC-06|Role-based Visibility|Student views posts|Login as Student → Navigate to Notice/News/Events|Student sees only posts targeted to Students|Student|pass|
|TC-07|Role-based Visibility|Lecturer views posts|Login as Lecturer → Navigate to Notice/News/Events|Lecturer sees only posts targeted to Lecturers|Lecturer|pass|
|TC-08|Commenting|Add comment on a post|1.Open any post 2.Click Comment button 3.Type comment and send|Comment posted successfully and visible to all users|All|pass|
|TC-09|Profile Management|Update profile picture and name|1.Click profile icon (top left) 2.Change photo & name 3.Save changes"|Profile updated successfully (Email remains unchanged)|All|pass|
|TC-10|Logout|Logout from the app|Go to Profile → Click Logout|User successfully logged out and redirected to Welcome screen|All|pass|
|TC-11|Theme Switching|Switch between Light and Dark mode|Toggle theme from settings or profile|App changes between Light (White) and Dark Purple mode|All|pass|
|TC-12|Navigation - Admin|Test Admin bottom navigation|Login as Admin → Click Posts, Home, News, Events|All navigation buttons work correctly|Admin|pass|
|TC-13|Navigation - User|Test Student/Lecturer navigation|Login as Student/Lecturer → Click Notice, News, Events|All navigation buttons work correctly|Student,Lecturer|pass|
|TC-14|Delete Post|Admin deletes a post|Login as Admin → Open post → Delete|Post is deleted and no longer visible to users|Admin|pass|

  
  
<!--PROJECT NDU UPDATE  
  ##Testing and Quality Assurance Engineer
##click on the app icon which takes you to the welcome screen with login amd register buttons
when you don't have an account ,you click login which takes you to login screen with 
textarea to put in the eamil and password ,the one you registered when creating an acount.
if you don't have an account click on link 'i dont have an acount' which take you to the register screen to .
create an account with textarea like fullname,eamil address,password and confirm password.
once all is inserted you can create the account.
# when you login as an admin 
you go to the admin home screen with  message'manage university updates ,news and events from here'.
#below is the navigation bar with posts,home,news,events
#click on the posts button takes the screen with a plus sign.
the plus sign when you click on it bring options like title,type which enables u to choose which message you to send.
audience dropdown menu to select frompeople you want to send the message.
attachments option where images and files can be attached for posting.
#further more when u just want to send a specific message you can either brower to news or events buttons
when amessage is sent to audience as students only students can see and vice versa.
can delete the messages posted


## when you login as lecture or student
Get to the home screen with you see a message'welcome back! stay informed with the lastest information from ndejje university'
Below is the navigation bar with buttons notice,news and events
click notice one can get the notice posted by the admin and comments through clicking a comment button to comment 
this commit can be see by all the users and it applys to all the buttons.
here both parties the students and lecture can comment on the posted messages.

##click on the lefttop corners profice icon 
this icon take you to the profile area to make some changes like
adding profile image and edit their names but not they emails then later click on save changes
you can cancel and also logout from here.
so this cut across the all users 

##theme we have both lightmode which is white and darkmode is dark purple
##state was managed well 
when you are typing and the screen changes orientation.
you continue with what you are typing this means that state of the typed words was retain
-->

