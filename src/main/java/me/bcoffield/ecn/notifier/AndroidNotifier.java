package me.bcoffield.ecn.notifier;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class AndroidNotifier extends AbstractNotifier {
  @Override
  void sendNotification(String note) {
    Storage storage = StorageOptions.getDefaultInstance().getService();
    Bucket defaultBucket = storage.list().getValues().iterator().next();
    Iterable<Blob> firebaseTokenBlobs = defaultBucket.list(Storage.BlobListOption.prefix("firebaseTokens")).getValues();
    List<Message> messages = StreamSupport.stream(firebaseTokenBlobs.spliterator(), true).map(blob -> Message.builder()
        .setToken(new String(blob.getContent()))
        .putData("note", note)
        .build()).collect(Collectors.toList());
    try {
      FirebaseMessaging.getInstance().sendAll(messages);
    } catch (FirebaseMessagingException e) {
      log.error("Unable to send message", e);
    }
  }

  @Override
  NotifierType getNotifierType() {
    return NotifierType.ANDROID;
  }
}
