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
Communication between ViewModels and Repository, and between Repository and Firestore is implemented using Kotlin coroutines and suspended functions.

Application implements four models as Kotlin data classes: User, Room, Meeting and CalendarEvent




## Application Limitations



## Some Screen Captures

