package com.minus.diary;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minius.common.CommonBitmap;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.enity.DiaryListViewEnity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.RTPullListView;
import com.minus.sql_interface.Database;
import com.minus.table.DiaryTable;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.AlbumPacketHandler;
import com.minus.xsocket.handler.DiaryPacketHandler;

public class DiaryActivity extends BroadCast {

	// handler
	private static final int HANDLE_RESPON = 1;
	private static final int RETRUEN_READLASTMODIFYTIME = 2;

	String userAccount = "";
	String mySmallName = "";
	String tarSmallName = "";

	private Timer connectTimer = null;
	private Bitmap tarHeadLayerDrawable = null;
	private Bitmap myHeadLayerDrawable = null;
	// ��ȡSQLite�е�����

	// private List<DiaryTable> diarylist = null;
	private List<String> refreshList = null;

	// �������
	private ImageView Back_View = null;
	private ImageView Write_View = null;
	private RelativeLayout dairyMainRL = null;

	View Current_view = null;

	// ȫ�ֱ���
	// private List<DiaryListViewEnity> mDataArrays = new
	// ArrayList<DiaryListViewEnity>();
	private MyAdapter mAdapter;

	private RTPullListView mListview = null;
	Database db = null;

	// private SharedPreferences preferences = null;

	// ViewHolder��������
	private class ViewHolder {
		private int pos = 0;
		private ImageView headphoto;
		private TextView name; // small name or bigname
		private TextView title;
		private TextView content;
		private ImageView New;
		private TextView date;

		private boolean isSelected;
		private boolean isComing = true;
		private String initdate;
		private String account = "";

		// private ImageView CommentButton;
		private ImageView ModiferButton;
		private ImageView DeleteButton;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_view);
		if (receiver == null)
			receiver = new MyReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Protocol.ACTION_DIARYPACKET);
		filter.addAction(Protocol.ACTION_DIARYPACKET_READLASTMODIFYTIME);
		filter.addAction(Protocol.ACTION_DIARYPACKET_ONERECORD);
		filter.addAction(Protocol.ACTION_DIARYPACKET_REFRESH);
		this.registerReceiver(receiver, filter);

		dairyMainRL = (RelativeLayout) findViewById(R.id.diary_main);
		Back_View = (ImageView) findViewById(R.id.imageview_diaryback);
		Write_View = (ImageView) findViewById(R.id.imageview_diarywrite);

		mListview = (RTPullListView) findViewById(R.id.diary_list);
		mListview.setSelector(android.R.color.transparent);

		// preferences = getSharedPreferences("LoverHouse_Diary",
		// Activity.MODE_PRIVATE);

		// init common info
		SelfInfo mSelf = SelfInfo.getInstance();
		GlobalApplication mGA = GlobalApplication.getInstance();
		this.userAccount = mSelf.getAccount();

		this.mySmallName = mSelf.getNickName();
		this.tarSmallName = mGA.getTiSmallName();
		if (this.tarSmallName.equals(Protocol.DEFAULT + "")) {
			this.tarSmallName = mGA.getTiBigName();
		}

		// diarylist = new ArrayList<DiaryTable>();

