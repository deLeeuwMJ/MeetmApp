package com.jaysonm.meetm.controllers.home;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.contacts.FollowedUserIdFetcher;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.util.MapUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;
import rm.com.longpresspopup.PopupStateListener;

public class CrossedUserAdapter extends PagerAdapter implements View.OnClickListener, PopupInflaterListener {

    private List<User> dataSet;
    private LayoutInflater layoutInflater;
    private MapInteractionListener mapInteractionListener;
    private Context context;

    public CrossedUserAdapter(List<User> list, Context context, MapInteractionListener mapInteractionListener) {
        this.dataSet = list;
        this.context = context;
        this.mapInteractionListener = mapInteractionListener;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.crossed_user_item, container, false);

        ImageView imageView;
        MaterialTextView name;
        MaterialButton followButton;

        imageView = view.findViewById(R.id.user_card_image);
        name = view.findViewById(R.id.crossed_user_title);
        followButton = view.findViewById(R.id.crossed_user_follow_button);

        Picasso.get().load(Uri.parse(dataSet.get(i).getPhotoUri())).into(imageView);
        name.setText(dataSet.get(i).getName());

        List<String> userFollowingId = FollowedUserIdFetcher.getInstance().getList();

        //update button if user followed user already
        if (userFollowingId.contains(dataSet.get(i).getUserId())) {
            followButton.setText(context.getResources().getString(R.string.user_following));
        } else {
            followButton.setText(context.getResources().getString(R.string.user_follow));
        }

        //saves followed userId in database
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               handleFollowing(followButton,userFollowingId,i);
            }
        });

        LongPressPopup popup = new LongPressPopupBuilder(context)
                .setTarget(imageView)
                .setPopupView(R.layout.profile_quickview, CrossedUserAdapter.this)
                .setDismissOnLongPressStop(false)
                .setLongPressReleaseListener(CrossedUserAdapter.this)
                .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER)
                .setTag(dataSet.get(i).getEmail())
                .build();

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapInteractionListener.moveCamera(MapUtil.getLatLngFromGeoLocation(dataSet.get(i).getGeoLocation()));
                popup.showNow();
                return false;
            }
        });

        container.addView(view, 0);
        return view;
    }

    private void handleFollowing(MaterialButton followButton, List<String> userFollowingId, int i){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //adding to follower list
        DatabaseReference userFollowingRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/following/" + dataSet.get(i).getUserId());
        DatabaseReference destFollowingRef = FirebaseDatabase.getInstance().getReference("members/" + dataSet.get(i).getUserId() + "/following/" + user.getUid());

        //adding chat
        DatabaseReference userChatRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/chats/" + dataSet.get(i).getUserId());
        DatabaseReference destChatRef = FirebaseDatabase.getInstance().getReference("members/" + dataSet.get(i).getUserId() + "/chats/" + user.getUid());

        if (!userFollowingId.contains(dataSet.get(i).getUserId())) {
            followButton.setText("Following");

            //add db followed
            userFollowingRef.setValue(true);
            destFollowingRef.setValue(true);

            //add db chat id
            String roomId = dataSet.get(i).getUserId().substring(7) + user.getUid().substring(7);
            userChatRef.setValue(roomId);
            destChatRef.setValue(roomId);
        } else {
            followButton.setText("Follow");
            userFollowingRef.removeValue();
            destFollowingRef.removeValue();
            userChatRef.removeValue();
            destChatRef.removeValue();
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onViewInflated(@Nullable String popupTag, View root) {

        User selectedUser = getSelectedUser(popupTag);

        if (selectedUser == null)
            return;

        CircleImageView profileImage = root.findViewById(R.id.profile_user_image);
        MaterialTextView name = root.findViewById(R.id.profile_name);
        MaterialTextView location = root.findViewById(R.id.profile_user_location);

        MaterialTextView email = root.findViewById(R.id.profile_email);
        MaterialTextView aboutMe = root.findViewById(R.id.profile_about_me);

        MaterialTextView job = root.findViewById(R.id.profile_job);
        MaterialTextView birthday = root.findViewById(R.id.profile_birthday);
        MaterialTextView phoneNumber = root.findViewById(R.id.profile_phone_number);

        Picasso.get().load(selectedUser.getPhotoUri()).into(profileImage);
        name.setText(selectedUser.getName());
        location.setText(selectedUser.getLocation());

        email.setText(selectedUser.getEmail());
        aboutMe.setText(selectedUser.getAboutMe());

        job.setText(selectedUser.getJob());
        birthday.setText(selectedUser.getBirthDay());
        phoneNumber.setText(selectedUser.getPhoneNumber());
    }

    private User getSelectedUser(String popupTag) {
        for (User user: dataSet){
            if (user.getEmail().equals(popupTag)){
                return user;
            }
        }

        return null;
    }

    @Override
    public void onClick(View v) {

    }
}