# GBCContactTracing

This project is protoype android application for COVID-19 contact tracing in Gettysburg College and a part of CS440 course in Gettysburg College. 
\

**API level: 26 \
Compile SDK version: 30**

The application implements Bluetooth Low Energy (BLE) technology to broadcast college email address (which should be verified in intro-screens) as token. This token is exchanged between devices in the background while also keeping track of 
**interaction time** and **interaction device**. Through this, the application obtains the following information after post-processing of the raw-data and stores it in the **SQLite Database**.


| Information      | Description |
| ----------- | ----------- |
| token      | College email. |
| startTime   | First time the device was seen. |
| endTime   | End time the device was seen. |
| avgDistance   | Average distance over the interaction. |
\
The SQLite database has one table that captures this exact information. Following is the specification: \
\
Database name: **TracedContacts**
Table Name: **Contacts**
\
| id | starTime | endTime| avgDistance
| ----------- | ----------- | ----------- |  ----------- |
| example@email.com |  1607125639912 |  1607125739912 | 3 |

The application also store a user-preference as **user_id** under the name **GBContact Tracing**.

We have the following the following parameters in the following files to tune the way application works:

File : **gbcontacttracing.bluetoothManager**

| Field      | Description |
| ----------- | ----------- |
| MIN_EXPOSURE_TIME      | Minimum time for an interaction to be considered as sufficient exposure. |
| DISAPPEAR_TIME   | Minimum time of dissappearnce of a device to be counted as end of interaction. |
| MAX_TIME_DIFF   | Time for which if the difference of endTime and current time is greater than this then the exposure record is deleted from the database. |
| MIN_EXPOSURE_DISTANCE   | Minimum distance for an interaction to be considered as sufficient exposure* |

\
*It is important to note that even if the average distance at the end of interaction is greater than **MIN_EXPOSURE_DISTANCE**, if there's any situation where average interaction distance is less than **MIN_EXPOSURE_DISTANCE** after **MIN_EXPOSURE_TIME** has passed, then the interaction will still be counted as an exposure.

The application also has a server but it merely accepts post request from the application in order to send the respective infromation. The server has the following end points:

| End Points   | File  | Description |
| ----------- | -----------|----------- |
| verificationServerEndPoint | LoginActivity.kt | php endpoint that accepts post request to send a verification email.* |
| releaseServerEndpoint | ReleaseFragment.kt | php endpoint that accepts post request to send data to authority.* |

