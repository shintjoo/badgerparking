# badgerparking
Most Recent on top

Project Log:

--12/14/2023 - Fixed issues with notifications not being distinguished from each other once being trigger (Daniel)

--12/14/2023 - Added in quick notifications that can be used during the demo, they are tagged with todo since they should be removed if this app were ever to be released (Daniel)

--12/10/2023 - Map activity complete (Brian)

--12/08/2023 - Finished creating alarms to trigger notifications, but have not tested all cases yet (Daniel)

--12/08/2023 - Part way through creating alarms to trigger notifications when the app is not running (Daniel)

--12/8/2023 - Upgraded timer to adjust its start time to match the parking restrictions of the location parked at (Daniel)

--12/07/2023 - Modified the tolerances in the search for restrictions function. Still not working perfectly because sometimes it will display the information on the wrong side of the street (Shawn)

--12/06/2023 - Fixed issue where multiple timers could run at once and would fight for control of the timer display (Daniel)

--12/05/2023 - Needed to introduce Places SDK. this will allow for autocomplete of addresses and the ability to turn that into coordinates (Shawn)

--12/05/2023 - Search activity needed to use geocoder in order to take user input and convert it into coordinates (Shawn)

--12/05/2023 - Added int column with minutes per time_limit column for timer. (Brian)

--11/27/2023 - Created the layout and the initial logic for SearchActivity. Currently our database only has coordinates. Need to figure how to take street names and turn it into coordinates. (Shawn)

--11/16/2023 - Changed timer to use countDownTimer instead of previous jury rigged method (Daniel)

--11/13/2023 - fixed issues with timer updating, now occurs every 15 seconds. Timer will now turn red when the time limit has been execeded (Daniel)

--11/12/2023 - fixed constraints on menu bar, changed announcements source, cleaned up onCreate a bit (nick)

--11/12/2023 - Clicking the park button will now update the timer automatically, refresh button has also been removed (Daniel)

--11/10/2023 - Timer will now update itself every minute- note that this doesn't line up with time change of the device clock, it just occurs every minute after the app is created (Daniel)

--10/31/2023: Milestone 1 functionality complete, will tie to logic of timer. Based on location of user, they get the restrictions + a time of duration to park : ). (Brian) 

--10/30/2023 - Completed logic of the clock to display time remaing before the user will need to move their car, added refresh button as a temporary measure until I can find a functional way to automatically refresh the timer every minute (Daniel)

-- 10/29/2023 - Communication with DB is very volitile at this state. Rounding DB lat/long down to work with google's lat/long. Will not push this update until I resolve this issue tat casues constant crashes as to not delay development time. I believe this issue lays with the active map in some way (it throws a billion gazillion log messages). (Brian)


--10/29/2023 - added ability to select a date when adjusting the timer, have not changed the logic of the clock display to reflect this yet (Daniel)

--10/28/23 - Map follows user's live location, working on park button + backend api integration, then will fix map to properly fit screen (a little bit of white space on top). (Brian)

--10/28/23 - add timer and button to adjust timer to main activity, time selected is stored in shared preferences and will used to calculate the time remaining when the app is run. However there is still an issue with calculating the time remaining between days. There is no way to set and store dates as of yet. (Daniel)

-- 10/24/23 - added RSS compatibility to display news from madison street engineering, added a news page (nick)

-- 10/23/23 - made the menu bar work (nick)

-- 10/21/23 - added a park button on the main screen, added some project 4 location code (nick)

-- 10/20/23 - Added a bottom menu bar via android @menu, added assets for the menu icons from the android studio stock assets (nick)

-- 10/19/23 - Removed ADA dataset, only 2 spaces, with one being way away, in Madison have non-24hr restriction. Found commonalities, cleaning data, making DB file to share. DB file created, shared in Google Drive (Brian)

-- 10/18/23 / 10/19/23 - Configuring postgre/postgis AWS database + ec2 instance(dont quote me on this) for API backend for app. Had to switch from SQLite due to geograpic behavior of data, would be very very slow to search through all the points. Database architecture complete, importing and formatting to Postgis required format then testing performance. 

-- 10/17/23 - Created Repo
