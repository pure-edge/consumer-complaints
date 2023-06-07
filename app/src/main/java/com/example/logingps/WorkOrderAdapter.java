package com.example.logingps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.logingps.models.CrewWorkOrder;
import com.example.logingps.utils.DateTimeConverter;

import java.util.List;

public class WorkOrderAdapter extends ArrayAdapter<CrewWorkOrder> {
    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView description;
        TextView address;
    }

    public WorkOrderAdapter(Context context, int resourceId, List<CrewWorkOrder> workOrders) {
        super(context, resourceId, workOrders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CrewWorkOrder workOrder = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.work_order_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textViewDate);
            viewHolder.description = (TextView) convertView.findViewById(R.id.textViewDescription);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAccountNumber);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(workOrder.getMember_consumer_owner().getName());
        viewHolder.date.setText(DateTimeConverter.forNotificationDateString(workOrder.getDate_assigned()));
        viewHolder.description.setText(workOrder.getDescription());
        viewHolder.address.setText(workOrder.getMember_consumer_owner().getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}