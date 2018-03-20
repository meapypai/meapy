package project.meapy.meapy.utils;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by yassi on 20/03/2018.
 */

public class FirebaseMessageService extends com.google.firebase.messaging.FirebaseMessagingService {

    public FirebaseMessageService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }
}
