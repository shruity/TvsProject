package com.tvsproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<UserModel> userModelList = null;
    private List<UserModel> orig;
    private ListDetailsInterface listDetailsInterface;
    private String searchString = "";

    UserListAdapter(Context context, List<UserModel> userModelList, ListDetailsInterface listDetailsInterface) {
        this.userModelList = userModelList;
        this.context = context;
        this.listDetailsInterface = listDetailsInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final List<UserModel> user = userModelList;
        holder.setIsRecyclable(false);

        if (user.get(position).getName() != null && !user.get(position).getName().isEmpty() && !user.get(position).getName().equals("")){
            holder.tvUsername.setText(user.get(position).getName());
            holder.tvUsername.setText(highlightText(searchString,user.get(position).getName()));
        }

        if (user.get(position).getDesignation() != null && !user.get(position).getDesignation().isEmpty()
                && !user.get(position).getDesignation().equals("")){
            holder.tvDesignation.setText(user.get(position).getDesignation());
        }

        if (user.get(position).getCity() != null && !user.get(position).getCity().isEmpty()
                && !user.get(position).getCity().equals("")){
            holder.tvCity.setText(user.get(position).getCity());
        }

        if (user.get(position).getSalary() != null && !user.get(position).getSalary().isEmpty()
                && !user.get(position).getSalary().equals("")){
            holder.tvSalary.setText(user.get(position).getSalary());
        }

        holder.llData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDetailsInterface.listDetails(user.get(position));
            }
        });

    }
    @NonNull
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                searchString = (String) constraint;
                final FilterResults oReturn = new FilterResults();
                final List<UserModel> results = new ArrayList<>();

                if (!TextUtils.isEmpty(constraint)) {
                    if (orig != null && orig.size() > 0) {
                        for (final UserModel g : orig) {
                            if (g.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                results.add(g);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                else {
                    oReturn.values = orig;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userModelList = (ArrayList<UserModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUsername, tvDesignation, tvCity, tvSalary;
        LinearLayout llData;

        MyViewHolder(View itemView) {
            super(itemView);
            tvUsername      = itemView.findViewById(R.id.tvUsername);
            tvDesignation   = itemView.findViewById(R.id.tvDesignation);
            tvCity          = itemView.findViewById(R.id.tvCity);
            tvSalary        = itemView.findViewById(R.id.tvSalary);
            llData          = itemView.findViewById(R.id.llData);
        }
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    void updateList(List<UserModel> list) {
        userModelList = list;
        notifyDataSetChanged();
    }

    private CharSequence highlightText(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            int start = normalizedText.indexOf(search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }



}
