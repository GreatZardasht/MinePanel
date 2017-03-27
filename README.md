# MinePanel 
This will be a pretty simple-to-use interface for managing a Minecraft server. It gives you the ability to edit properties and such without ever having to touch the properties file, and if you want to change one of the more advanced properties, you can do that too. You can add custom commands, too! For more info about that, see the wiki.

All the user has to do is forward a port and then the app will do the rest! The only file the user has to manually edit is the EULA file created on startup for the first time, since if Minepanel edited it for them, they wouldn't have consciously agreed to the EULA.

## Setup/Instructions:
1. Make sure you compile or download the executable for your platform. **At the moment, there are no downloadable executables, since I have not yet deemed the program worthy of a first release. However, you can still (if you know what you are doing) compile the program yourself. More info about that can be found below.**
2. Download the latest Minecraft server jar. You can do this through the new launcher, just go to the version you want in the version selector and download the server for it.
3. Place the server jar file in the folder you want it to live in and rename it to `server.jar`.
4. Place the executable (jar file) for Minepanel into the same folder and launch it. If you don’t have Java installed, make sure you get the right one for your system [here](https://www.java.com).
5. Hit the start button. The first time you run the server, it’s going to create a file for the EULA (<u>E</u>nd <u>U</u>ser <u>L</u>icense <u>A</u>greement), which you will have to agree to. Minepanel will instruct you on how to do that.
6. After you accept the EULA, run the server again and then stop it once it finished launching. You should then check out the server properties, mess around, and make it how you want it! Change the gamemode, the world name, difficulty, etc. and have at it. For information on how to connect, see [here](http://minecraft.gamepedia.com/Tutorials/Setting_up_a_server#Connect_to_the_Minecraft_server).

## To-do:
1. ~~Get it running the jar and showing the output. This is the current goal.~~
2. ~~Make sure file IO exceptions are all handled nicely. Don't want the program crashing because the user messed with it, now do we?~~
3. Make it look nicer. I'm using SWT (with WindowBuilder) to make the GUI. While it does use native designs, images, etc., I'm not a GUI designer. I'm a programmer. Any help with the locations of buttons would be much appreciated.

## Setting up your own project:
First, make sure you have Java 8+ installed (preferably the latest version, and make sure it’s the JDK, not just the JRE), and then make sure you have Eclipse installed. I recommend the latest version. Then, follow the following steps:
1. Clone this repository. For example:
```
$ cd ~/github/
$ git clone https://github.com/willeccles/minepanel
```
2. Then, go [here](http://download.eclipse.org/eclipse/downloads/) and under the “Latest Downloads” heading, choose the most recent version and click the link.
3. On the page that brings you to, scroll down to the “SWT Binary and Source” section and get the appropriate one for your platform.
4. Extract that archive. It should contain a folder called “org.eclipse.swt.” Copy the folder into the repository you cloned. Thus, the repository should contain README.md, Minepanel, a couple other things, and org.eclipse.swt.
5. Open Eclipse. Go to File > Open Projects From Filesystem.
6. Hit the Browse button and select BOTH of the folders in the repository: Minepanel and org.eclipse.swt.
7. Right-click on Minepanel in Eclipse and go to Properties. Click on Java Build Path.
8. Go to the Libraries tab. Click “Add External Jars…”
9. Browse to the org.eclipse.swt project you downloaded and select swt.jar.
10. You should now be able to build and run the Minepanel project. Complicated? Yeah, kinda, but if it wasn’t done right you wouldn’t be able to use the project, so…

## So when will it be done?
Since I am a student, I have limited time to work on this project, and it has been dormant for a long time now. I recently looked back into it, and it's pretty close to being where I want it for release. It really should work for most people at this time, but I don't yet have executables created for release since it's a good amount of work (easiest way for me just is to create one on each OS that I want to release it for).

## How much will it cost?
Well, it'll be free. However, you can always buy the DLC to allow the app to run a server :trollface:

But seriously, it will be totally free. If you like the project, you should [donate here.](https://secure2.wish.org/site/SPageServer?pagename=donate&chid=100-000) If you donate, prove it to me and I will add your name here ;)

## All that legal licensing garbage:
You can use this code in your own projects, but you must provide a description of changes to the code and a copyright notice.

This open source project is distributed under the [Apache 2.0 License](https://github.com/WillEccles/MinePanel/blob/master/LICENSE.txt).
