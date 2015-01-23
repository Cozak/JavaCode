package com.minus.lovershouse.face;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minus.lovershouse.R;
import com.minus.lovershouse.face.FaceAdapter.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class EmoticonConversionUtil {
    private static String[] mEmoticons = {
		"(/ﾟДﾟ)/",
//      "(;¬_¬)",
        "(≧σ≦)",
        "o(-`д´- ｡)",
        "(¬_¬)ﾉ",
//        "(︶︹︺)",
        "(≧０≦)",
        "(=ﾟωﾟ)ﾉ",
        "(´-ω-｀)",
        "┗(･ω･;)┛",
        "(* >ω<)",
        "(*´ω｀)o",
        "m(￢0￢)m",
        "(￣(エ)￣)",
        "(=^-ω-^=)",
        "(´･_･`)",
        "(◎_◎;)",
        "( ・◇・)？",
        "┏(＾0＾)┛",
        "(ノ￣ω￣)ノ",
        "(o´･_･)っ",
        "(×_×;）",
		"（Ω_Ω）",
		"~(>_<。)＼",
		"ﾍ(￣ ￣;ﾍ)",
		"( -。-)" ,
		"(｡･｀ω´･｡)",
		"(｀Д´*)",
		"ψ(*｀ー´)ψ",
		"(●´∀｀●)",
		"⊂((・▽・))⊃",
		"（￣ー￣）",
		"∩( ・ω・)∩",
		"ヽ(*≧ω≦)ﾉ",
		"(ﾉ´ｰ`)ﾉ",
		"o(≧∇≦o)",
		"( ～'ω')～",
		"⊙ω⊙",
		"(・ε・)",
		"(ゝ∀･)",
		"（ΦωΦ）",
		"（＠´＿｀＠）",
		"(づ￣ ³￣)づ",
		"┐(‘～`；)┌",
		"~(>_<。)＼",
		"(￣（∞）￣)",
		"(Ｔ▽Ｔ)",
		"╥﹏╥",
		"(´；д；`)",
		//too long             "｡･ﾟﾟ･(>д<;)･ﾟﾟ･｡",
		"( ≧Д≦)",
		"p(´⌒｀｡q)",
		"⊙︿⊙",
		"(O_O；)",
		"щ(゜ロ゜щ)",
		"(⌒_⌒;)",
		"(*ﾉωﾉ)",
		"(ﾉ´▽｀)ﾉ♪",
		"(´～`)",
		"(。-ω-)zzz",
		"o(´^｀)o",
		"⊙﹏⊙",
		"(;° ロ°)",
		"φ(．．;)",
    };

	/** 每一页表情的个数 */
	private int pageSize = 12;

	private static EmoticonConversionUtil mConversionUtil;

	/** 表情分页的结果集合 */
	public  List<List<String>> emoticonsList;

	public static EmoticonConversionUtil getInstace() {
		if (mConversionUtil == null) {
			mConversionUtil = new EmoticonConversionUtil();
		}
		return mConversionUtil;
	}
	
	private EmoticonConversionUtil() {
		emoticonsList = new ArrayList<List<String>>();
		for (int i = 0; i < mEmoticons.length; i += pageSize) {
			List<String> emoticons = new ArrayList<String>();
			for (int j = 0; j < pageSize && i + j < mEmoticons.length; ++j) {
				emoticons.add(mEmoticons[i + j]);
			}
			emoticonsList.add(emoticons);
		}
	}
	
	public List<List<String>> getEmoticonsList() {
		return emoticonsList;
	}
}

public class EmoticonAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater inflater;
    private List<String> mList;

    public EmoticonAdapter(Context context, List<String> list) {
    	mContext = context;
    	 this.inflater=LayoutInflater.from(context);
        this.mList = list;
    }

    @Override
    public int getCount() {
    	return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.emoticons, null);
            viewHolder.tv_emoicon=(TextView)convertView.findViewById(R.id.tv_em);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.tv_emoicon.setText(mList.get(position));
        
        return convertView;
	}
	 class ViewHolder {

	        public TextView tv_emoicon;
	    }
}
