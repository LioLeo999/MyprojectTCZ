const functions = require('firebase-functions/v1');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnNewMessage = functions.database.ref('/Chats/{chatId}/messages/{messageId}')
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.val();
        const receiverId = messageData.receiverId;

        if (!receiverId) return null;

        const userSnapshot = await admin.database().ref(`/Users/${receiverId}/fcmToken`).once('value');
        const token = userSnapshot.val();

        if (!token) return null;

        // שינוי קריטי: החלפנו את notification ב-data.
        // זה מבטל את התערבות המערכת ומעביר את השליטה לאפליקציה שלך.
        const payload = {
            data: {
                title: 'הודעה חדשה!',
                body: messageData.content || 'קיבלת הודעה חדשה באפליקציה.',
                chatId: context.params.chatId // אפשר אפילו להעביר את מזהה הצ'אט
            }
        };

        return admin.messaging().sendToDevice(token, payload);
    });