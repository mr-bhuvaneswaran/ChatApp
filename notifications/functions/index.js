const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase)

exports.sendNotification = functions.database.ref('/Notifications/{user_id}/{notification_id}').onWrite(event =>{
    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;
    console.log("The User_id is : ",user_id);

    if(!event.data.value()){
        console.log('A Notification is deleted : ', notification_id);
        return
    }

    const payload = {
        Notification:{
            title: "FRIEND REQUEST",
            body: "You have New Friend Request",
            icon: ""
        }
    };

    return admin.messaging().sendToDevice(/*TOKEN ID*/,payload).then(Response =>{
        console.log("Notification Feature Added")
    });
});