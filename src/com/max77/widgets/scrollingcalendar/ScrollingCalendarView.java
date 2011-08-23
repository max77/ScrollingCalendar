package com.max77.widgets.scrollingcalendar;

import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Календарь.
 * @author max77
 *
 */
public class ScrollingCalendarView extends GridView {
	public Calendar EPOCH_START;
	private static final int END_OF_DAYS = 1000000;

	private int mCellsPerRow;
	
	private int mCellResource;
	
	private Drawable mDrawableNormal;
//	private Drawable mDrawableToday;
//	private Drawable mDrawableWeekend;
	private Drawable mDrawablePast;
//	private Drawable mDrawableNextMonth;
	private Drawable mDrawableSelectedStart;
	private Drawable mDrawableSelectedRange;
	
	private int mColorTextNormal;
	private int mColorTextPast;
//	private int mColorTextThisMonth;
//	private int mColorTextNextMonth;
	private int mColorTextEvenMonth;
	private int mColorTextOddMonth;

	private Calendar mToday;
	private Calendar mSelectionStart;
	private Calendar mSelectionEnd;
	
	private OnDateSelectionListener mListener = null;

	/*
	 * 
	 * 
	 */
	
	private class DateViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		DateViewAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return END_OF_DAYS;
		}

		@Override
		public Object getItem(int position) {
			return positionToCalendar(position);
		}

		/**
		 * Вычисляет дату по позиции элемента сетки
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			TextView tvDay;
			TextView tvMonth;
//			TextView tvExtra;
			LinearLayout layoutCell;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if(mCellResource == 0)
				return null;
			
            if (convertView == null) {
            	convertView = mInflater.inflate(mCellResource, null);
            	
            	holder = new ViewHolder();
            	
            	holder.tvDay = (TextView) convertView.findViewById(android.R.id.text1);
            	holder.tvMonth = (TextView) convertView.findViewById(android.R.id.text2);
            	holder.layoutCell = (LinearLayout) convertView.findViewById(android.R.id.background);
            	
            	convertView.setTag(holder);
	        } else {
	        	holder = (ViewHolder) convertView.getTag();
	        }
	        
            Calendar cal = (Calendar) getItem(position);
	        
            holder.tvDay.setText(DateFormat.format("d", cal));
	        holder.tvMonth.setText(DateFormat.format("MMM", cal));

	        Drawable drawable = null;
        	int colorDay = mColorTextNormal;
//        	int colorMonth = mColorTextThisMonth;
          	int colorMonth = cal.get(Calendar.MONTH) % 2 == 1 ? mColorTextOddMonth : mColorTextEvenMonth;
//	        int day = cal.get(Calendar.DAY_OF_WEEK);

	        if(mSelectionStart != null && mSelectionEnd == null && mSelectionStart.equals(cal))
	        	drawable = mDrawableSelectedStart;
	        else if(mSelectionStart != null && mSelectionEnd != null && 
	        		(mSelectionStart.before(cal) || mSelectionStart.equals(cal)) && 
	        		(mSelectionEnd.after(cal) || mSelectionEnd.equals(cal)))
	        	drawable = mDrawableSelectedRange;
//	        else if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
//	        	drawable = mDrawableWeekend;
//	        else if(cal.get(Calendar.YEAR) > mToday.get(Calendar.YEAR) ||
//	        		(cal.get(Calendar.YEAR) == mToday.get(Calendar.YEAR) && cal.get(Calendar.MONTH) > mToday.get(Calendar.MONTH))) {
//	        	drawable = mDrawableNextMonth;
//	        	colorMonth = mColorTextNextMonth;
//	        }
	        else if(cal.before(mToday)) {
	        	drawable = mDrawablePast;
	        	colorDay = mColorTextPast;
	        	colorMonth = mColorTextPast;
	        }
//	        else if(cal.equals(mToday)) {
//	        	drawable = mDrawableToday;
//	        }
	        else {
	        	drawable = mDrawableNormal;
	        }
	        
	        holder.layoutCell.setBackgroundDrawable(drawable);
	        holder.tvDay.setTextColor(colorDay);
	        holder.tvMonth.setTextColor(colorMonth);

	        return convertView;
		}
		
	}
	
	public interface OnDateSelectionListener {
		void onDateSelected(Calendar cal);
	}

	/*
	 * 
	 * 
	 */
	
	private Calendar positionToCalendar(int position) {
		Calendar cal = (Calendar) EPOCH_START.clone();
		cal.add(Calendar.DAY_OF_YEAR, position);
		
		return cal;
	}
	
	private int calendarToPosition(Calendar cal) {
		Calendar tmp = (Calendar) EPOCH_START.clone();
		int days = 0;

		while(tmp.before(cal)) {
			tmp.add(Calendar.DAY_OF_YEAR, 1);
			days ++;
		}
		
		return days - 1;
	}
	
	private void truncDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	private void setup(Context context) {
		setAdapter(new DateViewAdapter(context));
		setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if(mListener != null)
					mListener.onDateSelected((Calendar) ((Calendar) getItemAtPosition(position)).clone());
			}
		});
		
		setGravity(Gravity.CENTER);
		setHorizontalSpacing(0);
		setVerticalSpacing(0);
		setVerticalScrollBarEnabled(false);
		setStretchMode(STRETCH_COLUMN_WIDTH);
		setSelector(new BitmapDrawable());
		
		EPOCH_START = Calendar.getInstance();
		EPOCH_START.set(2000, 0, 3, 0, 0, 0);
		EPOCH_START.set(Calendar.MILLISECOND, 0);

		mToday = Calendar.getInstance();
	}
	
	
	public ScrollingCalendarView(Context context) {
		super(context);
		setup(context);
	}

	public ScrollingCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	public ScrollingCalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
	}

	@Override
	public void setColumnWidth(int columnWidth) {
	}
	
	@Override
	public void setNumColumns(int numColumns) {
		mCellsPerRow = numColumns;
		
		super.setNumColumns(numColumns);
	}
	
	
	public void setCellLayout(int cellLayoutId) {
		mCellResource = cellLayoutId;
	}
	
	public void setCellBackgroundDrawables(Drawable normal, Drawable past, Drawable selectedStart, Drawable selectedRange) {
//		int len = arr.length;
		
		mDrawableNormal = normal;
//		mDrawableToday = len > 1 ? arr[1] : arr[0];
//		mDrawableWeekend = len > 2 ? arr[2] : arr[0];
		mDrawablePast = past;
//		mDrawableNextMonth = len > 4 ? arr[4] : arr[0];
		mDrawableSelectedStart = selectedStart;
		mDrawableSelectedRange = selectedRange == null ? selectedStart : selectedRange;
	}

	public void setCellTextColors(int textNormal, int textPast, int textEvenMonth, int textOddMonth) {
//		int len = arr.length;
		
		mColorTextNormal = textNormal;
		mColorTextPast = textPast;
//		mColorTextThisMonth = len > 2 ? arr[2] : arr[0];
//		mColorTextNextMonth = len > 3 ? arr[3] : arr[0];
		mColorTextEvenMonth = textEvenMonth;
		mColorTextOddMonth = textOddMonth;
	}
	
	public void setOnDateSelectionListener(OnDateSelectionListener l) {
		mListener = l;
	}

	/*
	 * 
	 */
	
	public void gotoDate(Calendar cal) {
		int pos = calendarToPosition(cal);
		
		if(pos < 0)
			pos = 0;
		else if(pos >= END_OF_DAYS)
			pos = END_OF_DAYS - 1;
		
		setSelection(pos - mCellsPerRow);
	}
	
	public void setToday(Calendar today) {
		mToday = (Calendar) today.clone();
		truncDate(mToday);
	}
	
	public void setSelectionStart(Calendar cal) {
		if(cal == null)
			mSelectionStart = null;
		else {
			mSelectionStart = (Calendar) cal.clone();
			truncDate(mSelectionStart);
		}
			
		mSelectionEnd = null;
	}
	
	public void setSelectionEnd(Calendar cal) {
		if(mSelectionStart != null) {
			mSelectionEnd = (Calendar) cal.clone();
			truncDate(mSelectionEnd);
		}

		if(mSelectionEnd.before(mSelectionStart)) {
			Calendar tmp = mSelectionEnd;
			mSelectionEnd = mSelectionStart;
			mSelectionStart = tmp;
		}
	}
	
	public void clearSelection() {
		mSelectionStart = mSelectionEnd = null;
	}
}
