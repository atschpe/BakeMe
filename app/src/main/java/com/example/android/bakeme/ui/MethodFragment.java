package com.example.android.bakeme.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakeme.R;
import com.example.android.bakeme.data.Recipe;
import com.example.android.bakeme.data.Recipe.Steps;
import com.example.android.bakeme.utils.RecipeUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link MethodFragment} is a {@link Fragment} showing the detailed step in question of the current
 * recipe selected.
 */
public class MethodFragment extends Fragment implements ExoPlayer.EventListener {

    //description views
    @BindView(R.id.nav_prev_bt)
    ImageButton navPrevBt;
    @BindView(R.id.nav_next_bt)
    ImageButton navNextBt;
    @BindView(R.id.recipe_step_tv)
    TextView recipeStep;

    //For the Video player
    @BindView(R.id.exo_play)
    SimpleExoPlayerView exoPlayerView;
    @BindView(R.id.video_thumbnail_iv)
    ImageView videoThumbnailIv;

    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat videoSession;
    private PlaybackStateCompat.Builder stateBuilder;
    long playerCurrentPosition; // position handling: https://stackoverflow.com/a/45482017/7601437
    private static final String TAG = MethodFragment.class.getSimpleName();
    //private Handler handler;

    //passed on data & setters to do so.
    private Recipe recipe;
    private Steps step;
    private ArrayList<Steps> stepsList;

    // check whether device is landscape mode (single pane)
    private boolean landMode;
    private final static String CURRENT_POSITION = "current player position";

    public void setStepsList(ArrayList<Steps> stepsList) {
        this.stepsList = stepsList;
    }

