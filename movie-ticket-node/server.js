process.env.DEBUG = 'actions-on-google:*';
const App = require('actions-on-google').DialogflowApp;
var express = require("express");
var app = express();
var cfenv = require("cfenv");
var bodyParser = require('body-parser')


app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var mydb;
var convid;
var get_otp;
var dt, movie, ticket, dates, otp_val, url, usr, details;

app.post("/api/visitors/", function (request, response) {
    const googleapp = new App({request, response});
    otp_val = Math.floor(Math.random() * 2000);
    if(otp_val<1001)
    {
      otp_val=10000-otp_val;
    }
    convid=request.body.sessionId;
    var body = JSON.stringify(request.body);
    var jsonobject = request.body;
    var userName = jsonobject.result.parameters.movie_name ;
    var number = jsonobject.result.parameters.number;
    var date = jsonobject.result.parameters.date;
    var userid = jsonobject.result.parameters.str; 
    var otp = jsonobject.result.parameters.otp;
      

    if(request.body.result.metadata.intentName == 'Ticket'){
    if(!mydb) {
        
        response.send("Hello " + userName + "!");
        return;
      }

      var PushNotifications = require('ibm-push-notifications').PushNotifications;
      var Notification = require('ibm-push-notifications').Notification;
      var PushMessageBuilder = require('ibm-push-notifications').PushMessageBuilder;
      
      //Region, "AppGuid" and "App secret" provided for OTP.
      var myPushNotifications = new PushNotifications(PushNotifications.Region.US_SOUTH, "AppGuid", "App secret");
      
      var message = PushMessageBuilder.Message.alert(otp_val).url("a").build();
      var notificationExample = Notification.message(message).build();
      var target = PushMessageBuilder.Target.userIds([userid]).build();
      var notificationExample = Notification.message(message)
          .target(target).settings(null).build();
      myPushNotifications.send(notificationExample, function (error, response1, body) {
       
     });
      
      // insert the username as a document
      mydb.insert({ "Movie_Name" : userName , "No_Tickets": number ,"date": date, "userid": userid, "convid": convid,"otpv": otp_val}, function(err, body, header) {
        if (err) {
          return console.log('[mydb.insert] ', err.message);
        }
        googleapp.ask('Hey ! tell me your OTP');
      }); 

     
    }else if(request.body.result.metadata.intentName == 'OTP_Intent'){
      var searchQuery = {
        "selector": {
          "convid": {
            "$eq": convid
          }         
      },
      "fields": [
          "otpv",
          "_id",
          "_rev",
          "Movie_Name",
           "No_Tickets",
           "date",
           "userid"


      ]
      };
      mydb.find(searchQuery, function(err, data){
     
        console.log("Inside Find");
        if (!err) {
          dt = data.docs[0].otpv;
          movie = data.docs[0].Movie_Name;
          ticket = data.docs[0].No_Tickets;
          dates = data.docs[0].date;
          usr = data.docs[0].userid;
        //otp check
        if(otp==dt)
        {
          details="{\"Movie\": \" "+movie+"\" ,\"Ticket\": \""+ticket+"\" ,\"Date\": \""+ dates+"\" }";
          url= "Movie: "+movie+"\nTickets: "+ticket+"\nDate: "+dates;
          var pld = {"Movie":movie,"Ticket":ticket,"Date":dates};
          var con=JSON.stringify(pld);
          var PushNotifications = require('ibm-push-notifications').PushNotifications;
          var Notification = require('ibm-push-notifications').Notification;
          var PushMessageBuilder = require('ibm-push-notifications').PushMessageBuilder;

          //Region, "AppGuid" and "App secret" provided for Movie Details.
          var myPushNotifications = new PushNotifications(PushNotifications.Region.US_SOUTH, "AppGuid", "App secret");
          
          var message = PushMessageBuilder.Message.alert(url).url(details).build();
          var notificationExample = Notification.message(message).build();
          var target = PushMessageBuilder.Target.userIds([usr]).build();
          var style = PushMessageBuilder.FCMStyle.type(Notification.FCMStyleTypes
              .PICTURE_NOTIFICATION).title("Big Text Notification").url(url)
              .build();
          var fcm = PushMessageBuilder.FCM.priority(Notification.FCMPriority.DEFAULT).sync(true).visibility(Notification.Visibility.PUBLIC)
              .style(style).payload(con).build();
          var settings = PushMessageBuilder.Settings.apns(null).fcm(fcm).safariWeb(null)
              .firefoxWeb(null).chromeAppExt(null).chromeWeb(null).build();   
          var notificationExample = Notification.message(message)
              .target(target).settings(settings).build();
          
          myPushNotifications.send(notificationExample, function (error, response1, body) {
            });
        
          googleapp.tell("I've booked "+ ticket + ' movie tickets of ' +movie+ ' for '+dates+'. Enjoy ');
          
        }
        else{
         
          googleapp.tell('Error ! you entered wrong otp');
        }
        
        //odb insert
        
          
        } else {
            console.log(err);
          
          }
      });
    }

});

// load local VCAP configuration  and service credentials
var vcapLocal;
var otp_dbname, odb;
try {
  vcapLocal = require('./vcap-local.example.json');
  console.log("Loaded local VCAP", vcapLocal);
} catch (e) { 
  console.log("Err"+ e);
}

const appEnvOpts = vcapLocal ? { vcap: vcapLocal} : {}

const appEnv = cfenv.getAppEnv(appEnvOpts);

if (appEnv.services['cloudantNoSQLDB'] || appEnv.getService(/cloudant/)) {
  // Load the Cloudant library.
  var Cloudant = require('cloudant');

  // Initialize database with credentials
  if (appEnv.services['cloudantNoSQLDB']) {
     // CF service named 'cloudantNoSQLDB'
     var cloudant = Cloudant(appEnv.services['cloudantNoSQLDB'][0].credentials);
  } else {
     // user-provided service with 'cloudant' in its name
     var cloudant = Cloudant(appEnv.getService(/cloudant/).credentials);
  }

  //database name
  var dbName = 'mydb';

  // Create a new "mydb" database.
  cloudant.db.create(dbName, function(err, data) {
    if(!err) //err if database doesn't already exists
      console.log("Created database: " + dbName );
  });


  cloudant.db.create(otp_dbname, function(err, data) {
    if(!err) //err if database doesn't already exists
      console.log("Created database: "  + otp_dbname);
  });


  // Specify the database we are going to use (mydb)...
  mydb = cloudant.db.use(dbName);
}

//serve static file (index.html, images, css)
app.use(express.static(__dirname + '/views'));


var port = process.env.PORT || 3000;
app.listen(port, function() {
    console.log("To view your app, open this link in your browser: http://localhost:" + port);
});


//get mydb

app.get("/api/visitors", function (request, response) {
  var names = [];
  if(!mydb) {
    response.json(names);
    return;
  }

  mydb.list({ include_docs: true }, function(err, body) {
    if (!err) {
      body.rows.forEach(function(row) {
        if(movie) 
          names.push(movie);
       if(ticket)
       names.push(ticket);   
       if(dates)
       names.push(dates);  
       if(row.doc.otp)
       names.push(row.doc.otp); 
       if(row.doc.emaili)
       names.push(row.doc.emaili);
      });
      response.json(names);
    }
  });
});





