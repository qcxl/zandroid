package com.zcj.android.view.pathchoose;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zcj.android.R;
import com.zcj.util.filenameutils.FilenameUtils;

/**
 * 路径选择弹出窗的内容列表适配器
 * 
 * @author yeguozhong@yeah.net
 * 
 */
public class PathChooseAdapter extends BaseAdapter {

	private List<String> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	private OnPathOperateListener listener;

	public interface OnPathOperateListener {

		public final static int DEL = 0;
		public final static int RENAME = 1;

		public void onPathOperate(int type, int position, TextView pathName);
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public PathChooseAdapter(Context context, List<String> data, int resource, OnPathOperateListener listener) {
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.listener = listener;
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = listContainer.inflate(itemViewResource, null);
		}

		LinearLayout llOp = (LinearLayout) convertView.findViewById(R.id.ll_op);
		llOp.setVisibility(View.GONE);

		final TextView tvPath = (TextView) convertView.findViewById(R.id.tvPath);
		tvPath.setText(FilenameUtils.getName(listItems.get(position)));
		Button btnDel = (Button) convertView.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onPathOperate(OnPathOperateListener.DEL, position, tvPath);
			}
		});
		Button btnRename = (Button) convertView.findViewById(R.id.btn_rename);
		btnRename.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onPathOperate(OnPathOperateListener.RENAME, position, tvPath);
			}
		});
		return convertView;
	}

}
