# Task Master

## Overview

- The Task Master app lets you add daily tasks to your list to keep note of things to do.

## App Page Layout

### Homepage

![Task Master Homepage](screenshots/taskMasterHomepage.PNG)

### Updated Homepage

![Updated Task Master Homepage](screenshots/newtaskMasterHomepage.PNG)

### Updated Homepage 10/21

![Updated Task Master Homepage](screenshots/newNewTaskMasterHomepage.PNG)

### Updated Homepage 10/22

![Updated Task Master Homepage](screenshots/newUpdatedTaskMasterHomepage.PNG)

### Updated Hompage 10/27

![Updated Task Master Homepage](screenshots/updatedTaskMasterHomepage.PNG)

### Updated Homepage 10/29

![Updated Task Master Homepage](screenshots/taskMasterHomepage10.29.PNG)

### Updated Homepage 11/2

![Updated Task Master Homepage](screenshots/taskMasterHomepage11.2.PNG)

### Signup Page

![Task Master Signup Page](screenshots/taskMasterSignup.PNG)

### Confirmation Page

![Task Master Confirmation Page](screenshots/taskMasterConfirmation.PNG)

### Login Page

![Task Master Login Page](screenshots/taskMasterLogin.PNG)

### Add Task

![Task Master Add Task Page](screenshots/taskMasterAddTask.PNG)

### Updated Add Task 10/22

![Task Master Add Task Page](screenshots/updatedTaskMasterAddTask.PNG)

### Updated Add Task 10/29

![Task Master Add Task Page](screenshots/taskMasterAddTask10.29.PNG)

### Updated Add Task 11/3

![Task Master Add Task Page](screenshots/taskMasterAddTask11.3.PNG)

### Submit Confirmation

![Task Master Submit Page](screenshots/taskMasterSubmit.PNG)

### All Tasks

![Task Master All Tasks Page](screenshots/taskMasterAllTasks.PNG)

### Task Detail

![Task Master Task Detail](screenshots/taskMasterTaskDetail.PNG)

### Updated Task Detail 10/21

![Updated Task Master Task Detail](screenshots/updatedTaskMasterTaskDetail.PNG)

### Updated Task Detail 11/3

![Updated Task Master Task Detail](screenshots/taskMasterTaskDetail11.3.PNG)

### Updated Task Detail 11/10

![Updated Task Master Task Detail](screenshots/taskMasterTaskDetail11.10.PNG)

### User Settings

![Task Master User Settings](screenshots/taskMasterUserSettings.PNG)

### Update User Settings

![Task Master User Settings](screenshots/taskMasterUserSettings10.29.PNG)

### Confirmation Notication from Firebase

![Task Master Firebase Notification](screenshots/firebaseCloudMessaging.PNG)

### Confirmation Notication from Pinpoint

![Task Master Pinpoint Notification](screenshots/awsPinpointMessage.PNG)

### AWS Analytics Events Data

![AWS Analytics Events Data](screenshots/awsPinpointEvents.PNG)

## Daily Change Log

- 10/19/2020
  - Created the home, add, submit confirmation and all tasks pages.
- 10/20/2020
  - Updated the homepage with three more buttons that lead to the new task detail page. Also created a user settings page where the user can update their username.
  - Took out the submit confirmation page after adding a task and replaced with a toast instead.
- 10/21/2020
  - Updated the homepage with the recycler view to show a list of tasks
  - Made the list of tasks clickable
  - Created a task class
- 10/22/2020
  - Updated the Add Task page to save task data to my local database.
  - Refactored homepage RecycleView to display all tasks in my database.
  - Task detail page reflects clicked task title and description.
- 10/26/2020
  - Created Espresso tests for homepage button paths and back button functionality
  - Created Espresso tests for adding a new tasks to the recycler view on homepage
  - Created Espresso tests for adding a new username and have the new username viewable on the homepage
- 10/27/2020
  - Connected to DynamoDB using Amplify
  - Homepage list is updated from DynamoBW
  - Added tasks are now saved to DynamoDB instead of room
- 10/28/2020
  - Created team model with realtion with task model and vice versa
  - Updated add task page to include team selection and save to DynamoDB
  - Updated user settings page to inlcude team selection
  - Homepage title displays user's team selection
- 11/2/2020
  - Created classes and activities for signup, login and user confirmation
  - Added a logout button
  - Updated homepage with more buttons
  - Added Cognito user authentication plugin
- 11/3/2020
  - Connected Amplify Storage to app
  - Added the ability to add attachment to individual tasks
  - Updated detail page to show image selected of task
  - Updated task object in schema to take an additional property
- 11/4/2020
  - Connected AWS Notification Services to my app
  - Able to create and recieve notifications from the Firebase console
  - Able to create and recieve notifications from the Pinpoint console
- 11/5/2020
  - Added Amplify Analytics
  - Created Event Tracking class and methods to go along
    - trackButtonClicked()
    - eventTrackerAppStart()
  - Added methods to all necessary events within app
- 11/10/2020
  - Shared images are available to Add Task page
  - Shared images are viewed in Task Detail page
  