//		initView();

		Back_View.setOnClickListener(new MyOnClickListener(1));
		Write_View.setOnClickListener(new MyOnClickListener(2));

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		if (this.connectTimer != null) {
			this.connectTimer.cancel();
			this.connectTimer = null;
		}

		tarHeadLayerDrawable = null;

		myHeadLayerDrawable = null;

		refreshList = null;
		// �������
		Back_View = null;
		Write_View = null;
		dairyMainRL = null;

		Current_view = null;

		mAdapter = null;
		mListview = null;
		db = null;
		super.onDestroy();
	}

	public void reloadData() {

		mAdapter.listData = db.getDiary();
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}

	}

	private void initViewAndData() {

		db = Database.getInstance(this);
		List<DiaryTable> diarylist = db.getDiary();
		if (diarylist.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}
		mAdapter = new MyAdapter(DiaryActivity.this, diarylist);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long id) {
				ViewHolder holder = (ViewHolder) v.getTag();
				if (holder.isSelected) {
					holder.isSelected = false;
					holder.content.setMaxLines(3);
					if (!(holder.isComing)) {
						holder.ModiferButton.setVisibility(View.INVISIBLE);
						holder.DeleteButton.setVisibility(View.INVISIBLE);
					}
				} else {
					holder.isSelected = true;
					holder.content.setMaxLines(500);
					if (!(holder.isComing)) {
						holder.ModiferButton.setVisibility(View.VISIBLE);
						holder.DeleteButton.setVisibility(View.VISIBLE);
					}
				}
				if (Current_view != null && !Current_view.equals(v)) {
					ViewHolder vh = (ViewHolder) Current_view.getTag();
					vh.isSelected = false;
					vh.content.setMaxLines(3);
					if (!(vh.isComing)) {
						vh.ModiferButton.setVisibility(View.INVISIBLE);
						vh.DeleteButton.setVisibility(View.INVISIBLE);
					}
				}
				if (holder.New.getVisibility() == View.VISIBLE) {
					holder.New.setVisibility(View.GONE);
					String author = holder.account;
					String initdate = holder.initdate;
					db.changeNewDiary(author, initdate, 0);
					DiaryPacketHandler mDP = new DiaryPacketHandler();
					mDP.RemoveDiaryNewSymbol(initdate);
					int pos = holder.pos;
					mAdapter.listData.get(pos).setIsnew(0);
					mAdapter.notifyDataSetChanged();

				}
				Current_view = v;
			}
		});
	}

	@Override
	protected void onResume() {
		GlobalApplication.getInstance().setDiaryVisible(true);
		super.onResume();
		initViewAndData();
		refreshFromServer();

	}

	@Override
	protected void onPause() {
		GlobalApplication.getInstance().setDiaryVisible(false);
		super.onPause();
	}

	private class MyOnClickListener implements View.OnClickListener {

		private int index = 0;

		public MyOnClickListener(int i) {
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			if (this.index == 1) {
				DiaryActivity.this.finish();
			}
			if (this.index == 2) { // �����new �ռ�, if has draft,show it
				Intent intent = new Intent();
				intent.setClass(DiaryActivity.this, EditDiaryActivity.class);
				intent.putExtra("who", 0);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);

			}
			// comment
			if (this.index == 3) {

			}
			// modifer
			if (this.index == 4) {
				ViewHolder holder = (ViewHolder) Current_view.getTag();
				Intent intent = new Intent();
				intent.setClass(DiaryActivity.this, EditDiaryActivity.class);

				String title = holder.title.getText().toString();
				title = title.substring(title.indexOf("��") + 1,
						title.indexOf("��"));
				String content = holder.content.getText().toString();
				String date = holder.initdate;

				intent.putExtra("who", 1);
				intent.putExtra("DiaryIniDate", date);
				intent.putExtra("DiaryTitle", title);
				intent.putExtra("DiaryArticle", content);

				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);

			}
			// Delete
			if (this.index == 5) {
				showDeleteAlert();

			}
		}
	}

	public void showDeleteAlert() {
		myDialog = new AlertDialog.Builder(DiaryActivity.this).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.delete_diary);
		Button cancelBtn = (Button) myDialog.getWindow().findViewById(
				R.id.diarydelete_no);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Delete_NO(v);

			}
		});
		Button deleteBtn = (Button) myDialog.getWindow().findViewById(
				R.id.diarydelete_yes);
		deleteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Delete_YES(v);

			}
		});

	}

	// toggle no
	public void Delete_NO(View v) {
		if (myDialog != null) {
			myDialog.dismiss();
			myDialog = null;
		}
	}

	// toggle yes
	public void Delete_YES(View v) {
		ViewHolder holder = (ViewHolder) Current_view.getTag();
		String acc = holder.account;
		String iniDate = holder.initdate;
		int delePos = holder.pos;
		// ���û����ɾ����ʱ��������޸����ݿ���������¼��־ֵ��ֻ�л�÷�����ȷ����Ϣ֮�������ɾ����¼;

		String state = String.format("%c", Protocol.WaitForServerComfirmRemove);
		db.updateStateFromServer(acc, iniDate, state);
		DiaryPacketHandler req = new DiaryPacketHandler();
		req.RemoveDiary(iniDate);

		mAdapter.listData.remove(delePos);
		mAdapter.notifyDataSetChanged();
		if (myDialog != null) {
			myDialog.dismiss();
			myDialog = null;
		}
	}

	/**
	 * Rewrite the Adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private Context context = null;
		private List<DiaryTable> listData = null;

		private ListView mListView;

		private LayoutInflater inflater = null;

		// private int[] DefHeadimageIds = { R.drawable._0002_girl_photo_kuang,
		// R.drawable._0002_girl_photo_kuang, R.drawable._0002_girl_photo_kuang,
		// R.drawable._0003_boy_photo };

		public MyAdapter(Context context, List<DiaryTable> coll) {
			this.context = context;
			this.listData = coll;
			inflater = LayoutInflater.from(context);
			init(context);
		}

		private void init(Context con) {
			myHeadLayerDrawable = CommonBitmap.getInstance().getMyHeadBm();

			tarHeadLayerDrawable = GlobalApplication.getInstance()
					.getTarHeadPicBm();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			DiaryTable entity = listData.get(position);
			ViewHolder holder = null;

			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.customcell, null);
				holder.content = (TextView) view
						.findViewById(R.id.diary_content);
				holder.headphoto = (ImageView) view
						.findViewById(R.id.diary_headphoto);
				holder.New = (ImageView) view.findViewById(R.id.diary_new);
				holder.title = (TextView) view.findViewById(R.id.diary_title);
				holder.name = (TextView) view
						.findViewById(R.id.diary_namelabel);
				holder.date = (TextView) view.findViewById(R.id.diary_date);
				// holder.CommentButton =
				// (ImageView)view.findViewById(R.id.diary_commentButton);
				holder.ModiferButton = (ImageView) view
						.findViewById(R.id.diary_editButton);
				holder.DeleteButton = (ImageView) view
						.findViewById(R.id.diary_deleteButton);
				holder.isSelected = false;
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.account = entity.getAuthor();
			if (holder.account.equals(DiaryActivity.this.userAccount)) {
				holder.isComing = false;
				holder.headphoto.setImageBitmap(myHeadLayerDrawable);
				// holder.CommentButton.setOnClickListener(new
				// MyOnClickListener(3));
				holder.ModiferButton
						.setOnClickListener(new MyOnClickListener(4));
				holder.DeleteButton
						.setOnClickListener(new MyOnClickListener(5));
				holder.name.setText(mySmallName);
			} else {
				holder.isComing = true;
				holder.name.setText(tarSmallName);
				holder.headphoto.setImageBitmap(tarHeadLayerDrawable);
				// holder.CommentButton.setOnClickListener(null);
				holder.ModiferButton.setOnClickListener(null);
				holder.DeleteButton.setOnClickListener(null);

				holder.ModiferButton.setVisibility(View.GONE);
				holder.DeleteButton.setVisibility(View.GONE);
				holder.name.setText(tarSmallName);
			}
			holder.pos = position;
		
			holder.initdate =entity.getInitdate();

			if (entity.getTitle() == null || entity.getTitle().equals(""))
				holder.title.setText("��null��");
			else
				holder.title.setText("��" + entity.getTitle() + "��");

			holder.content.setText(entity.getContent());
			holder.date.setText(AppManagerUtil.transformDisplayDiary(entity.getEditdate()));

			int isNew = entity.getIsnew();
			if (isNew == 1)
				holder.New.setVisibility(View.VISIBLE);
			else
				holder.New.setVisibility(View.GONE);

			return view;
		}

		/**
		 * 
		 * @param itemIndex
		 */
		public void updateView(int itemIndex) {
			// �õ���һ������ʾ�ؼ���λ�ã�
			int visiblePosition = mListView.getFirstVisiblePosition();
			// ֻ�е�Ҫ���µ�view�ڿɼ���λ��ʱ�Ÿ��£����ɼ�ʱ������������
			if (itemIndex - visiblePosition >= 0) {
				// �õ�Ҫ���µ�item��view , it has an invisible view
				View convertView = mListView.getChildAt(itemIndex
						- visiblePosition);
				DiaryTable entity = listData.get(itemIndex);
			}
		}

		public void setListView(ListView listView) {
			this.mListView = listView;
		}

	}

	// ������־ģ��ʱ���Ȱ�֮ǰû�д���
	public void dealWithDiaryWaitForRemove() {

		List<DiaryTable> diaryWaitForRemove = Database.getInstance(
				getApplicationContext()).getWaitForRemoveDiary();
		for (DiaryTable mDt : diaryWaitForRemove) {
			DiaryPacketHandler mDp = new DiaryPacketHandler();
			mDp.RemoveDiary(mDt.getInitdate());
		}

	}

	// ���������ȡ����޸�ʱ��;
	public void getReadLastModifyTimeFromServer() {
		DiaryPacketHandler mDP = new DiaryPacketHandler();
		mDP.getDiaryReadLastModifyTime();
	}

	// ���û�������־����ʱ�򣬶Է����޸Ļ���ɾ����־ʱ�򣬾�Ҫˢ����־���棻
	public void refreshDiary(List<String> record) {
		mAdapter.listData = Database.getInstance(getApplicationContext())
				.getDiary();
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}
		mAdapter.notifyDataSetChanged();

		if (record == null)
			return;

		// ��Ҫ���ж��ǲ����û������������ȡ�ġ�
		if (refreshList != null) {
			if (refreshList.size() < 2) { // ˵���д��ˣ�����һ����2�ı���
				refreshList = null;
			} else {
				if (refreshList.get(0).equals(record.get(0))
						&& refreshList.get(1).equals(record.get(1))) {
					this.refreshList.remove(0);
					this.refreshList.remove(1);

				}
				// ˵�����������ȡ�Ķ��Ѿ�ȫ��������,��Ҫ���������ȡһ����д������޸�ʱ��.
				if (refreshList.size() == 0) {
					refreshList = null;
					DiaryPacketHandler handler = new DiaryPacketHandler();
					handler.GetDiaryLastModifyTime();
					// //ֹͣ�������ӵľջ���
					// UIApplication *app = [UIApplication sharedApplication];
					// if (app.networkActivityIndicatorVisible) {
					// app.networkActivityIndicatorVisible=!app.networkActivityIndicatorVisible;
					// }
				}
			}
		}
	}

	// �������ɾ����־��Ĵ���;
	public void responseForRemoveDiary(String str) {
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}

		// initData();
		// mAdapter.notifyDataSetChanged();
	}

	// ������������־��Ĵ���;
	public void responseForNewDiary(String str) {
		// initData();
		// mAdapter.notifyDataSetChanged();
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}


	}

	// ��������޸���־��Ĵ���
	public void responseForModifyDiary(String str) {
		// initData();
		// mAdapter.notifyDataSetChanged();
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}

	}

	// ����ӷ������õ�������޸�ʱ��;
	public void responseForDiaryLastModifyTime(String str) {
		String time = str.substring(Protocol.HEAD_LEN);
		// Log.v("diary","��������޸�ʱ�� " + lastModifytime);
		// û������޸�ʱ��˵�����״�ʹ�ã��������ϲ�û�м�¼�С�
		if (time.equals("")) {
			return;
		}
		// DiaryHandler *req = [[DiaryHandler alloc]init];
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		String localLastModifyTime = mSP.getString(
				Protocol.PREFERENCE_DiaryLastModifyTime, "0000-00-00-00:00:00");

		if (!(time.equals(localLastModifyTime))) {
			DiaryPacketHandler mDP = new DiaryPacketHandler();
			mDP.GetDiaryTimeList();
		} else { // �������޸�ʱ��һ���Ļ�����ô�ʹ����ط���
			DiaryPacketHandler mDP = new DiaryPacketHandler();
			mDP.dealWithWaitForAddModifyRemoveDiary();
		}
	}

	// �п���ʱ��һ���������û���һ������ֹ������ͬʱд�ļ����ա�
	private boolean isDiaryContain(String editDate, String acc) {
		List<DiaryTable> diarylist = mAdapter.listData;
		for (DiaryTable mDt : diarylist) {
			if (mDt.getEditdate().equals(editDate)
					&& (mDt.getAuthor().equals(acc))) {
				return true;
			}
		}
		return false;

	}

	public boolean isServerTimeListContainsEditDate(String[] timeList,
			String editeDate) {
		for (int index = 2; index < timeList.length; index += 3) {
			if (timeList[index].equals(editeDate)) {
				return true;
			}
		}
		return false;
	}

	// ��Ϊ����������ʱ���б������ǣ��ʺ� ����ʱ�� �޸�ʱ��
	public void responseForDiaryTimeList(String str) {
		String mess = str.substring(Protocol.HEAD_LEN);
		String[] timeList = mess.split(" ");
		boolean isNeedToRefresh = false;
		List<DiaryTable> diarylist = mAdapter.listData;
		// �����ɾ�����ݵĻ�������������ɾ������֮���Ҫȥˢ��һ��ҳ��;
		DiaryPacketHandler mDP = new DiaryPacketHandler();
		Database mDB = Database.getInstance(getApplicationContext());
		// ����Ϊ�գ�˵����������û�������ˣ���ô��Ҫ�������ж���ɾ���ˡ�
		if (timeList.length == 1 && timeList[0].equals("")) {

			for (DiaryTable mDT : diarylist) {
				mDB.removeDiary(mDT.getAuthor(), mDT.getInitdate());

			}
			// ���������ȡһ������д�뱾�ص���־ģ�������޸�ʱ�䣻
			mDP.GetDiaryLastModifyTime();
			reloadData();
			mAdapter.notifyDataSetChanged();
			return;
		}
		if (timeList.length < 3)
			return;

		// �������У�����û�У���Ҫ���������ȡ;
		for (int index = 0; index < timeList.length; index += 3) {
			if (!(this.isDiaryContain(timeList[index + 2], timeList[index]))) {
				isNeedToRefresh = true;
				mDP.GetDiaryWithAccount(timeList[index], timeList[index + 1]);

				if (refreshList == null) {

					refreshList = new ArrayList<String>();
				}
				this.refreshList.add(timeList[index]);
				this.refreshList.add(timeList[index + 1]);

			}
		}

		// �����ɾ�����ݵĻ�������������ɾ������֮���Ҫȥˢ��һ��ҳ��;

		for (int index = 0; index < diarylist.size(); index++) {
			DiaryTable cDT = diarylist.get(index);

			if (!(this.isServerTimeListContainsEditDate(timeList,
					cDT.getEditdate()))) {
				// ˵����֮ǰû�����־���ͳɹ��ģ���ô����Ҫ���·���;
				if (cDT.getServerState().equals(
						Protocol.WaitForServerComfirmAdd + "")) {

					mDP.AddDiary(cDT.getInitdate(), cDT.getTitle(),
							cDT.getContent(), cDT.getEditdate());
				}
				// ˵��֮ǰû���޸���־�ɹ����������ϣ����ھ�Ҫ���·����޸���Ϣ;
				else if (cDT.getServerState().equals(
						Protocol.WaitForServerComfirmModify + "")) {

					mDP.ModifyDiary(cDT.getInitdate(), cDT.getTitle(),
							cDT.getContent(), cDT.getEditdate());
				} else {
					Database.getInstance(getApplicationContext()).removeDiary(
							cDT.getAuthor(), cDT.getInitdate());

				}
				isNeedToRefresh = true;

			}
		}

		if (isNeedToRefresh == true) {
			reloadData();
			mAdapter.notifyDataSetChanged();
		} else {
			// ˵����������ʱ���б��Ѿ��ͱ���һ���ˣ���ô����ȡ��д�޸�ʱ�������Ϊ�˱����û�ɾ�������������ȡ��ʷ����ʱ��û����������д���޸�ʱ�䣬��ɲ��ϳ���new��־��
			mDP.GetDiaryLastModifyTime();
		}
	}

	// �ӷ�����������־��¼�����������֮��ͻ����һ�ν���;
	public void respondsForDiaryOneRecord(List<String> record) {
		// initData();
		// mAdapter.notifyDataSetChanged();
		int isNew = Integer.parseInt(record.get(5));

		DiaryTable mDt = new DiaryTable();
		mDt.setAuthor(record.get(0));
		mDt.setInitdate(record.get(1));
		mDt.setEditdate(record.get(2));
		mDt.setTitle(record.get(3));
		mDt.setContent(record.get(4));
		mDt.setIsnew(isNew);
		mDt.setServerState(record.get(6));
		mAdapter.listData.add(0, mDt);
		mAdapter.notifyDataSetChanged();
		this.mListview.setSelection(0);
		
		if (mAdapter.listData.size() <= 0) {
			this.showNoDiaryView();
		} else {
			this.dismissNoDiaryViewIfExist();
		}
		// ��Ҫ���ж��ǲ����û������������ȡ�ġ�
		if (refreshList != null) {
			if (refreshList.size() < 2) { // ˵���д��ˣ�����һ����2�ı���
				refreshList = null;
			} else {
				if (refreshList.get(0).equals(mDt.getAuthor())
						&& refreshList.get(1).equals(mDt.getInitdate())) {
					this.refreshList.remove(0);
					this.refreshList.remove(0);

				}
				// ˵�����������ȡ�Ķ��Ѿ�ȫ��������,��Ҫ���������ȡһ����д������޸�ʱ��.
				if (refreshList.size() == 0) {
					if (mAdapter.listData.size() <= 0) {
						this.showNoDiaryView();
					} else {
						this.dismissNoDiaryViewIfExist();
					}
					refreshList = null;
					DiaryPacketHandler handler = new DiaryPacketHandler();
					handler.GetDiaryLastModifyTime();
				
				}
			}
		}
	}

	public void processResponse(String str) {
		char operatorCode = 0;
		try {
			operatorCode = (char) (str.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		// Log.v("diary", "diary  operatorCode " + (byte) operatorCode);
		switch (operatorCode) {
		case Protocol.ADD_DIARY_SUCC:
		case Protocol.ADD_DIARY_FAIL:
			responseForNewDiary(str);

			mAdapter.notifyDataSetChanged();
			break;
		case Protocol.MODIFY_DIARY_SUCC:
		case Protocol.MODIFY_DIARY_FAIL:
			responseForModifyDiary(str);

			break;
		case Protocol.REMOVE_DIARY_SUCC:
		case Protocol.REMOVE_DIARY_FAIL:
			responseForRemoveDiary(str);

			break;
		case Protocol.RETURN_DIARY_READ_LAST_MODIFY_TIME:
			responseForDiaryLastModifyTime(str);
			break;

		case Protocol.RETURN_DIARY_TIME_LIST:
//			Log.v("diary", "����ʱ���б�");
			responseForDiaryTimeList(str);
			break;
		default:
			break;
		}

	}

	// ����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ,�����Ҫ����һ����ʱ����ÿ��1����һ�Ρ�
	private void refreshFromServer() {
		if (SelfInfo.getInstance().isOnline()) {
			// ���������ȡ����޸�ʱ��,������ص�����޸�ʱ��ͷ������Ĳ�һ���Ļ�����Ҫ���������ȡʱ���б��Ա���־;
			getReadLastModifyTimeFromServer();
		} else {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}
			this.connectTimer = new Timer();
			TimerTask mTimerTask = new TimerTask() {

				@Override
				public void run() {
					DiaryActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							sendInfoToServer();

						}
					});

				}

			};
			connectTimer.schedule(mTimerTask, 1L, 1000L);

		}

	}

	// ����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ
	public void sendInfoToServer() {
		if (SelfInfo.getInstance().isOnline()) {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}

			// ���������ȡ����޸�ʱ��,������ص�����޸�ʱ��ͷ������Ĳ�һ���Ļ�����Ҫ���������ȡʱ���б��Ա���־;
			getReadLastModifyTimeFromServer();

		}
	}

	public void showNoDiaryView() {
		if (dairyMainRL == null)
			dairyMainRL = (RelativeLayout) this.findViewById(R.id.diary_main);
		dairyMainRL.setBackgroundResource(R.drawable.diary_nodiary_40h);
	}

	public void dismissNoDiaryViewIfExist() {
		if (dairyMainRL == null)
			dairyMainRL = (RelativeLayout) this.findViewById(R.id.diary_main);
		dairyMainRL.setBackgroundResource(R.drawable.diary_background2);
	}

	// -------------------------------------------------------------------------------------------------------------
	public class MyReceiver extends BroadcastReceiver {
		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// Log.v("diary", "@@##on receiver " + action);
			if (Protocol.ACTION_DIARYPACKET.equals(action)) {
				// �����û����ݰ� ����,������Ϣ,��handler���½���

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);

				msg.what = HANDLE_RESPON;
				mHandler.sendMessage(msg);

			}
			if (Protocol.ACTION_DIARYPACKET_READLASTMODIFYTIME.equals(action)) {
				String data = intent.getStringExtra(Protocol.EXTRA_DATA);
				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);

				msg.what = RETRUEN_READLASTMODIFYTIME;
				mHandler.sendMessage(msg);

			}
			if (Protocol.ACTION_DIARYPACKET_ONERECORD.equals(action)) {

				List<String> serializableExtra = null;
				try {
					serializableExtra = ((List<String>) intent
							.getSerializableExtra(Protocol.EXTRA_DATA));
				} catch (Exception e) {
					serializableExtra = null;
					e.printStackTrace();
				}

				respondsForDiaryOneRecord(serializableExtra);
			}
			if (Protocol.ACTION_DIARYPACKET_REFRESH.equals(action)) {
				List<String> serializableExtra = (List<String>) intent
						.getSerializableExtra(Protocol.EXTRA_DATA);
				refreshDiary(serializableExtra);
			}

		} // onReceive
	}

	private static class MyHandler extends Handler {
		WeakReference<DiaryActivity> mActivity;

		MyHandler(DiaryActivity diaryActivity) {
			mActivity = new WeakReference<DiaryActivity>(diaryActivity);
		}

		@Override
		public void handleMessage(Message msg) {

			DiaryActivity theActivity = mActivity.get();
			String mData = msg.getData().getString("data");
			switch (msg.what) {

			case HANDLE_RESPON:
				theActivity.processResponse(mData);
				break;
			case RETRUEN_READLASTMODIFYTIME:
				theActivity.responseForDiaryLastModifyTime(mData);
				break;
			default:
				break;
			}
		}
	}

	private MyReceiver receiver = null;
	private MyHandler mHandler = new MyHandler(this);
	private AlertDialog myDialog = null;

}