    public void setStep(Steps step) {
        this.step = step;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    public MethodFragment() { //empty constructor as needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_method, container, false);
        ButterKnife.bind(this, root);

        landMode = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                && !getResources().getBoolean(R.bool.isTablet);

        playerCurrentPosition = C.TIME_UNSET;
        ;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RecipeUtils.STEP_LIST)) {
                stepsList = savedInstanceState.getParcelableArrayList(String.valueOf(
                        RecipeUtils.STEP_LIST));
            }
            if (savedInstanceState.containsKey(RecipeUtils.SELECTED_STEP)) {
                step = savedInstanceState.getParcelable(String.valueOf(RecipeUtils.SELECTED_STEP));
            }
            if (savedInstanceState.containsKey(RecipeUtils.SELECTED_RECIPE)) {
                recipe = savedInstanceState.getParcelable(String.valueOf(
                        RecipeUtils.SELECTED_RECIPE));
            }
            if (savedInstanceState.containsKey(CURRENT_POSITION)) {
                playerCurrentPosition = savedInstanceState.getLong(CURRENT_POSITION);
            }
        }

        if (!landMode) {
            updateStepText();
        }

        //handler = new Handler();

        String thumbnail = step.getThumbnail();
        //if there are not thumbnails set image to null so app icon is shown
        assert thumbnail != null;
        if (thumbnail.isEmpty()) thumbnail = null;

        Picasso.with(getActivity()).load(thumbnail)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(videoThumbnailIv);

        // Initialize the Media Session to display the video.
        initializeVideoSession();

        // Initialize the player.
        initializePlayer();

        if (landMode) {
            Toast.makeText(getActivity(), R.string.landscape_instruction, Toast.LENGTH_SHORT)
                    .show();
            setLandMode();
        } else {
            setPortMode();
        }

        container.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeLeft() {
                if (step.getId() == 0) {
                    Toast.makeText(getActivity(), R.string.swiper_no_prev_toast, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    goToPrevStep();
                    startChosenVideo();

                    if (landMode) { //to help the user stay oriented when swiping through the videos
                        Toast.makeText(getActivity(), step.getShortDescription(),
                                Toast.LENGTH_SHORT)
                                .show();
                        setLandMode();
                    }
                }
            }

            public void onSwipeRight() {
                if (stepsList.size() == step.getId() + 1) {
                    Toast.makeText(getActivity(), R.string.swiper_last_step_toast,
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    goToNextStep();
                    startChosenVideo();

                    if (landMode) { //to help the user stay oriented when swiping through the videos
                        Toast.makeText(getActivity(), step.getShortDescription(),
                                Toast.LENGTH_SHORT)
                                .show();
                        setLandMode();
                    }
                }
            }
        });

        return root;
    }

    private void setPortMode() {
        navNextBt.setVisibility(View.VISIBLE);
        navPrevBt.setVisibility(View.VISIBLE);
        recipeStep.setVisibility(View.VISIBLE);
    }

    // In landscape mode, ensure text and buttons remain hidden.
    private void setLandMode() {
        navNextBt.setVisibility(View.GONE);
        navPrevBt.setVisibility(View.GONE);
        recipeStep.setVisibility(View.GONE);
    }

    private void goToPrevStep() {
        long stepId = step.getId() + 1;
        Steps nextStep = recipe.getSteps().get((int) stepId);
        setStep(nextStep);
        updateStepText();
        // Stop previous playback, prepare and play new
        startChosenVideo();
        updateNavButtons();
    }

    private void goToNextStep() {
        long stepId = step.getId() - 1;
        Steps prevStep = recipe.getSteps().get((int) stepId);
        setStep(prevStep);
        updateStepText();
        // Stop previous playback, prepare and play new
        startChosenVideo();
        updateNavButtons();
    }

    //Ensure that the previous and next button only show when there is something to navigate to
    private void updateNavButtons() {
        if (step.getId() == 0) {
            navPrevBt.setVisibility(View.INVISIBLE);
        } else if (stepsList.size() == step.getId() + 1) {
            navNextBt.setVisibility(View.INVISIBLE);
        } else {
            navPrevBt.setVisibility(View.VISIBLE);
            navNextBt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(String.valueOf(RecipeUtils.STEP_LIST), stepsList);
        outState.putParcelable(String.valueOf(RecipeUtils.SELECTED_RECIPE), recipe);
        outState.putParcelable(String.valueOf(RecipeUtils.SELECTED_STEP), step);
        outState.putLong(CURRENT_POSITION, playerCurrentPosition);
        super.onSaveInstanceState(outState);
    }

    private void startChosenVideo() {
        exoPlayer.stop();
        if (!step.getVideo().isEmpty()) {
            MediaSource mediaSource = getMediaSource();
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } else {
            videoThumbnailIv.setVisibility(View.VISIBLE);
        }

    }

    @NonNull
    private MediaSource getMediaSource() {
        String userAgent = Util.getUserAgent(getActivity(), "BakeMe");
        return new ExtractorMediaSource(Uri.parse(step.getVideo()),
                new DefaultDataSourceFactory(getActivity(), userAgent),
                new DefaultExtractorsFactory(), null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoSession.setActive(false);
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        playerCurrentPosition = exoPlayer.getCurrentPosition();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer();
    }

    //Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
    //and media controller.
    private void initializeVideoSession() {

        // Create a MediaSessionCompat for the videos to be viewed.
        videoSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable mediaButton ~ and transportControls callbacks
        videoSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        videoSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        videoSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        videoSession.setCallback(new MethodFragment.MySessionCallback());

        // Start the Media Session since the activity is active.
        videoSession.setActive(true);
    }

    //Initialize ExoPlayer.
    private void initializePlayer() {
        if (exoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelection.Factory videoTrackSelectionFactory
                    = new AdaptiveVideoTrackSelection.Factory(new DefaultBandwidthMeter());
            DefaultTrackSelector trackSelector
                    = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector,
                    loadControl);
            exoPlayerView.requestFocus();
            exoPlayerView.setPlayer(exoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            exoPlayer.addListener(this);

            // Prepare the MediaSource and start playing
            if (!step.getVideo().isEmpty()) {
                exoPlayer.prepare(getMediaSource());
                exoPlayer.setPlayWhenReady(true);
                videoThumbnailIv.setVisibility(View.INVISIBLE);
            } else {
                videoThumbnailIv.setVisibility(View.VISIBLE);
            }
        }
    }

    // ExoPlayer Event Listeners
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
            videoThumbnailIv.setVisibility(View.INVISIBLE); //hide thumbnail to play
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
            videoThumbnailIv.setVisibility(View.VISIBLE); // show thumbnail when paused
        }
        videoSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    //Media Session Callbacks, where all external clients control the player.
    class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }

    /**
     * {@link MediaButtonClickReceiver} is a {@link BroadcastReceiver} which will receive the
     * MEDIA_BUTTON intent from clients.
     */
    public class MediaButtonClickReceiver extends BroadcastReceiver {

        public MediaButtonClickReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(videoSession, intent);
        }
    }

    //Release ExoPlayer when it is no longer needed.
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void updateStepText() {
        if (!step.getDescription().isEmpty()) {
            recipeStep.setText(step.getDescription());
        } else {
            recipeStep.setText(R.string.no_detail_step_available);
        }
    }

    //track swipe gestures for quick&easy navigation. See: https://stackoverflow.com/a/12938787
    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int THRESHOLD = 100;
            private static final int VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) { // not needed
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > THRESHOLD && Math.abs(velocityX) >
                                VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        void onSwipeRight() {
        }

        void onSwipeLeft() {
        }

    }
}