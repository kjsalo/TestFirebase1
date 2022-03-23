# TestFirebase1 - Andoid Application for Scheduling Meetings

## Requirements

This application was developed against the following requirements:

* A view to select the desired participants.
* Ability to view participants’ current schedule (for example in a modal or a separate view).
* A view to create a new meeting based on times chosen by your recommendation algorithm.
* Ability to save meeting to devices calendar on creation.

## Application Architecture

Application follows common MVVM architecture pattern (see figure below)

<img src="https://user-images.githubusercontent.com/725242/159723966-207fd80e-fc57-4965-aa29-c4f1b382935f.png" width = "300">


## Application Implementation

Views are implemented using one Activity, three Fragments and respective resources files.
Communication from fragments to Activity is implemented using common interface.

There are two ViewModels, one implementing the business logic of creating a new meeting, 
and another implementing the business logic related viewing participant's meetings.
Communication between ViewModels and Views is implemented using Observer pattern and LiveData objects

There is one Repository for organizing the access to data. Data itself is stored in Firestore (Cloud Firestore is a NoSQL document database).
There are four collections in Firestore: Rooms, Meetings, Users and Counter. Counter is to used to implement meeting id automation.

<img src="https://user-images.githubusercontent.com/725242/159728183-34e4edfc-1149-4ccf-bd9e-a2aff0c940fa.png" height = "300">

Communication between ViewModels and Repository, and between Repository and Firestore is implemented using Kotlin coroutines and suspended functions.

Application implements also four models as Kotlin data classes: User, Room, Meeting and CalendarEvent

<img src="https://user-images.githubusercontent.com/725242/159729519-173aa366-0f4d-4e06-9005-89cd77d4782b.png" width = "200">

## Application Limitations

* Firebase Project is in Test Mode limited time (until early April)
* Application itself is targeted only for managing meetings. Thus managing users or rooms is done in Firebase.
* Finding a new meeting time is limited for five consecutiven weekdays, and only between 8:00 - 17:00. Also the meeting length is limited to full hours.

## Some Screen Captures
<img src="https://user-images.githubusercontent.com/725242/159733797-8961cf77-d4af-4007-a09f-5c4191c3a27a.png" width = "200">

<img src="https://user-images.githubusercontent.com/725242/159733816-ea28f260-ff7d-417d-9900-19ef8adddfcb.png)

<img src="https://user-images.githubusercontent.com/725242/159733834-f91db6d4-5908-4a9e-9b70-9a69a2d51625.png)

<img src="https://user-images.githubusercontent.com/725242/159733842-7117ccfe-e884-4804-9022-c054a64cbc7b.png)

<img src="https://user-images.githubusercontent.com/725242/159733853-3aef62ca-5e70-4f4f-87f8-6dfc81215c2c.png)

<img src="https://user-images.githubusercontent.com/725242/159733871-c223824a-21c3-4e8d-8a14-34c11fd90669.png)

<img src="https://user-images.githubusercontent.com/725242/159733881-d63ecc7f-a67f-4f81-92d8-1823dd0b41d1.png)

<img src="https://user-images.githubusercontent.com/725242/159733891-1be7563b-c172-4a9d-9a87-3d1fd425281c.png)





