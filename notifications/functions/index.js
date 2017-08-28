const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase)

exports.sendNotification = functions.database.ref('/Notifications/{user_id}/{notification_id}').onWrite(event =>{
    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;
    console.log("The User_id is : ",user_id);

    if(!event.data.val()){
        console.log('A Notification is deleted : ', notification_id);
        return
    }

    const from_user = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');
        
    return from_user.then(userResult => {
        const from_user_id = userResult.val().from;
        console.log("You have New Notification from : ", from_user_id);
    
        const userQuery = admin.database().ref(`/Users/${from_user_id}/name`).once('value');
        const device_token = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
        return Promise.all([userQuery, device_token]).then(result => {
            console.log("from_user_id",from_user_id);
            const to_user_name = result[0].val(); 
            const token_id = result[1].val();
                   const payload = {
            notification:{
            title: "FRIEND REQUEST",
            body: `${to_user_name} sent a Friend Request`,
            icon: "default",
            click_action : "chatapp.com.chatapp_TARGET_NOTIFICATION",
            },
            data : {
                from_user : from_user_id
            }
        };
          return admin.messaging().sendToDevice(token_id,payload).then(Response =>{
                console.log("Notification Feature Added")
            });
    
    });
    

    });

});

