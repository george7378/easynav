# easynav
> Released Android app is [available here](https://play.google.com/store/apps/details?id=kristianseng.easynav).

EasyNav is a GPS navigation tool for Android which aims to make it as simple as possible to find your way to any location on Earth, defined by you. It provides self-contained navigation which requires GPS connectivity only - there's no need to be online to download maps or local information because EasyNav doesn't use them.

The interface lets you choose a user-defined waypoint and provides distance, direction and relative altitude information. The app doesn't provide routing, traffic or local area information, but this is part of its beauty.

If you're out on a drive, walking in the wilderness or even lost in the middle of the desert, all you have to do is acquire a GPS connection on your device, open EasyNav and enter the coordinates of your destination to be pointed towards it.

## Interface

**Home screen**

This shows the important navigation details for the active waypoint. Tapping either of the main data fields will switch between different readings. The first field shows either the distance to your waypoint or your GPS latitude/longitude (in degrees). The second field shows your altitude relative to the waypoint (altitude offset) or your absolute GPS altitude (relative to the WGS84 ellipsoid).

The compass bezel will point towards your selected waypoint (provided a location fix has been possible). The readout in the centre of the bezel shows your relative bearing to the waypoint. When this reading is zero (and the bezel is pointing upwards), you are facing your destination.

<img src="https://raw.githubusercontent.com/george7378/easynav/master/misc/readme/1.png" width="250">

**Waypoints screen**

Here you can create, delete and activate custom waypoints. Any saved waypoints will appear in the drop-down menu. Selecting a waypoint in this menu lets you immediately activate or delete it. If you activate the waypoint, any navigation info on the main page will now point to this waypoint. You can review the currently active waypoint in the field below the drop-down menu. Tapping this field will remind you of the coordinates of the active waypoint.
	
<img src="https://raw.githubusercontent.com/george7378/easynav/master/misc/readme/2.png" width="250">

**New waypoint screen**

This screen lets you add a new waypoint to the drop-down list on the previous page. You must enter a unique name, a decimal latitude (from -90 to +90 degrees), a decimal longitude (from -180 to +180 degrees) and an altitude above the WGS84 ellipsoid baseline. Note that the WGS84 ellipsoid does not always correspond directly to sea level due to 'geoid undulations' which are not accounted for in GPS signals.

<img src="https://raw.githubusercontent.com/george7378/easynav/master/misc/readme/3.png" width="250">

**Settings screen**

Here you can change core settings for the app. The Update Frequency field controls how often the app queries for new GPS readings. A smaller value means more frequent updates, but a higher battery usage. The Metric Units field speaks for itself - it will control the units used to both display and enter information throughout the app.
	
<img src="https://raw.githubusercontent.com/george7378/easynav/master/misc/readme/4.png" width="250">
