package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liguang.imagesimilar.R;
import com.tonicartos.superslim.GridSLM;

import java.util.ArrayList;
import java.util.List;

import tool.LocalImage;

/**
 *
 */
public class SimilarPictureAdapter extends RecyclerView.Adapter<CountryViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int LINEAR = 0;

    private final ArrayList<LineItem> mItems;

    private int mHeaderDisplay;

    private boolean mMarginsFixed;
    private boolean mTextVisible;

    private final Context mContext;
    List<List<LocalImage>> similarItems;

    public SimilarPictureAdapter(Context context, int headerMode, List<List<LocalImage>> similarItem) {
        mContext = context;
        this.similarItems = similarItem;

        mHeaderDisplay = headerMode;

        mItems = new ArrayList<>();

        //Insert headers into list of items.
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;


        for (int i = 0; i < similarItems.size(); i++) {
            String header = String.valueOf(i);
            mItems.add(new LineItem(header, true, sectionManager, headerCount + sectionFirstPosition));
            for (LocalImage lg : similarItems.get(i)) {
                LineItem lineItem = new LineItem(lg.getFilePath(), false, sectionManager, headerCount + sectionFirstPosition);
                lineItem.otherInfo = lg.getAvgPixel() + "\n" + lg.getSourceHashCode();
                mItems.add(lineItem);
            }
            headerCount++;
            sectionFirstPosition = sectionFirstPosition + similarItems.get(i).size();
        }
    }

    public boolean isItemHeader(int position) {
        return mItems.get(position).isHeader;
    }

    public String itemToString(int position) {
        return mItems.get(position).text;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_line_item, parent, false);
        }
        return new CountryViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;

        holder.bindItem(item.text, item.isHeader, item.otherInfo);
        if (!item.isHeader) {
            holder.setTextVisible(mTextVisible);
        }

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.isHeader) {
            lp.headerDisplay = mHeaderDisplay;
            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }
//        lp.setSlm(item.sectionManager == LINEAR ? LinearSLM.ID : GridSLM.ID);
        lp.setSlm(GridSLM.ID);
        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            LineItem item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    private static class LineItem {

        public int sectionManager;

        public int sectionFirstPosition;

        public boolean isHeader;

        public String text;

        public String otherInfo;


        public LineItem(String text, boolean isHeader, int sectionManager,
                        int sectionFirstPosition) {
            this.isHeader = isHeader;
            this.text = text;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
        }
    }

    public boolean ismTextVisible() {
        return mTextVisible;
    }

    public void setmTextVisible(boolean mTextVisible) {
        this.mTextVisible = mTextVisible;
        notifyDataSetChanged();
    }
}
