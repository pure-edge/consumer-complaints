package com.example.logingps.models;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.logingps.R;
import com.example.logingps.utils.DateTimeConverter;
import com.example.logingps.utils.UriConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrewWorkOrdersDAO extends FirebaseDAO {
    public CrewWorkOrdersDAO(Context context) {
        super(context);
    }

    public void confirmWorkOrderFinished(CrewWorkOrder order, Date date, OnSuccessListener successListener, OnFailureListener failureListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("date_completed", date);
        // TODO: UPDATE account/{userID}/work_orders/{workOrderID}
        database.collection(FirebaseConstants.COLLECTION_ACCOUNT)
                .document(Firebase.getCurrentUser().getUid())
                .collection(FirebaseConstants.SUBCOLLECTION_WORK_ORDERS)
                .document(order.getId())
                .set(data, SetOptions.merge());

        // TODO: UPDATE work_order
        data = new HashMap<>();
        data.put("date_completed", date);
        //data.put("photos", UriConverter.getFileNames(photos));
        //data.put("signature", UriConverter.getFileName(signature));
        database.collection(FirebaseConstants.COLLECTION_WORK_ORDER)
                .document(order.getWork_order_id())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public List<CrewWorkOrder> getCrewWorkOrders(String uid) {
        final List<CrewWorkOrder> workOrders = new ArrayList<>();
        database.collection(FirebaseConstants.COLLECTION_ACCOUNT)
                .document(uid)
                .collection(FirebaseConstants.SUBCOLLECTION_WORK_ORDERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MemberConsumerOwner memberConsumerOwner =
                                        new MemberConsumerOwner(document.get("member_consumer_owner"));

                                Timestamp timestamp = document.getTimestamp("date_created");
                                long dateCreatedMillis = timestamp.getSeconds() * 1000 + timestamp.getSeconds() / 1000000;


                                CrewWorkOrder crewWorkOrder = null;
                                try {
                                    /*crewWorkOrder = new CrewWorkOrder(document.getString("assigned_by"),
                                            DateTimeConverter.toDateObject(dateCreatedMillis),
                                            document.getString("description"),
                                            memberConsumerOwner);*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                workOrders.add(crewWorkOrder);
                            }
                        }
                    }
                });
        return workOrders;
    }
}
