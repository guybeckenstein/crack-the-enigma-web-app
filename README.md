# Enigma Machine - Web application
## A web application with three types of clients. UBoat, Allies, Agent

### It simulates a competition to find the encrypted string of the Enigma machine, in which the winners are those who find the original cipher in the shortest time.

### This is mainly a backend project.

- **General**
```bash
Each client has a personalized login screen, where he chooses a unique username from all types of clients.
```
- **UBoat**
```bash
A client which creates Enigma machine configuration guidelines - 
first by uploading a XML file to the server, which includes the current Enigma machine that is used, 
then by creating the configuration of the machine, and choosing the encrypted message to be decrypted. 
All this is in an existing competition where the admin is the UBoat user.
This encrypted message is forwarded to all Allies teams.
The XML file defines the number of participants allowed and the dictionary used in the competition.
```

- **Allies**
```bash
A client who chooses a UBoat competition that is waiting for participants, and joins it, until it starts. 
This happens as soon as it is filled with Allies according to the XML file definitions. 
From this moment on, the Allies search for 'decryption candidates' by brute force, 
and each such candidate is displayed on the screen. 
The process will stop until the Allies' find the original cipher.
The encryptions are actually performed by Agents that are linked to the Allies, 
where the Allies only generate the configurations of the machine, which each Agent encrypts by itself.
```

- **Agent**
```bash
On his login screen, the Agent must choose an Allies type client from the multitude of options presented to him, 
but only from among those that are not in active competition (one that has started and in fact the whole 
brute force process takes place in it).
Then, once the Allies it is linked to are in active competition, 
the Agent will begin to decode the encrypted cipher in a variety of configurations, 
until it finds the original cipher.
When the Agent finds candidates for decoding, it will send them to the Allies, 
and when the UBoat indicates that the original cipher has been found, the identity of the winning Allies will be announced.
```


It is including Enigma machine implementation, automatic decipher process (using brute force), and client-server model. 

The machine is a generic one, not limited to a certain scale. 

The process involved working with multiple threads, files, collections and other OOP concepts and principles (e.g. interfaces, encapsulation, SOLID). 

Familiarity with Spring and Maven frameworks, and Postman for building the API of HTTP application.

I used Apache Tomcat for web-server environment.



**Screenshots**
