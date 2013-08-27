package com.kpi.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.kpi.android.R;
import com.kpi.android.googleMap.mapItem;

public class MapGoogleActivity extends MapActivity {

	private static final String TAG = "MapGoogleView";

	private Button mBtnMode;
	private Button mBtnFilter;
	private boolean checkedItems[] = new boolean[5];
	private boolean mMode = false;
	private MapView mapView;
	private MapController mc;
	private List<Overlay> mapOverlays;
	private Bitmap kpiIcon;
	private Projection projection;
	private mapItem itemAr[];
	private KPIActivity parent;
	private static int zoomValue = 14;
	private Boolean initiated = false;
	private Resources r;

	public static final String NAVIGATION_POINT = "NAVIGATION_POINT";
	private static final int DIALOG_FITER = 1;

	private int mZoomMin = 12;
	private int mZoomMax = 15;

	private ArrayList<MapItem> mOverlays = new ArrayList<MapItem>();

	GeoPoint p;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_google_view);

		mBtnMode = (Button) findViewById(R.id.btnMode);
		mBtnMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeMapMode();
			}
		});

		mBtnFilter = (Button) findViewById(R.id.btnFilter);
		mBtnFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_FITER);
			}
		});

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(mMode);

		r = this.getResources();

		// Show All building by default
		for (int i = 0; i < checkedItems.length; i++) {
			checkedItems[i] = true;
		}

		parent = (KPIActivity) getParent();

		projection = mapView.getProjection();
		mc = mapView.getController();
		kpiIcon = BitmapFactory.decodeResource(r, R.drawable.icon);

		// mapOverlays = mapView.getOverlays();
		// mapOverlays.add(new MapOverlay());

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = r.getDrawable(R.drawable.map_marker);
		MapItemOverlay itemOverlay = new MapItemOverlay(drawable, this);

		loadFaculties(itemOverlay);
		mapOverlays.add(itemOverlay);

		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (initiated) {
			init();
		} else {
			initiated = true;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_FITER:
			return new AlertDialog.Builder(MapGoogleActivity.this)
					.setTitle(R.string.googlemap_filter)
					.setMultiChoiceItems(R.array.googlemap_buildings,
							checkedItems,
							new DialogInterface.OnMultiChoiceClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {

									checkedItems[which] = isChecked;
									// Redraw Buildings
									updateMarks();
								}
							}).setPositiveButton("Ok", null).create();
		}
		return null;
	}

	public void init() {
		switch (parent.mapTask) {
		case 0:
			navigateToPoint(parent.naviPoint);
			break;
		case 1:
			navigaleToMapItem(parent.mapItemType, parent.mapItemIndex);
			break;
		}
		if (parent.mapTask != -1) {
			parent.mapTask = -1;
		}
		mapView.invalidate();
	}

	public void navigateToPoint(int[] point) {
		mc.setZoom(point[2]);
		mc.animateTo(new GeoPoint(point[0], point[1]));
	}

	public void navigaleToMapItem(int itemType, String itemIndex) {
		mapItem item = findItem(itemType, itemIndex);

		if (item != null) {
			mc.animateTo(item.gp);
			mc.setZoom(mZoomMax);
		} else {
			navigateToPoint(parent.naviPoint);
		}
	}

	public mapItem findItem(int itemType, String itemIndex) {
		int i;

		for (i = 0; i < itemAr.length; i++) {
			if (itemAr[i].type == itemType
					&& itemAr[i].index.contentEquals(itemIndex)) {
				return itemAr[i];
			}
		}

		return null;
	}

	protected void loadFaculties(MapItemOverlay itemOverlay) {
		Resources r = getResources();
		String itAr[][] = getItemArFromString(r
				.getStringArray(R.array.map_items));
		// Log.d("", ""+itAr[0][0]+" "+itAr[0][1]+" "+itAr[0][2]);
		// GeoPoint itemsGP[] = parceToGP(itAr[1]);
		// String itemsIndex[] = itAr[2];
		// String itemsType[] = itAr[0];

		itemAr = new mapItem[itAr.length];

		int i;
		MapItem overlayitem;

		Drawable korpIcon = this.getResources().getDrawable(
				R.drawable.map_marker);
		korpIcon.setBounds(-korpIcon.getIntrinsicWidth() / 2,
				-korpIcon.getIntrinsicHeight() / 2,
				korpIcon.getIntrinsicWidth() / 2,
				korpIcon.getIntrinsicHeight() / 2);

		Drawable homeIcon = this.getResources().getDrawable(
				R.drawable.map_marker_home);
		homeIcon.setBounds(-homeIcon.getIntrinsicWidth() / 2,
				-homeIcon.getIntrinsicHeight() / 2,
				homeIcon.getIntrinsicWidth() / 2,
				homeIcon.getIntrinsicHeight() / 2);

		for (i = 0; i < itAr.length; i++) {
			itemAr[i] = new mapItem();
			itemAr[i].gp = parceToGP(itAr[i][1]);
			itemAr[i].index = itAr[i][2];
			itemAr[i].type = Integer.parseInt(itAr[i][0]);

			overlayitem = new MapItem(itemAr[i].gp, itemAr[i].index, "");
			itemOverlay.addOverlay(overlayitem);

			itemAr[i].overlay = itemOverlay.mOverlays.get(itemOverlay.mOverlays
					.size() - 1);
			switch (itemAr[i].type) {
			case 0:
				itemAr[i].overlay.setMarker(korpIcon);
				break;
			case 1:
				itemAr[i].overlay.setMarker(homeIcon);
				break;
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
			zoomValue = zoomValue + 1;
			mc.setZoom(zoomValue);
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
			zoomValue = zoomValue - 1;
			mc.setZoom(zoomValue);
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
			mMode = false;
			changeMapMode();
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
			mMode = true;
			changeMapMode();
		}
		return super.dispatchKeyEvent(event);
	}

	public String[][] getItemArFromString(String[] StrAr) {
		int i;
		String[][] Ar = new String[StrAr.length][3];

		for (i = 0; i < Ar.length; i++) {
			Ar[i] = StrAr[i].split("; ");
		}
		return Ar;
	}

	public GeoPoint parceToGP(String Str) {
		String s[];
		GeoPoint gp;

		s = Str.split(", ");
		gp = new GeoPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));

		return gp;
	}

	public GeoPoint[] parceToGPAr(String[] StrAr) {
		GeoPoint arGP[] = new GeoPoint[StrAr.length];

		int i;
		String[] s;
		Point gp = new Point();
		Float f;

		for (i = 0; i < StrAr.length; i++) {
			s = StrAr[i].split(", ");
			gp.x = Integer.parseInt(s[0]);
			gp.y = Integer.parseInt(s[1]);
			arGP[i] = new GeoPoint(gp.x, gp.y);
		}

		return arGP;
	}

	/**
	 * Chage View Mode Map
	 */
	private void changeMapMode() {
		mMode = !mMode;
		if (mMode) {
			mBtnMode.setText(R.string.googlemap_sattelite);
		} else {
			mBtnMode.setText(R.string.googlemap_map);
		}
		mapView.setSatellite(mMode);
	}

	private void updateMarks() {

	}

	class MapItem extends OverlayItem {

		public MapItem(GeoPoint arg0, String arg1, String arg2) {
			super(arg0, arg1, arg2);
			// TODO Auto-generated constructor stub
		}
		/*
		 * @Override public Drawable getMarker(int stateBitset){ Drawable
		 * drawable = getResources().getDrawable(R.drawable.map_marker);
		 * 
		 * Log.d("", "" + drawable.toString());
		 * 
		 * return drawable; }
		 */

	}

	class MapItemOverlay extends ItemizedOverlay {
		private ArrayList<MapItem> mOverlays = new ArrayList<MapItem>();
		private Context mContext;

		public MapItemOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		public MapItemOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		public void addOverlay(MapItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		@Override
		protected MapItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		@Override
		protected boolean onTap(int index) {
			MapItem item = mOverlays.get(index);

			navigateToPoint(new int[] { itemAr[index].gp.getLatitudeE6(),
					itemAr[index].gp.getLongitudeE6(), 18 });

			return true;
		}

		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			int cZoom = mapView.getZoomLevel();
			GeoPoint kpGP = parceToGP(r.getString(R.string.kpiPoint));
			//Log.i(TAG, "Zoom=" + cZoom);
			
			if (cZoom > mZoomMin) {

				GeoPoint[] kpiZone0 = parceToGPAr(r
						.getStringArray(R.array.kpiZone0));
				
				Paint mPaint = new Paint();
				mPaint.setDither(true);
				mPaint.setColor(0xff0962f5);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setStrokeJoin(Paint.Join.ROUND);
				mPaint.setStrokeCap(Paint.Cap.ROUND);
				mPaint.setStrokeWidth(2);
				drawFigure(mPaint, canvas, shadow, kpiZone0);

				if (cZoom > mZoomMax) {
					super.draw(canvas, mapv, false);

					int i;
					Point p = new Point();
					Paint tPaint = new Paint();

					tPaint.setDither(true);
					tPaint.setColor(Color.BLACK);
					tPaint.setStyle(Paint.Style.FILL);
					tPaint.setFakeBoldText(true);
					tPaint.setTextAlign(Align.CENTER);
					tPaint.setTextSize(14);

					for (mapItem item : itemAr) {
						projection.toPixels(item.gp, p);
						canvas.drawText(item.index, p.x, p.y, tPaint);
					}
				} else { // Draw only KPI Area
					int i;
					Point p = new Point();
					Paint tPaint = new Paint();

					tPaint.setDither(true);
					tPaint.setColor(0xff0962f5);
					tPaint.setStyle(Paint.Style.FILL);
					tPaint.setFakeBoldText(true);
					tPaint.setTextAlign(Align.CENTER);
					tPaint.setTextSize(18);

					projection.toPixels(kpGP, p);
					canvas.drawText("KPI",p.x,p.y,tPaint);
					canvas.drawBitmap(kpiIcon, p.x-kpiIcon.getWidth()/2, p.y-kpiIcon.getHeight()/2, null);

				}
			}else{
				Point p = new Point();
				projection.toPixels(kpGP, p);
				canvas.drawBitmap(kpiIcon, p.x-kpiIcon.getWidth()/2, p.y-kpiIcon.getHeight()/2, null);
			}
			
			
		}

		/**
		 * 
		 * @param mPaint
		 * @param canvas
		 * @param shadow
		 * @param pointAr
		 */
		public void drawFigure(Paint mPaint, Canvas canvas, boolean shadow,
				GeoPoint[] pointAr) {
			int i;

			for (i = 0; i < pointAr.length - 1; i++) {
				drawLine(mPaint, canvas, shadow, pointAr[i], pointAr[i + 1]);
			}
			drawLine(mPaint, canvas, shadow, pointAr[pointAr.length - 1],
					pointAr[0]);
		}

		/**
		 * 
		 * @param mPaint
		 * @param canvas
		 * @param shadow
		 * @param gP1
		 * @param gP2
		 */
		public void drawLine(Paint mPaint, Canvas canvas, boolean shadow,
				GeoPoint gP1, GeoPoint gP2) {

			Point p1 = new Point();
			Point p2 = new Point();

			Path path = new Path();

			projection.toPixels(gP1, p1);
			projection.toPixels(gP2, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			canvas.drawPath(path, mPaint);
		}
	}
}

// Related Links
// http://stackoverflow.com/questions/2176397/want-to-draw-a-line-or-path-on-google-map-in-hellomapview
// http://blogoscoped.com/archive/2008-12-15-n14.html
// http://www.stageco.com/upload/product/16.pdf
// http://groups.google.com/group/android-beginners/browse_thread/thread/57f4f547e339d23c
// TRY 2 -WORK from
// http://mobiforge.mobi/developing/story/using-google-maps-android?dm_switcher=true
// http://mobiforge.com/developing/story/using-google-maps-android