package adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.com.evknz.vol.free.R;
import model.Item;

public class ItemSelectionAdapter extends ArrayAdapter<Item> {

    List<Item> lista = null;
	Context context = null;

    static class ViewHolder {
		TextView tvitem;
        ImageView ivcheck;
	}
	
	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

	public ItemSelectionAdapter(Context context, int resource, int textViewResourceId, List<Item> lista) {
		super(context, resource, textViewResourceId, lista);
		this.lista = lista;
		this.context = context;
	}

	public void setNewSelection(int position, boolean value) {
		mSelection.put(position, value);
		notifyDataSetChanged();
	}

	public boolean isPositionChecked(int position) {
		Boolean result = mSelection.get(position) != null;
		return result == null ? false : result;
	}

	public Set<Integer> getCurrentCheckedPosition() {
		return mSelection.keySet();
	}

	public void removeSelection(int position) {
		mSelection.remove(position);
		notifyDataSetChanged();
	}

	public void clearSelection() {
		mSelection = new HashMap<Integer, Boolean>();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.lista_items_adapter, parent, false);

			holder.tvitem = (TextView) convertView.findViewById(R.id.tvItem);
            holder.ivcheck = (ImageView) convertView.findViewById(R.id.ivCheck);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Item item = this.lista.get(position);
		holder.tvitem.setText("- " + item.getItem());

        if (item.getDone() == 1){
            //holder.tvitem.setBackgroundColor(convertView.getResources().getColor(R.color.custom_background_warning_item));
            holder.tvitem.setTextColor(convertView.getResources().getColor(R.color.custom_background_gray_item));
            holder.tvitem.setPaintFlags(holder.tvitem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.ivcheck.setVisibility(View.VISIBLE);
        }else{
            //holder.tvitem.setBackgroundColor(convertView.getResources().getColor(android.R.color.transparent));
            holder.tvitem.setTextColor(convertView.getResources().getColor(R.color.custom_background_normal_item));
            holder.tvitem.setPaintFlags(holder.tvitem.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.ivcheck.setVisibility(View.GONE);
        }

		// let the adapter handle setting up the row views default color
		convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		if (mSelection.get(lista.get(position).getId()) != null) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.custom_background_selected_item));
		}

		return convertView;
	}

}
