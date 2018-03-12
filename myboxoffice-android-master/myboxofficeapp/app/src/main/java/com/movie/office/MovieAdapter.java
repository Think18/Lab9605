package com.movie.office;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by akhilanand on 02/03/18.
 */

public class MovieAdapter extends BaseAdapter {

    private Context mcontext;
    private List<MovieItems> mProductList;

    public MovieAdapter(Context mcontext, List<MovieItems> mProductList) {
        this.mcontext = mcontext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItems movieItems = mProductList.get(position);
        View v =View.inflate(mcontext, com.movie.office.R.layout.movie_content,null);
        TextView id=(TextView)v.findViewById(com.movie.office.R.id.second);
        TextView id1=(TextView)v.findViewById(com.movie.office.R.id.Ticket);
        TextView id2=(TextView)v.findViewById(com.movie.office.R.id.Date);
        ImageView id3=(ImageView)v.findViewById(com.movie.office.R.id.QrCode);

        StringBuilder movieTicket = new StringBuilder();
        StringBuilder movieTicket1 = new StringBuilder();
        StringBuilder movieTicket2 = new StringBuilder();

        Glide.with(mcontext).load(movieItems.getUrl()).into(id3);
        movieTicket.append(movieItems.getName());
        movieTicket1.append(movieItems.getNumberOfTickets());
        movieTicket2.append(movieItems.getDate());
//        ImageView img=(ImageView)v.findViewById(R.id.QrCode);
//        GlideBuilder image=new GlideBuilder();
//        image.setBitmapPool(movieItems.get)

        id.setText(movieTicket.toString());
        id1.setText(movieTicket1.toString());
        id2.setText(movieTicket2.toString());


        return v;
    }
}
