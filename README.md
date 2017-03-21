# MinePanel 
A simple GUI for running your Minecraft server. Will show output, will allow you to easily edit properties, etc.
This is not meant for large server owners (i.e. companies with rentable servers), but more for the average Joe who doesn't know what all the settings do or how to set up the server with the correct arguments.

All the user has to do is forward a port and then the server app will do the rest!

To discuss the app, timeline, features, or to submit requests/questions, head to the [subreddit](https://www.reddit.com/r/minepanel).

## To-do:
1. ~~Get it running the jar and showing the output. This is the current goal.~~
2. ~~Make sure file IO exceptions are all handled nicely. Don't want the program crashing because the user messed with it, now do we?~~
3. Make it look nicer. I'm using SWT (with WindowBuilder) to make the GUI. While it does use native designs, images, etc., I'm not a GUI designer. I'm a programmer. Any help with the locations of buttons would be much appreciated.

## For those who want to help:
First of all, I'm using [Eclipse](http://www.eclipse.org) and [Java 8](https://www.java.com/en/).
So what can you do to help?
- First and foremost, I need help with the GUI. I'm a coder, not a designer. Just tell me what I can do to make it nice. I'm using WindowBuilder for Eclipse, cuz frankly, I'm too lazy to hand code every little GUI I ever make. If you download and open this project, you will have issues. This is because you need to download the latest Eclipse SWT package and copy the contents (should be stuff like `swt.jar`, `src.zip`, etc.) into another project called "org.eclipse.swt" alongside the Minepanel project.
- Second, I am only working on making it *work*. Later, I'll get my code nicely tidied up. If you're an OCD kinda person who hates how my code it laid out, you can fix it. Just don't make any crazy changes, because I still need to be able to use it :P
- Finally, for you experienced coders out there, you should help me make things work better or more efficiently. As I said, I'm first trying to just get it working nicely. If I need a `try/catch` somewhere, add it. If I don't, remove it.

## So when will it be done?
Since I am a student, I have limited time to work on this project, and it has been dormant for a long time now. I recently looked back into it, and it's pretty close to being where I want it for release. It really should work for most people at this time, but I don't yet have executables created for release since it's a good amount of work (easiest way for me just is to create one on each OS that I want to release it for).

## How much will it cost?
Well, it'll be free. However, you can always buy the DLC to allow the app to run a server :trollface:

But seriously, it will be totally free. If you like the project, you should [donate here.](https://secure2.wish.org/site/SPageServer?pagename=donate&chid=100-000) If you donate, prove it to me and I will add your name here ;)

## All that legal licensing garbage:
You can use this code in your own projects, but you must provide a description of changes to the code and a copyright notice.

This open source project is distributed under the [Apache 2.0 License](https://github.com/WillEccles/MinePanel/blob/master/LICENSE.txt).
