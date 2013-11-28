Android Client
==============

It's a SIP softphone based on CSipSimple created with the intention of automating the configuration 
of a ng-voice account.

It consists of a series of classes which are capable of connecting via HTTPS to a REST API using a one-time login 
to fetch every account and information needed to create a local SIP account on the softphone.

This project is a work in progress.

Features
==============
Apart from the very well known features of the Csipsiple softphone, this custom version has:

- one click configuration using a unique login credential
- every account is configured automatically. Special for those who are not used to SIP terminology
- mobile push notifications using Google Cloud Messaging. This allows us to trigger configuration reloading, registration
and unregistration on demand, etc.

Screenshots
==============

![Splash screen](http://i.imgur.com/Ztj9rIT.png)

![Login screen](http://i.imgur.com/3HhUCGR.png)

![Configuration loader](http://i.imgur.com/BBlrwqN.png)

![Dialer](http://i.imgur.com/duPyn4s.png)

![Customer Info](http://i.imgur.com/TFQGSPS.png)
