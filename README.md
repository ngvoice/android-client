Android Client
==============

It's a SIP softphone based on CSipSimple created with the intention of automating the configuration 
of a ng-voice account.

It consists of a series of classes which are capable of connecting via HTTPS to a REST API using a one-time login 
to fetch every account and information needed to create a local SIP account on the softphone.

*This project is a work in progress.*

Features
==============
Apart from the very well known features of the Csipsiple softphone, this custom version has:

- one click configuration using a unique login credential
- every account is configured automatically. Special for those who are not used to SIP terminology
- custom codec list selection for NB and WB
- mobile push notifications using Google Cloud Messaging. This allows us to trigger configuration reloading, registration
and unregistration on demand, etc.
- auto-waking-up to receive calls if needed (using GCM)
- autoload configuration by reading a QR code (zero typing configuration)
- video plugin enabled by default

Screenshots
==============

![Splash screen](http://caruizdiaz.com/wp-content/uploads/2014/05/Screenshot_2014-05-12-23-04-03.png)

![Login screen](http://caruizdiaz.com/wp-content/uploads/2014/05/Screenshot_2014-05-12-23-07-50.png)

![Scanning QR code](http://caruizdiaz.com/wp-content/uploads/2014/05/Screenshot_2014-05-12-23-11-19.png)

![Dialer](http://caruizdiaz.com/wp-content/uploads/2014/05/Screenshot_2014-05-12-23-01-28.png)

![Customer Info](http://i.imgur.com/TFQGSPS.png)
