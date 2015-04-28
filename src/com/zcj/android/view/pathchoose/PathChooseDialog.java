package com.zcj.android.view.pathchoose;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zcj.android.R;
import com.zcj.android.util.UtilAppFile;
import com.zcj.android.view.pathchoose.PathChooseAdapter.OnPathOperateListener;
import com.zcj.util.UtilFile;
import com.zcj.util.UtilString;
import com.zcj.util.filenameutils.FilenameUtils;

/**
 * 路径选择弹
 * 
 * @author yeguozhong@yeah.net
 * 
 */
public class PathChooseDialog extends Dialog {

	private ListView lv;
	private Button btnComfirm;
	private Button btnBack;
	private Button btnNew;

	private TextView tvCurPath;

	private Context ctx;

	private List<String> data;
	private ListAdapter listAdapter;

	private ChooseCompleteListener listener;

	private Stack<String> pathStack = new Stack<String>();

	private int firstIndex = 0;

	private View lastSelectItem; // 上一个长按操作的View

	// 监听操作事件
	private OnPathOperateListener pListener = new OnPathOperateListener() {
		@Override
		public void onPathOperate(int type, final int position, final TextView pathName) {
			if (type == OnPathOperateListener.DEL) {
				String path = data.get(position);
				int rs = UtilFile.deleteBlankPath(path);
				if (rs == 0) {
					data.remove(position);
					refleshListView(data, firstIndex);
					Toast.makeText(ctx, "删除成功", Toast.LENGTH_SHORT).show();
				} else if (rs == 1) {
					Toast.makeText(ctx, "没有权限", Toast.LENGTH_SHORT).show();
				} else if (rs == 2) {
					Toast.makeText(ctx, "不能删除非空目录", Toast.LENGTH_SHORT).show();
				}

			} else if (type == OnPathOperateListener.RENAME) {
				final EditText et = new EditText(ctx);
				et.setText(FilenameUtils.getName(data.get(position)));
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle("重命名");
				builder.setView(et);
				builder.setCancelable(true);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String input = et.getText().toString();
						if (UtilString.isBlank(input)) {
							Toast.makeText(ctx, "输入不能为空", Toast.LENGTH_SHORT).show();
						} else {
							String newPath = pathStack.peek() + File.separator + input;
							boolean rs = UtilFile.reNamePath(data.get(position), newPath);
							if (rs == true) {
								pathName.setText(input);
								data.set(position, newPath);
								Toast.makeText(ctx, "重命名成功", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(ctx, "重命名失败", Toast.LENGTH_SHORT).show();
							}
						}
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		}
	};

	public interface ChooseCompleteListener {
		void onComplete(String finalPath);
	}

	public PathChooseDialog(Context context, ChooseCompleteListener listener) {
		super(context);
		this.ctx = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zandroid_pathchoose_main);
		setCanceledOnTouchOutside(true);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		lv = (ListView) findViewById(android.R.id.list);
		btnComfirm = (Button) findViewById(R.id.btn_comfirm);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnNew = (Button) findViewById(R.id.btn_new);
		tvCurPath = (TextView) findViewById(R.id.tv_cur_path);

		// 获得内置SD卡的根路径
		String rootPath = null;
		if (UtilAppFile.checkExternalSDExists()) {
			rootPath = "/storage";
			data = new ArrayList<String>();
			data.add(Environment.getExternalStorageDirectory().getAbsolutePath());
			data.add(UtilAppFile.getExternalSDRoot());
		} else {
			rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			data = UtilFile.listPath(rootPath);
		}

		tvCurPath.setText(rootPath);

		pathStack.add(rootPath);

		refleshListView(data, 0);
		// 单击
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				firstIndex = position;
				String currentPath = data.get(position);
				tvCurPath.setText(currentPath);
				data = UtilFile.listPath(currentPath);
				pathStack.add(currentPath);
				refleshListView(data, pathStack.size() - 1);
			}
		});
		// 长按
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (lastSelectItem != null && !lastSelectItem.equals(view)) {
					lastSelectItem.findViewById(R.id.ll_op).setVisibility(View.GONE);
				}
				LinearLayout llOp = (LinearLayout) view.findViewById(R.id.ll_op);
				int visible = llOp.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
				llOp.setVisibility(visible);
				lastSelectItem = view;
				return true;
			}
		});

		// 确认
		btnComfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onComplete(pathStack.peek());
				dismiss();
			}
		});

		// 后退
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pathStack.size() >= 2) {
					pathStack.pop();
					data = UtilFile.listPath(pathStack.peek());
					tvCurPath.setText(pathStack.peek());
					refleshListView(data, firstIndex);
				}
			}
		});

		// 新建
		btnNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText et = new EditText(ctx);
				et.setText("新建文件夹");
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle("新建文件夹");
				builder.setView(et);
				builder.setCancelable(true);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String rs = et.getText().toString();
						if (UtilString.isBlank(rs)) {
							Toast.makeText(ctx, "输入不能为空", Toast.LENGTH_SHORT).show();
						} else {
							String newPath = pathStack.peek() + File.separator + rs;
							int status = UtilFile.createPath(newPath);
							switch (status) {
							case 1:
								data.add(newPath);
								refleshListView(data, data.size() - 1);
								Toast.makeText(ctx, "创建成功", Toast.LENGTH_SHORT).show();
								break;
							case 0:
								Toast.makeText(ctx, "创建失败", Toast.LENGTH_SHORT).show();
								break;
							case 2:
								Toast.makeText(ctx, "文件名重复", Toast.LENGTH_SHORT).show();
								break;
							}
						}
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}

	/**
	 * 更新listView视图
	 * 
	 * @param data
	 */
	private void refleshListView(List<String> data, int firstItem) {
		String lost = Environment.getExternalStorageDirectory().getAbsolutePath() + "lost+found";
		data.remove(lost);
		listAdapter = new PathChooseAdapter(ctx, data, R.layout.zandroid_pathchoose_listitem, pListener);
		lv.setAdapter(listAdapter);
		lv.setSelection(firstItem);
	}
}
