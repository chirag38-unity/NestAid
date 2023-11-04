# Nest Aid

A GPS-based Community Service Application for Donating Food, Clothes, and Shelter

The proposed app is designed to deal with the demanding situations confronted with the aid of donors and receivers in terms of locating and gaining access to vital sources together with meals, clothing, and refuge. The app presents a platform that simplifies donating and receiving those sources by supplying a streamlined and person-friendly interface. Through the app, donors can without difficulty sign up and offer their donations of food, garb, and refuge. Donors can imply the forms of assets they're willing to provide and their availability. The app additionally presents a simple and steady price system that lets donors make financial contributions to the purpose. On the other hand, receivers can access the app to find donors in their region. By using the app's place-primarily based offerings, receivers can quickly pick out close-by donors who have the assets they need. Receivers can then initiate touch with the donors via the app to set up the pickup or shipping of the donated sources. The app's use of technology performs an essential function in simplifying the donation and receiving technique. By leveraging region-based total offerings, the app allows connecting donors and receivers in real time. Additionally, the app's user-pleasant interface and steady charge machine make the technique of donating and receiving assets convenient and efficient. Overall, the app's goals are to make a contribution in the direction of the betterment of society by means of enhancing admission to vital assets and fostering a sense of network engagement. By offering a platform for donors and receivers to connect and work in the direction of a commonplace cause, the app allows for bridging the space between the ones in need and those who are willing to assist.


# Flowchart of the proposed system
<img width="493" alt="image" src="https://github.com/chirag38-unity/NestAid/assets/90930534/d1bb5885-44a4-46c4-b99d-d159a282c79c">

# Screenshots of the application

User's registration and Login:

<img width="400" alt="User Registration" src="https://github.com/chirag38-unity/NestAid/assets/90930534/870d7fe1-9042-48ff-894f-b906c9119270">   <img width="400" alt="User Login" src="https://github.com/chirag38-unity/NestAid/assets/90930534/bcfbdd52-ccc2-43d0-bedf-91f1f3358cb8">

Donor's dashboard and donation form:

<img width="400" alt="Donor dashboard" src="https://github.com/chirag38-unity/NestAid/assets/90930534/3f4d813a-3b1a-4461-9824-f62f9f02fd8f">  <img width="400" alt="flowchart" src="https://github.com/chirag38-unity/NestAid/assets/90930534/184175af-405f-4af7-92b2-2f0a55a3f996">

Contributions can be either deactivated or eliminated by the donor:

<img width="400" alt="deactivate" src="https://github.com/chirag38-unity/NestAid/assets/90930534/1315face-c839-474a-b1af-2f29407446c3"> <img width="400" alt="delete" src="https://github.com/chirag38-unity/NestAid/assets/90930534/bdfe6d8e-8954-4c32-afa2-d15137277c02">

Receiver's dashboard and donation list:

<img width="400" alt="receiver's dashboard " src="https://github.com/chirag38-unity/NestAid/assets/90930534/8b283cbc-a816-4fbf-ad2b-0d1e509001b1">    <img width="400" alt="donation" src="https://github.com/chirag38-unity/NestAid/assets/90930534/d20170b9-9c04-4113-a969-13f999c7666f">

# Methodology

* Sharing -
  When a donor makes a donation their current location is grabbed and a document comprising of the item and donor info is added to a list of donations and stored according to the geohashes. A donor can opt to share his exact location or just the geohash and later talk to the receiver about it.

* Receiving -
  A receiver is provided with a list of donations and information about the donors from the nearby area allowing them to choose whom they want to approach. They can approach by calling & messaging.

* Geohashes -
  Geo-hashes encode a geographic location into a short string of letters and digits. Further, the receiver’s vicinity is scanned in a rectangular fashion covering a circular area of about 10 sq km. The area comprises adjacent hashes of the receiver’s location. 


# Technologies, libraries, and packages used

* Java
* Firebase
* Android Studio
* Geohashes
  
  # Local Setup

1. Fork this repository
2. Clone it in your local system
3. Open Android Studio and select 'Open Project'. You can just browse through the file chooser to the folder where you have cloned the project. The file chooser will show an Android face as the folder icon, which you can select to open the project.
4. Link the project to your Firebase Account and your RESTFUL server (we have included sockets too)
5. After opening the project Android Studio will try to build the project directly. To create it manually, follow the menu path 'Build'/'Make Project', or just click the 'Play' button in the toolbar to build and run it on a mobile device or an emulator. The resulting .apk file will be saved in the 'build/outputs/apk/' subdirectory in the project folder.
6. You can install the .apk file on your device and enjoy its enriching features.
