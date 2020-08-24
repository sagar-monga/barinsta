package awais.instagrabber.adapters.viewholder.feed;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import org.json.JSONObject;

import awais.instagrabber.R;
import awais.instagrabber.customviews.CommentMentionClickSpan;
import awais.instagrabber.customviews.RamboTextView;
import awais.instagrabber.databinding.ItemFeedBottomBinding;
import awais.instagrabber.databinding.ItemFeedTopBinding;
import awais.instagrabber.interfaces.MentionClickListener;
import awais.instagrabber.models.FeedModel;
import awais.instagrabber.models.ProfileModel;
import awais.instagrabber.utils.Utils;

public abstract class FeedItemViewHolder extends RecyclerView.ViewHolder {
    public static final int MAX_CHARS = 255;
    private final ItemFeedTopBinding topBinding;
    private final ItemFeedBottomBinding bottomBinding;
    private final MentionClickListener mentionClickListener;

    boolean captionExpanded = false;

    public FeedItemViewHolder(@NonNull final View root,
                              final ItemFeedTopBinding topBinding,
                              final ItemFeedBottomBinding bottomBinding,
                              final MentionClickListener mentionClickListener,
                              final View.OnClickListener clickListener,
                              final View.OnLongClickListener longClickListener) {
        super(root);
        this.topBinding = topBinding;
        this.bottomBinding = bottomBinding;
        this.mentionClickListener = mentionClickListener;
        topBinding.title.setMovementMethod(new LinkMovementMethod());
        bottomBinding.btnComments.setOnClickListener(clickListener);
        topBinding.viewStoryPost.setOnClickListener(clickListener);
        topBinding.ivProfilePic.setOnClickListener(clickListener);
        bottomBinding.btnDownload.setOnClickListener(clickListener);
        bottomBinding.viewerCaption.setOnClickListener(clickListener);
        bottomBinding.viewerCaption.setOnLongClickListener(longClickListener);
        bottomBinding.viewerCaption.setMentionClickListener(mentionClickListener);
    }

    public void bind(final FeedModel feedModel) {
        if (feedModel == null) {
            return;
        }
        topBinding.viewStoryPost.setTag(feedModel);
        topBinding.ivProfilePic.setTag(feedModel);
        bottomBinding.btnDownload.setTag(feedModel);
        bottomBinding.viewerCaption.setTag(feedModel);
        bottomBinding.btnComments.setTag(feedModel);
        final ProfileModel profileModel = feedModel.getProfileModel();
        if (profileModel != null) {
            // glide.load(profileModel.getSdProfilePic())
            //         .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            //         .into(topBinding.ivProfilePic);
            topBinding.ivProfilePic.setImageURI(profileModel.getSdProfilePic());
            final int titleLen = profileModel.getUsername().length() + 1;
            final SpannableString spannableString = new SpannableString("@" + profileModel.getUsername());
            spannableString.setSpan(new CommentMentionClickSpan(), 0, titleLen, 0);
            topBinding.title.setText(spannableString);
            topBinding.title.setMentionClickListener((view, text, isHashtag) -> mentionClickListener.onClick(null, profileModel.getUsername(), false));
        }
        bottomBinding.tvPostDate.setText(feedModel.getPostDate());
        final long commentsCount = feedModel.getCommentsCount();
        bottomBinding.commentsCount.setText(String.valueOf(commentsCount));

        final JSONObject location = feedModel.getLocation();
        setLocation(location);
        CharSequence postCaption = feedModel.getPostCaption();
        final boolean captionEmpty = Utils.isEmpty(postCaption);
        bottomBinding.viewerCaption.setVisibility(captionEmpty ? View.GONE : View.VISIBLE);
        if (!captionEmpty) {
            if (Utils.hasMentions(postCaption)) {
                postCaption = Utils.getMentionText(postCaption);
                feedModel.setPostCaption(postCaption);
                bottomBinding.viewerCaption.setText(postCaption, TextView.BufferType.SPANNABLE);
            } else {
                bottomBinding.viewerCaption.setText(postCaption);
            }
        }
        expandCollapseTextView(bottomBinding.viewerCaption, feedModel.getPostCaption());
        bindItem(feedModel);
    }

    private void setLocation(final JSONObject location) {
        if (location == null) {
            topBinding.location.setVisibility(View.GONE);
            topBinding.title.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
            ));
        } else {
            topBinding.location.setVisibility(View.VISIBLE);
            topBinding.location.setText(location.optString("name"));
            topBinding.title.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            ));
            topBinding.location.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext()).setTitle(location.optString("name"))
                                                       .setMessage(R.string.comment_view_mention_location_search)
                                                       .setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok,
                        (dialog, which) -> mentionClickListener.onClick(null, location.optString("id") + "/" + location.optString("slug"), false)).show();
            });
        }
    }

    /**
     * expands or collapses {@link RamboTextView} [stg idek why i wrote this documentation]
     *
     * @param textView the {@link RamboTextView} view, to expand and collapse
     * @param caption
     * @return isExpanded
     */
    public static boolean expandCollapseTextView(@NonNull final RamboTextView textView, final CharSequence caption) {
        if (Utils.isEmpty(caption)) return false;

        final TextView.BufferType bufferType = caption instanceof Spanned ? TextView.BufferType.SPANNABLE : TextView.BufferType.NORMAL;

        if (!textView.isCaptionExpanded()) {
            int i = Utils.indexOfChar(caption, '\r', 0);
            if (i == -1) i = Utils.indexOfChar(caption, '\n', 0);
            if (i == -1) i = MAX_CHARS;

            final int captionLen = caption.length();
            final int minTrim = Math.min(MAX_CHARS, i);
            if (captionLen <= minTrim) return false;

            if (Utils.hasMentions(caption))
                textView.setText(Utils.getMentionText(caption), TextView.BufferType.SPANNABLE);
            textView.setCaptionIsExpandable(true);
            textView.setCaptionIsExpanded(true);
        } else {
            textView.setText(caption, bufferType);
            textView.setCaptionIsExpanded(false);
        }
        return true;
    }

    public abstract void bindItem(final FeedModel feedModel);
}