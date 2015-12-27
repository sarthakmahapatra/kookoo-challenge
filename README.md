# kookoo-challenge - Quick Conference

An android app that can help host a quick tele-conference.

The user can restrict the people who can join the conference. The Quick Conference app will allow to invite people to the conference. User can choose to keep a closed group conference, where only select people can be allowed to join the conference. People can be added to the closed group by adding people from user phone's contact, and people will be allowed to join the conference when they call from selected numbers.

Users will be automatically directed to the conference, based on the number they are calling from. If the calling number is not configured in the system they will be rejected and the call will hung-up.

Users will be identified and addressed with Name when they dial in.

Conference details can be shared of messaging platforms like WhatsApp.

#How does it work?

Uses the following:
  1. kookoo API : provides the tele-conference APIs. (http://kookoo.ozonetel.com/)
  2. Node JS : Host web service that exposes 2 endpoints:
      1. / : called by the kookoo api 
      2. /register : called by the Quick Conference app, used to register user and conference details.
  3. Cloudant DB : Backend NoSQL DB that stores all user and conference details.
  4. Quick Conference Android app : An android app
