# Nest Aid

A GPS-based Community Service Application for Donating Food, Clothes, and Shelter

The proposed app is designed to deal with the demanding situations confronted with the aid of donors and receivers in terms of locating and gaining access to vital sources together with meals, clothing, and refuge. The app presents a platform that simplifies donating and receiving those sources by supplying a streamlined and person-friendly interface. Through the app, donors can without difficulty sign up and offer their donations of food, garb, and refuge. Donors can imply the forms of assets they're willing to provide and their availability. The app additionally presents a simple and steady price system that lets donors make financial contributions to the purpose. On the other hand, receivers can access the app to find donors in their region. By using the app's place-primarily based offerings, receivers can quickly pick out close-by donors who have the assets they need. Receivers can then initiate touch with the donors via the app to set up the pickup or shipping of the donated sources. The app's use of technology performs an essential function in simplifying the donation and receiving technique. By leveraging region-based total offerings, the app allows connecting donors and receivers in real time. Additionally, the app's user-pleasant interface and steady charge machine make the technique of donating and receiving assets convenient and efficient. Overall, the app's goals are to make a contribution in the direction of the betterment of society by means of enhancing admission to vital assets and fostering a sense of network engagement. By offering a platform for donors and receivers to connect and work in the direction of a commonplace cause, the app allows for bridging the space between the ones in need and those who are willing to assist.


# Flowchart of the proposed system
<img width="330" alt="flowchart" src="https://github.com/chirag38-unity/NestAid/assets/78786831/9da1cefc-d003-45ce-8e65-004598e6b282">


# Screenshots of the application

User's registration and Login:

<img width="400" alt="User Registration" src="https://github.com/khushishar/donateapp/assets/90930534/142adaf6-6d47-4845-ab4d-af19ebf6c767">   <img width="400" alt="User Login" src="https://github.com/khushishar/donateapp/assets/90930534/fd0f3b17-c1aa-403f-ad37-a70c37d30030">

Donor's dashboard and donation form:

<img width="400" alt="Donor dashboard" src="https://github.com/khushishar/donateapp/assets/90930534/f8068063-b115-4771-9997-f0b2376a4d74">  <img width="400" alt="flowchart" src="https://github.com/khushishar/donateapp/assets/90930534/33db564b-2ebe-472f-add2-61c96569af49">

Contributions can be either deactivated or eliminated by the donor:

<img width="400" alt="deactivate" src="https://github.com/khushishar/donateapp/assets/90930534/4aef5161-43ec-4519-8245-cb33ad4d2e6f"> <img width="400" alt="delete" src="https://github.com/khushishar/donateapp/assets/90930534/a79637cf-22bf-4aea-a074-bc245b2ef4f3">

Receiver's dashboard and donation list:

<img width="400" alt="receiver's dashboard " src="https://github.com/khushishar/donateapp/assets/90930534/90b9d17d-9920-4d3f-900c-9a3bbf6bf82e">    <img width="400" alt="donation" src="https://github.com/khushishar/donateapp/assets/90930534/a1bd5016-fa97-443a-a871-da25ce2da02a">

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
