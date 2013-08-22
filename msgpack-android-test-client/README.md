msgpack-android-test-server
==========================

This is the client part of a simple Android test pair, for sanity testing.

ServerConstants in msgpack-android-test-server contains the port.

Host is set through a start dialog, and is remembered within the app prefs.

To deploy quickly to available device, run:

**mvn clean package android:deploy android:run**