# ArchTouch Interview Challenge

## A simple app to query bus routes

Tha app allows users to find bus routes based on a street name or a location choosen on Google.Maps

The home screen contains a text input field where the user can type a street name, and a button
on the action bar to go to a Google Maps view and pick a location. The query is performed when
the user can press a button next to the input field, or when a location is picked on the map view.
Query results are displayed on de home screeen, on a List below the text input. Each item
displays the route code and name.

Clicking on any item in the results list will start two background tasks querying the selected
route's departure schedule and stops. After completing these tasks, the app switches to a tabbed
pager view, displaying the routes stops and departures (weekdays, saturday and sunday) on individual
views.

Tested on my Moto G 1st gen