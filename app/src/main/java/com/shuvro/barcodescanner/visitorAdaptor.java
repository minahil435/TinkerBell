package com.shuvro.barcodescanner;

/**
 * Created by Admin on 2/13/2018.
 */

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import static com.shuvro.barcodescanner.R.id.logo;

/**
 * Created by Admin on 7/21/2017.
 */


public class visitorAdaptor extends ArrayAdapter<kepingVisitorsRecord>
{


    public visitorAdaptor(Context context, ArrayList<kepingVisitorsRecord> words) {
        super(context, 0, words);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.visitorlist, parent, false);
        }




        // Get the {@link AndroidFlavor} object located at this position in the list
        kepingVisitorsRecord currentword = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.VisitorName);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentword.getTime());
        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView numberTextView = (TextView) listItemView.findViewById(R.id.Date);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        numberTextView.setText(currentword.getVisitor());




        ImageView iconView = (ImageView) listItemView.findViewById(logo);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView


            // If an image is available, display the provided image based on the resource ID

           // iconView.setImageResource(
                  String uri = currentword.getUri();
                  Uri myUri = Uri.parse(uri);
        Picasso.with(getContext()).load(myUri).fit().centerCrop().into(iconView);
            // Make sure the view is visible






        return listItemView;
    }


}

