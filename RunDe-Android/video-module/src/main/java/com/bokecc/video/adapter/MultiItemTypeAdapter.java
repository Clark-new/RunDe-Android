package com.bokecc.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.video.adapter.base.ItemViewDelegate;
import com.bokecc.video.adapter.base.ItemViewDelegateManager;
import com.bokecc.video.adapter.base.ViewHolder;

import java.util.List;


public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context mContext;

    private List<T> mDataList;

    private ItemViewDelegateManager mItemViewDelegateManager;

    private OnItemClickListener mOnItemClickListener;


    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDataList = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }


    @Override
    public int getItemViewType(int position) {

        if (!useItemViewDelegateManager())
            return super.getItemViewType(position);

        return mItemViewDelegateManager.getItemViewType(mDataList.get(position), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //得到viewType类型的view代理
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);

        //从item代理获取layoutId
        int layoutId = itemViewDelegate.getItemViewLayoutId();

        //创建一个ViewHolder
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);

        onViewHolderCreated(holder, holder.getConvertView());

        setListener(parent, holder, viewType);

        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {
    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {

        if (!isEnabled(viewType))
            return;

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {

                    int position = viewHolder.getAdapterPosition();

                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = mDataList.size();
        return itemCount;
    }


    public List<T> getDataList() {
        return mDataList;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {

        mItemViewDelegateManager.addDelegate(itemViewDelegate);

        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {

        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);

        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
